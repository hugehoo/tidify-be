package tidify.tidify.domain;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tidify.tidify.dto.BookmarkRequest;

@Getter
@Entity(name = "bookmark")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bookmark extends BaseEntity {

    @Column(name = "url")
    private String url;

    @Column(name = "og_image")
    private String ogImage;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id", nullable = true)
    private Folder folder;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(name = "starred")
    private boolean isStarred = false;

    @Builder(builderMethodName = "create")
    public Bookmark(String url, String name, Folder folder, User user) {
        this.url = verifyUrl(url);
        this.ogImage = getOgImage(url);
        this.name = name;
        this.folder = folder;
        this.user = user;
    }

    public void modify(String url, String name, Folder folder) {
        this.url = verifyUrl(url);
        this.name = name;
        this.folder = folder;
    }

    public void delete() {
        super.delete();
    }

    public void toggleStar() {
        this.isStarred = !this.isStarred;
    }

    private String verifyUrl(String url) {

        String HTTP = "http://";
        String HTTPS = "https://";

        if (url.startsWith(HTTP) || url.startsWith(HTTPS)) {
            return url;
        }
        return String.format("%s%s", HTTPS, url);
    }

    private String getOgImage(String url) {
        try {
            Element ogTag = Jsoup.connect(verifyUrl(url))
                .get()
                .select("meta[property=og:image]")
                .first();
            if (Objects.nonNull(ogTag)) {
                return ogTag.attr("content");
            }
            return "";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
