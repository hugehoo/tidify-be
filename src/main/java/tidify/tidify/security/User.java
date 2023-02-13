package tidify.tidify.security;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import tidify.tidify.common.KAKAOLoginTokenInfo;
import tidify.tidify.common.KakaoSample;
import tidify.tidify.domain.SocialType;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity(name = "user")
@DynamicUpdate
@DynamicInsert
public class User extends BaseEntity {

    @ColumnDefault(value = "0")
    @Column(nullable = false, length = 1)
    private String type;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 50)
    private String name;

    @JsonIgnore
    @Column(nullable = false, length = 150)
    private String password;

    @Singular("userRoles")
    @JsonManagedReference
    @JsonIgnoreProperties({"createTimestamp", "updateTimestamp", "del"})
    @OneToMany(mappedBy = "user")
    @Where(clause = "del = false")
    private Set<UserRole> userRoles;

    //
    // private String socialId;
    // private String socialAccount;

    private String profileImageUrl;

    private String description;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    // @Lob
    private String accessToken;

    // @Lob
    private String refreshToken;
    //

    // @Builder
    // public User(String type, String name, String email, String password) {
    //     this.type = type;
    //     this.name = name;
    //     this.email = email;
    //     this.password = password;
    // }

    @Builder(builderMethodName = "KAKAO")
    public User(String type, KakaoSample kakao, String password,
        KAKAOLoginTokenInfo token) {
        this.type = "0";
        this.name = kakao.getProperties().getNickName();
        this.accessToken = token.getAccess_token();
        this.refreshToken = token.getRefresh_token();
        this.email = kakao.getAccount().isEmail() ? kakao.getAccount().getEmail() : "";
        this.password = password;
        this.socialType = SocialType.KAKAO;
        this.profileImageUrl = kakao.getProperties().getProfileImage();
    }
}
