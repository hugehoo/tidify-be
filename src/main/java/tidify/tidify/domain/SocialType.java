package tidify.tidify.domain;

import java.util.Arrays;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SocialType {
    NON("소셜없음"), KAKAO("카카오"), APPLE("애플");

    private final String description;

    public static SocialType getStringToEnum(String name) {
        name = name.toUpperCase();
        for (SocialType value : SocialType.values()) {
            if (value.name().equals(name)) return value;
        }
        return null;
    }

    public static SocialType[] getStringsToEnums(String[] names) {
        if (!Objects.isNull(names) && names.length > 0) {
            SocialType[] values = Arrays.stream(names).map(SocialType::getStringToEnum).filter(Objects::nonNull).toArray(SocialType[]::new);
            return values.length > 0 ? values : null;
        }
        return null;
    }
}