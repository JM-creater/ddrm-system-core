package ph.gov.tabunok.drrm.functions.incident.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MediaKind implements Labeled {
    PHOTO("Photo"),
    VIDEO("Video");

    private final String label;
}
