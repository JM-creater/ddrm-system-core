package ph.gov.tabunok.drrm.functions.incident.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import ph.gov.tabunok.drrm.config.AppProperties;
import ph.gov.tabunok.drrm.exception.ApiException;
import ph.gov.tabunok.drrm.functions.incident.entity.MediaKind;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.checksums.RequestChecksumCalculation;
import software.amazon.awssdk.core.checksums.ResponseChecksumValidation;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Slf4j
@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final Map<String, String> PHOTO_TYPES = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp",
            "image/gif", "gif");

    private static final Map<String, String> VIDEO_TYPES = Map.of(
            "video/mp4", "mp4",
            "video/webm", "webm",
            "video/quicktime", "mov",
            "video/3gpp", "3gp");

    private final Path root;
    private final boolean useS3;
    private final String bucket;
    private final String publicUrl;
    private final long presignMinutes;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    public FileStorageServiceImpl(AppProperties properties) {
        this.root = Path.of(properties.getUploadDir()).toAbsolutePath().normalize();
        AppProperties.S3 s3 = properties.getS3();
        this.useS3 = s3 != null && s3.isConfigured();

        if (this.useS3) {
            this.bucket = s3.getBucket().trim();
            this.publicUrl = StringUtils.hasText(s3.getPublicUrl())
                    ? s3.getPublicUrl().trim().replaceAll("/+$", "")
                    : null;
            this.presignMinutes = Math.min(Math.max(s3.getPresignMinutes(), 1L), 10080L);

            URI endpointUri = normalizeEndpoint(s3.getEndpoint(), this.bucket);
            String regionName = StringUtils.hasText(s3.getRegion()) ? s3.getRegion().trim() : "auto";
            Region region = Region.of(regionName);
            StaticCredentialsProvider credentials = StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(s3.getAccessKey().trim(), s3.getSecretKey().trim()));
            S3Configuration serviceConfig = S3Configuration.builder()
                    .pathStyleAccessEnabled(s3.isPathStyleAccess())
                    .build();

            this.s3Client = S3Client.builder()
                    .endpointOverride(endpointUri)
                    .region(region)
                    .credentialsProvider(credentials)
                    .serviceConfiguration(serviceConfig)
                    .requestChecksumCalculation(RequestChecksumCalculation.WHEN_REQUIRED)
                    .responseChecksumValidation(ResponseChecksumValidation.WHEN_REQUIRED)
                    .build();

            this.s3Presigner = S3Presigner.builder()
                    .endpointOverride(endpointUri)
                    .region(region)
                    .credentialsProvider(credentials)
                    .serviceConfiguration(serviceConfig)
                    .build();

            log.info("Using Railway S3-compatible bucket '{}' at {}", this.bucket, endpointUri);
        } else {
            this.bucket = null;
            this.publicUrl = null;
            this.presignMinutes = 0L;
            this.s3Client = null;
            this.s3Presigner = null;
            try {
                Files.createDirectories(root.resolve("incidents"));
            } catch (IOException e) {
                throw new IllegalStateException("Cannot create upload directory " + root, e);
            }
            log.info("S3 bucket not configured; using local file storage at {}", this.root);
        }
    }

    private static URI normalizeEndpoint(String rawEndpoint, String bucketName) {
        String ep = rawEndpoint.trim().replaceAll("/+$", "");
        if (!ep.startsWith("http://") && !ep.startsWith("https://")) {
            ep = "https://" + ep;
        }
        URI uri = URI.create(ep);
        String host = uri.getHost();
        if (host != null && StringUtils.hasText(bucketName)
                && host.toLowerCase().startsWith(bucketName.toLowerCase() + ".")) {
            String baseHost = host.substring(bucketName.length() + 1);
            int port = uri.getPort();
            ep = uri.getScheme() + "://" + baseHost + (port > 0 ? ":" + port : "");
            return URI.create(ep);
        }
        return uri;
    }

    @PreDestroy
    public void close() {
        if (s3Presigner != null) {
            s3Presigner.close();
        }
        if (s3Client != null) {
            s3Client.close();
        }
    }

    @Override
    public StoredFile store(MultipartFile file, MediaKind kind) {
        String type = file.getContentType() == null ? "" : file.getContentType().toLowerCase();
        Map<String, String> allowed = kind == MediaKind.PHOTO ? PHOTO_TYPES : VIDEO_TYPES;
        String ext = allowed.get(type);
        if (ext == null) {
            throw ApiException.badRequest(kind == MediaKind.PHOTO
                    ? "Photos must be JPG, PNG, WEBP or GIF."
                    : "Videos must be MP4, WEBM, MOV or 3GP.");
        }
        long max = kind == MediaKind.PHOTO ? MAX_PHOTO_BYTES : MAX_VIDEO_BYTES;
        if (file.getSize() > max) {
            throw ApiException.badRequest(kind == MediaKind.PHOTO
                    ? "Each photo must be 8 MB or less."
                    : "The video must be 25 MB or less.");
        }
        String relative = "incidents/" + UUID.randomUUID() + "." + ext;
        if (useS3) {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(relative)
                    .contentType(type)
                    .contentLength(file.getSize())
                    .build();
            try (InputStream in = file.getInputStream()) {
                s3Client.putObject(putRequest, RequestBody.fromInputStream(in, file.getSize()));
            } catch (Exception e) {
                throw new IllegalStateException("Could not upload file to S3 bucket", e);
            }
        } else {
            Path target = root.resolve(relative).normalize();
            try (InputStream in = file.getInputStream()) {
                Files.createDirectories(target.getParent());
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new IllegalStateException("Could not save uploaded file", e);
            }
        }
        return new StoredFile(relative, type, file.getSize());
    }

    @Override
    public String resolveUrl(String relativePath) {
        if (!StringUtils.hasText(relativePath)) {
            return "";
        }
        if (relativePath.startsWith("http://") || relativePath.startsWith("https://")) {
            return relativePath;
        }
        String clean = relativePath.replace('\\', '/').replaceAll("^/+", "");
        if (useS3) {
            Path localTarget = root.resolve(clean).normalize();
            if (localTarget.startsWith(root) && Files.isRegularFile(localTarget)) {
                return "/uploads/" + clean;
            }
            if (publicUrl != null) {
                return publicUrl + "/" + clean;
            }
            try {
                GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(clean)
                        .build();
                GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(presignMinutes))
                        .getObjectRequest(getObjectRequest)
                        .build();
                return s3Presigner.presignGetObject(presignRequest).url().toString();
            } catch (Exception e) {
                log.warn("Could not presign S3 URL for {}: {}", clean, e.getMessage());
                return "/uploads/" + clean;
            }
        }
        return "/uploads/" + clean;
    }

    @Override
    public void deleteQuietly(String relativePath) {
        if (!StringUtils.hasText(relativePath)) return;
        String clean = relativePath.replace('\\', '/').replaceAll("^/+", "");
        if (useS3) {
            try {
                s3Client.deleteObject(DeleteObjectRequest.builder()
                        .bucket(bucket)
                        .key(clean)
                        .build());
            } catch (Exception e) {
                log.warn("Could not delete S3 object {}: {}", clean, e.getMessage());
            }
        }
        Path target = root.resolve(clean).normalize();
        if (!target.startsWith(root)) return;
        try {
            Files.deleteIfExists(target);
        } catch (IOException e) {
            log.warn("Could not delete {}: {}", target, e.getMessage());
        }
    }
}