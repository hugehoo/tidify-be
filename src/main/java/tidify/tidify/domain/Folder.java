package tidify.tidify.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tidify.tidify.security.BaseEntity;

@Getter
@Entity(name = "folder")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Folder extends BaseEntity {

    // TODO : 시큐리티 완성 짓고 제대로 연관관계 매핑
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "label")
    private LabelType label;

    public static Folder of(String name, LabelType type, Long userId) {
        return Folder.builder()
            .name(name)
            .label(type)
            .userId(userId)
            .build();
    }

    public void modify(String name, LabelType label) {
        this.name = name;
        this.label = label;
    }

}
