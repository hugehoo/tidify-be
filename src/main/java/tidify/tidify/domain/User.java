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
import tidify.tidify.security.JwtTokenProvider;

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
    public User(String email, String password, String accessToken, String refreshToken, SocialType socialtype) {
        this.email = email;
        this.password = password;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.socialType = socialtype;
    }

    public static User ofSocialType(String email, String password, String accessToken, String refreshToken, SocialType socialType) {
        return User.builder()
            .email(email)
            .password(password)
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .socialtype(socialType)
            .build();
    }

    public String getUserEmail() {
        return email;
    }

    public String getRoleKey() {
        return RoleType.ROLE_VIEW.name();
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

    public void modifyRefreshToken(String newRefreshToken) {
        this.refreshToken = newRefreshToken;
    }
}
