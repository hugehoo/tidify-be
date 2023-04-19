package tidify.tidify.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class ResponseDto {
    private final String message;
    private final String code;

    public static ResponseDto ofDeleteApi() {
        return ResponseDto.builder()
            .code("200")
            .message("success")
            .build();
    }

}
