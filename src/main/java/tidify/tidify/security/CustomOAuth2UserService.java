package tidify.tidify.security;

import java.util.Collections;

import javax.servlet.http.HttpSession;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tidify.tidify.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // provider
        String userNameAttributeName = userRequest.getClientRegistration()
            .getProviderDetails()
            .getUserInfoEndpoint()
            .getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName,
            oAuth2User.getAttributes());

        User user = saveOrUpdate(attributes);

        httpSession.setAttribute("user", new SessionUser(user));

        return new DefaultOAuth2User(
            Collections.singleton(
                new SimpleGrantedAuthority(user.getRoleKey())), attributes.getAttributes(),
            attributes.getNameAttributeKey());
    }

    private User saveOrUpdate(OAuthAttributes attributes) {
        User user = userRepository.findWithUserRolesByEmailAndDel(attributes.getEmail(), false)
            .map(entity -> entity.update(attributes.getName()))
            .orElse(attributes.toEntity());

        return userRepository.save(user);
    }
    // @Override
    // public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    //     Assert.notNull(userRequest, "userRequest cannot be null");
    //
    //     UserInfoEndpoint userInfoEndpoint = userRequest.getClientRegistration()
    //         .getProviderDetails()
    //         .getUserInfoEndpoint();
    //
    //     String userInfoUri = userInfoEndpoint.getUri();
    //     validateUserInfoUri(userRequest, userInfoUri);
    //
    //     String nameAttributeKey = userInfoEndpoint.getUserNameAttributeName();
    //     validateUserNameAttributeName(userRequest, nameAttributeKey);
    //
    //     Map<String, Object> attributes = getAttributes(userRequest);
    //     Set<GrantedAuthority> authorities = getAuthorities(userRequest, attributes);
    //
    //     return KakaoOAuth2User.builder()
    //         .authorities(authorities)
    //         .attributes(attributes)
    //         .nameAttributeKey(nameAttributeKey)
    //         .build();
    // }
}
