package tidify.tidify.dto;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import tidify.tidify.domain.Bookmark;

@Getter
@ToString
// @JsonInclude(JsonInclude.Include.NON_NULL)
public class BookmarkResponse {

    private Long id;
    private String url;
    private String name;
    private Long folderId;

    @Builder
    public BookmarkResponse(Long id, String url, String name, Long folderId) {
        this.id = id;
        this.url = url;
        this.name = name;
        this.folderId = folderId;
    }

    @QueryProjection
    public BookmarkResponse(Bookmark bookmark, Long folderId) {
        this.id = bookmark.getId();
        this.url= bookmark.getUrl();
        this.name = bookmark.getName();
        this.folderId = folderId;
    }


    public static BookmarkResponse of(Bookmark bookmark, Long folderId) {
        return BookmarkResponse.builder()
            .id(bookmark.getId())
            .url(bookmark.getUrl())
            .name(bookmark.getName())
            .folderId(folderId)
            .build();
    }

    @Getter
    public static class BookmarkModifyResponse {
        private String url;
        private String name;
        private Long folderId;

        public BookmarkModifyResponse(
            BookmarkRequest request) {
            this.url = request.getUrl();
            this.name = request.getName();
            this.folderId = request.getFolderId();
        }
    }
}
