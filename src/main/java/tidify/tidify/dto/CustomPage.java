package tidify.tidify.dto;

import java.util.List;

import org.springframework.data.domain.Page;

public record CustomPage(List<?> content, Boolean isLast, int currentPage) {
    public static CustomPage of(Page<?> data) {
        return new CustomPage(data.getContent(), data.isLast(), data.getNumber());
    }
}
