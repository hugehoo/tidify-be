package tidify.tidify.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;


@Getter
public class BookmarkRequest {

    @NotBlank
    private String url;

    @NotBlank
    private String name;

    private Long folderId;
}
