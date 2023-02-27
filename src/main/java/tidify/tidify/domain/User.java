package tidify.tidify.domain;

import java.util.Collection;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;
import tidify.tidify.common.kakao.KAKAOLoginTokenInfo;
import tidify.tidify.common.kakao.KakaoInfoResponse;

@Getter
@Setter
@Entity(name = "user")
@DynamicUpdate
@DynamicInsert
public class User extends BaseEntity implements UserDetails {
    public User() {
    }

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

    // private String description;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    // @Lob
    private String accessToken;

    // @Lob
    private String refreshToken;
    //

    @Builder
    public User(String name, String email, RoleType role) {
        this.name = name;
        this.email = email;
    }

    // @Builder(builderMethodName = "KAKAO")
    public User(KakaoInfoResponse kakao, String password,
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

    public String getRoleKey() {
        return RoleType.ROLE_VIEW.name();
    }

    public User update(String name) {
        this.name = name;

        return this;
    }

    // @Builder.Default
    // @ElementCollection(fetch = FetchType.EAGER)
    // private List<String> roles = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
