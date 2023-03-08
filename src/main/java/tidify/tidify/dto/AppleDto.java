package tidify.tidify.dto;

import java.util.ArrayList;

import lombok.Getter;

public class AppleDto {
    @Getter
    public static class AppleKeysResponse {
        private ArrayList<Key> keys;
    }

    @Getter
    public static class Header {
        private String alg;
        private String kid;
    }

    @Getter
    public static class Key {
        private String kty;
        private String kid;
        private String use;
        private String alg;
        private String n;
        private String e;
    }
}


