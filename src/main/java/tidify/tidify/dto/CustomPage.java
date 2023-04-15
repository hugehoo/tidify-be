package tidify.tidify.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomPage{
    private final List<?> content;
    private final Boolean isLast;
    private final int currentPage;


    public static CustomPage of(Page<?> data) {
        return CustomPage.builder()
            .content(data.getContent())
            .isLast(data.isLast())
            .currentPage(data.getNumber())
            .build();
    }
}
