package tidify.tidify.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import tidify.tidify.domain.Bookmark;

@Getter
@ToString
public class BookmarkResponse {

    @JsonProperty("bookmarkId")
    private Long id;
    private String url;
    private String name;
    private Long folderId;
    private String ogImage;
    private boolean star;

    @Builder
    public BookmarkResponse(Long id, String url, String name, Long folderId, boolean star) {
        this.id = id;
        this.url = url;
        this.name = name;
        this.folderId = folderId;
        this.star = star;
    }

    @QueryProjection
    public BookmarkResponse(Bookmark bookmark, Long folderId) {
        this.id = bookmark.getId();
        this.url= bookmark.getUrl();
        this.name = bookmark.getName();
        this.star = bookmark.isStarred();
        this.ogImage = bookmark.getOgImage();
        this.folderId = folderId;
    }


    public static BookmarkResponse of(Bookmark bookmark, Long folderId) {
        return BookmarkResponse.builder()
            .id(bookmark.getId())
            .url(bookmark.getUrl())
            .name(bookmark.getName())
            .star(bookmark.isStarred())
            .folderId(folderId)
            .build();
    }

    @Getter
    public static class BookmarkModifyResponse {
        private String url;
        private String name;
        private Long folderId;

        public BookmarkModifyResponse(Bookmark bookmark) {
            this.url = bookmark.getUrl();
            this.name = bookmark.getName();
            this.folderId = bookmark.getFolder() != null ? bookmark.getFolder().getId() : 0L;
        }
    }
}
