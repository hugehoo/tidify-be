package tidify.tidify.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.Getter;


@Getter
public class BookmarkRequest {

    @NotBlank
    private String url;

    @NotBlank
    @Length(max = 15)
    private String name;

    private Long folderId;
}
