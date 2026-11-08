package ph.gov.tabunok.drrm.functions.incident.service;

import org.springframework.web.multipart.MultipartFile;

import ph.gov.tabunok.drrm.functions.incident.entity.MediaKind;

public interface FileStorageService {

    long MAX_PHOTO_BYTES = 8L * 1024 * 1024;
    long MAX_VIDEO_BYTES = 25L * 1024 * 1024;

    record StoredFile(String path, String contentType, long size) {
    }

    StoredFile store(MultipartFile file, MediaKind kind);

    String resolveUrl(String relativePath);

    void deleteQuietly(String relativePath);
}