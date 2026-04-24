package com.g04.cityfix.common.constraints;

/**
 * Constraints of Report Type
 * @author Jerry Yang
 */
public enum ReportType {
    ROAD_DAMAGE("Road Damage"),
    STREET_LIGHT("Street Light Malfunction"),
    GARBAGE("Garbage Accumulation"),
    WATER_LEAK("Water Leak"),
    OTHER("Other");

    private final String label;

    ReportType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static String[] getLabels() {
        ReportType[] types = ReportType.values();
        String[] labels = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            labels[i] = types[i].getLabel();
        }
        return labels;
    }
}
