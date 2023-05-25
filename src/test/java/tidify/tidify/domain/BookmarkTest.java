package tidify.tidify.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class BookmarkTest {

    final String 프로토콜 = "https://";
    final String 프로토콜_없는_URL = "www.naver.com";
    final String 프로토콜_있는_URL = "https://www.naver.com";

    @Test
    public void HTTP_프로토콜_없을때_URL_테스트() {
        Bookmark bookmark = 북마크_생성(프로토콜_없는_URL);
        String url = bookmark.getUrl();

        assertTrue(url.contains(프로토콜));
        assertEquals(url, 프로토콜 + 프로토콜_없는_URL);
    }

    @Test
    public void HTTP_프로토콜_있을때_URL_테스트() {
        Bookmark bookmark = 북마크_생성(프로토콜_있는_URL);
        String url = bookmark.getUrl();

        assertEquals(url, 프로토콜_있는_URL);
    }

    @Test
    void URL_수정_테스트() {
        Bookmark bookmark = 북마크_생성(프로토콜_있는_URL);
        bookmark.modify(프로토콜_없는_URL, "아무이름", null);
        String 수정된_URL = bookmark.getUrl();

        assertEquals(수정된_URL, 프로토콜 + 프로토콜_없는_URL);
    }

    private Bookmark 북마크_생성(String httpUrl) {
        return Bookmark.create().url(httpUrl).build();
    }

}
