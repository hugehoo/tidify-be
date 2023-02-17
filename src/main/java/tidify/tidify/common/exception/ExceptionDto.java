package tidify.tidify.common.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class ExceptionDto {
    private final String path;
    private final String message;
    private final String errorCode;
}
