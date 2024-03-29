package tidify.tidify.dto;

import lombok.Getter;

@Getter
public class ObjectResponseDto<T> extends ResponseDto {
    private final T data;

    public ObjectResponseDto(T data) {
        super("success", "200");
        this.data = data;
    }
}