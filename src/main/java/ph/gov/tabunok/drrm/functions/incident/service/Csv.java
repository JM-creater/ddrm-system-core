package ph.gov.tabunok.drrm.functions.incident.service;

import java.util.List;
import java.util.stream.Collectors;

/** Minimal RFC 4180 CSV writer. */
public final class Csv {

    private Csv() {
    }

    public static String row(List<?> cells) {
        return cells.stream().map(Csv::cell).collect(Collectors.joining(",")) + "\r\n";
    }

    private static String cell(Object value) {
        if (value == null) return "";
        String s = value.toString();
        // Guard against spreadsheet formula injection
        if (!s.isEmpty() && "=+-@".indexOf(s.charAt(0)) >= 0 && !s.matches("-?\\d+(\\.\\d+)?")) {
            s = "'" + s;
        }
        if (s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r")) {
            s = "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }
}