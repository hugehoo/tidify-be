package tidify.tidify.dto;

import lombok.Getter;

@Getter
public class PageResponseDto<T> extends ResponseDto {
    private final CustomPage data;

    public PageResponseDto(CustomPage data) {
        super("success", "200");
        this.data = data;
    }
}