package ph.gov.tabunok.drrm.functions.incident.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import ph.gov.tabunok.drrm.config.AppProperties;
import ph.gov.tabunok.drrm.exception.ApiException;
import ph.gov.tabunok.drrm.functions.incident.entity.MediaKind;

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

    public FileStorageServiceImpl(AppProperties properties) {
        this.root = Path.of(properties.getUploadDir()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(root.resolve("incidents"));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create upload directory " + root, e);
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
        Path target = root.resolve(relative).normalize();
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Could not save uploaded file", e);
        }
        return new StoredFile(relative, type, file.getSize());
    }

    @Override
    public void deleteQuietly(String relativePath) {
        if (relativePath == null) return;
        Path target = root.resolve(relativePath).normalize();
        if (!target.startsWith(root)) return;
        try {
            Files.deleteIfExists(target);
        } catch (IOException e) {
            log.warn("Could not delete {}: {}", target, e.getMessage());
        }
    }
}