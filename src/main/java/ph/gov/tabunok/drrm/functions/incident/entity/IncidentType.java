package ph.gov.tabunok.drrm.functions.incident.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IncidentType implements Labeled {
    FLOOD("Flood"),
    TYPHOON("Typhoon"),
    STORM_SURGE("Storm surge"),
    EARTHQUAKE("Earthquake"),
    FIRE("Fire"),
    LANDSLIDE("Landslide"),
    OTHER("Other hazard");

    private final String label;
}
