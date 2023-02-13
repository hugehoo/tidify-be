package tidify.tidify.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    @Column(unique = true)
    private String nickname;

    private String socialId;


    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    private SocialType type;

    @Lob
    private String accessToken;

    @Lob
    private String refreshToken;

    @Builder(builderMethodName = "kakaoAccount")
    public Account(String kakaoUserId, String nickName, String accessToken, String refreshToken, String profileImageUrl, SocialType type, String email) {
        this.socialId = kakaoUserId;
        this.nickname = nickName;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.profileImageUrl = profileImageUrl;
        this.type = type;
    }
}
