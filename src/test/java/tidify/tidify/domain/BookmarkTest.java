package tidify.tidify.domain;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class BookmarkTest {
    final String missingHttpUrl = "www.naver.com";
    final String httpUrl = "https://www.naver.com";


    @Test
    public void HTTP_프로토콜_없을때_URL_테스트() {
        Bookmark bookmark = Bookmark.create().url(missingHttpUrl).build();
        String url = bookmark.getUrl();
        assertTrue(url.contains("https://"));
    }

    @Test
    public void HTTP_프로토콜_있을때_URL_테스트() {
        Bookmark bookmark = Bookmark.create().url(httpUrl).build();
        String url = bookmark.getUrl();
        assertEquals(url, httpUrl);
    }

}
