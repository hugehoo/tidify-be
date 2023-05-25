package tidify.tidify.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity(name = "bookmark")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bookmark extends BaseEntity {

    @Column(name = "url")
    private String url;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id", nullable = true)
    private Folder folder;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Builder(builderMethodName = "create")
    public Bookmark(String url, String name, Folder folder, User user) {
        this.url = verifyUrl(url);
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
        this.setDel(true);
    }

    private String verifyUrl(String url) {

        String HTTP = "http://";
        String HTTPS = "https://";

        if (url.startsWith(HTTP) || url.startsWith(HTTPS)) {
            return url;
        }
        return String.format("https://%s", url);
    }

}
