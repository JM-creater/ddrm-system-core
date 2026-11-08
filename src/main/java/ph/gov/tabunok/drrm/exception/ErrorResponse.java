package ph.gov.tabunok.drrm.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private final int status;
    private final String message;
    private final Map<String, String> errors;
    private final LocalDateTime timestamp;

    public static ErrorResponse of(int status, String message) {
        return new ErrorResponse(status, message, new LinkedHashMap<>(), LocalDateTime.now());
    }
}
