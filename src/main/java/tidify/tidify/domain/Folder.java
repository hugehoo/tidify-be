package tidify.tidify.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity(name = "folder")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Folder extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "label")
    private LabelType label;

    @Column(name = "shared")
    @Builder.Default
    private boolean isShared = false;

    @Column(name = "starred")
    @Builder.Default
    private boolean isStarred = false;

    public static Folder of(String name, LabelType type, User user) {
        return Folder.builder()
            .name(name)
            .label(type)
            .user(user)
            .build();
    }

    public void modify(String name, LabelType label) {
        this.name = name;
        this.label = label;
    }

    // TODO : needs Test
    public void delete() {
        super.delete();
    }

    public void toggleStar() {
        this.isStarred = !this.isStarred;
    }
}
