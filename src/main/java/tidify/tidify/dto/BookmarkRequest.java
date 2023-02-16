package tidify.tidify.dto;

import lombok.Getter;


@Getter
public class BookmarkRequest {

    private String url;

    private String name;

    private Long folderId;
}
