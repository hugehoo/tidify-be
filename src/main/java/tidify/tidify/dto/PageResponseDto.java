package tidify.tidify.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PageResponseDto<T> extends ResponseDto {
    private final CustomPage data;

    public PageResponseDto(CustomPage data) {
        super("success", "200");
        this.data = data;
    }
}