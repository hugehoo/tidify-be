package tidify.tidify.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class ExceptionDto {
    private String resultCode;
    private String message;

    public static ExceptionDto ofFailure(String resultCode, String message) {
        return ExceptionDto.builder()
            .resultCode(resultCode)
            .message(message)
            .build();
    }
}
