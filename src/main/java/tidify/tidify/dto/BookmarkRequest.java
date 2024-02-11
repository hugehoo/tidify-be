package tidify.tidify.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;


@Getter
public class BookmarkRequest {

    @NotBlank
    private String url;

    private String name;

    private Long folderId;
}
