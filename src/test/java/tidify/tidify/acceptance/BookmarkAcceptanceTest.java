package tidify.tidify.acceptance;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static tidify.tidify.acceptance.FolderAcceptanceTest.*;
import static tidify.tidify.common.SecretConstants.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@DisplayName("북마크 도메인 인수 테스트")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Transactional
public class BookmarkAcceptanceTest {

    final String 폴더_1_이름 = "뉴진스의 폴더";
    final String 폴더_2_이름 = "뉴진스의 폴더2";
    final String 색상 = "GREEN";
    final String 북마크_이름 = "뉴진스의 북마크";
    final String URL_GOOGLE = "www.google.com";
    final String URL_NAVER = "www.naver.com";

    final String 수정된_북마크_이름 = "뉴진스의 수정된 북마크";

    /**
     * given 데이터베이스에 북마크가 저장돼 있을 때,
     * when 유저의 북마크 목록 조회시
     * then 북마크의 이름/URL 정보는 항상 존재한다.
     */
    @Test
    void 북마크_조회_테스트() {
        // given : set up by DB

        // when
        JsonPath 북마크 = 북마크_조회().jsonPath();

        // then
        var 이름_누락_북마크 = 이름_누락_북마크_조회(북마크);
        var URL_누락_북마크 = URL_누락_북마크_조회(북마크);
        List<String> 누락_데이터 = Stream.concat(이름_누락_북마크, URL_누락_북마크).toList();

        assertThat(누락_데이터).isEmpty();
    }

    /**
     * given 북마크의 이름과 URL 이 모두 지정되었을 때
     * when 북마크를 저장하면
     * then 북마크가 생성된다.
     */
    @Test
    void 북마크_생성_테스트() {
        // given
        Map<String, Object> 북마크_정보 = setUpBookmarkRequestBody(북마크_이름, URL_GOOGLE, 0L);

        // when
        ExtractableResponse<Response> 북마크 = 북마크_생성_API(북마크_정보);

        // then
        String url = 북마크.jsonPath().get("url");
        String name = 북마크.jsonPath().get("name");

        assertEquals(URL_GOOGLE, url);
        assertEquals(북마크_이름, name);
    }

    /**
     * given 북마크 생성 시 이름을 null 로 지정하여
     * when 북마크를 저장하면
     * then 저장된 북마크의 이름은 url 과 동일하다.
     */
    @Test
    void 북마크_생성_이름_누락_테스트() {
        // given
        String 이름_누락 = null;
        Map<String, Object> 북마크_정보 = setUpBookmarkRequestBody(이름_누락, URL_GOOGLE, 0L);

        // when
        ExtractableResponse<Response> 북마크 = 북마크_생성_API(북마크_정보);

        // then
        String url = 북마크.jsonPath().get("url");
        String name = 북마크.jsonPath().get("name");

        assertThat(url).isEqualTo(name);
    }

    /**
     * given 기존 북마크의 이름이 있을 때
     * when 북마크의 이름을 수정하면
     * then 수정된 이름이 저장된다.
     */
    @Test
    void 북마크_이름_수정_테스트() {
        // given
        Long 북마크_ID = 북마크_생성(0L);

        // when
        Map<String, Object> 북마크_정보_수정 = setUpBookmarkRequestBody(수정된_북마크_이름, URL_GOOGLE, 0L);
        String name = 북마크_수정_API(북마크_ID, 북마크_정보_수정)
            .jsonPath()
            .get("name");

        // then
        assertEquals(수정된_북마크_이름, name);
    }

    /**
     * given 기존 북마크가 존재할 때
     * when 북마크의 폴더를 수정하면
     * then 해당 폴더를 조회 시, 북마크가 조회된다.
     */
    @Test
    void 북마크_폴더_수정_테스트() {

        // given
        Long 폴더_1_ID = 폴더_생성(폴더_1_이름, 색상);
        Long 폴더_2_ID = 폴더_생성(폴더_2_이름, 색상);
        Long 북마크_ID = 북마크_생성(폴더_1_ID.longValue());

        // when
        북마크__수정(폴더_2_ID, 북마크_ID);

        // then
        List<Integer> 폴더_1_북마크_리스트 = 폴더_조회(폴더_1_ID);
        List<Integer> 폴더_2_북마크_리스트 = 폴더_조회(폴더_2_ID);

        assertThat(폴더_1_북마크_리스트).doesNotContain(북마크_ID.intValue());
        assertThat(폴더_2_북마크_리스트).contains(북마크_ID.intValue());
    }

    /**
     * given 기존 북마크의 이름이 있을 때
     * when 북마크의 이름을 지우면
     * then url 값으로 이름이 저장된다.
     */
    @Test
    void 북마크_이름_NULL_수정_테스트() {

        // given
        Long 북마크_ID = 북마크_생성(0L);

        // when
        String 북마크_이름_NULL = null;
        Map<String, Object> 북마크_정보_수정 = setUpBookmarkRequestBody(북마크_이름_NULL, URL_NAVER, 0L);
        String 수정된_북마크_이름 = 북마크_수정_API(북마크_ID, 북마크_정보_수정)
            .jsonPath()
            .get("수정된_북마크_이름");

        // then
        assertEquals(URL_NAVER, 수정된_북마크_이름);
    }

    /**
     * given 북마크 목록 조회하여 ID 선택,
     * when 해당 ID 의 북마크를 삭제하면
     * then 북마크가 삭제된다.
     */
    @Test
    void 북마크_삭제_테스트() {
        // given
        List<Integer> 북마크_ID_목록 = 북마크_조회().jsonPath().getList("content.id");
        int 삭제대상_ID = ID_랜덤_선택(북마크_ID_목록);

        // when
        ExtractableResponse<Response> response = 북마크_삭제_API(삭제대상_ID);

        // then
        int statusCode = response.statusCode();
        assertThat(HttpStatus.NO_CONTENT.value()).isEqualTo(statusCode);
    }

    private static ExtractableResponse<Response> 북마크_삭제_API(long bookmarkId) {
        return RestAssured.given().log().all()
            .headers(
                "X-Auth-Token",
                ACCESS_TOKEN,
                "refreshToken",
                REFRESH_TOKEN,
                "Content-Type", ContentType.JSON,
                "Accept", ContentType.JSON)
            .when()
            .delete("/app/bookmarks/{bookmarkId}", bookmarkId)
            .then().log().all().extract();
    }

    private static ExtractableResponse<Response> 북마크_수정_API(Long bookmarkId, Map<String, Object> body) {

        return RestAssured.given().log().all()
            .headers(
                "X-Auth-Token",
                ACCESS_TOKEN,
                "refreshToken",
                REFRESH_TOKEN,
                "Content-Type", ContentType.JSON,
                "Accept", ContentType.JSON)
            .body(body)
            .when().patch("/app/bookmarks/{bookmarkId}", bookmarkId)
            .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 북마크_생성_API(Map<String, Object> body) {

        return RestAssured.given().log().all()
            .headers(
                "X-Auth-Token",
                ACCESS_TOKEN,
                "refreshToken",
                REFRESH_TOKEN,
                "Content-Type", ContentType.JSON,
                "Accept", ContentType.JSON)
            .body(body)
            .when().post("/app/bookmarks/")
            .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 북마크_조회() {
        return RestAssured.given().log().all()
            .headers(
                "X-Auth-Token",
                ACCESS_TOKEN,
                "refreshToken",
                REFRESH_TOKEN,
                "Content-Type", ContentType.JSON,
                "Accept", ContentType.JSON)
            .when()
            .get("/app/bookmarks")
            .then()
            .log()
            .all()
            .extract();
    }

    private static Stream<String> URL_누락_북마크_조회(JsonPath jsonPath) {
        return jsonPath.<String>getList("content.url").stream().filter(Objects::isNull);
    }

    private static Stream<String> 이름_누락_북마크_조회(JsonPath jsonPath) {
        return jsonPath.<String>getList("content.name").stream().filter(Objects::isNull);
    }

    public static Map<String, Object> setUpBookmarkRequestBody(String name, String url, Long folderId) {

        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("url", url);
        map.put("folderId", folderId);

        return map;
    }

    private static int ID_랜덤_선택(List<Integer> 북마크_ID_목록) {
        int 랜덤_ID = new Random().nextInt(북마크_ID_목록.size());
        return 북마크_ID_목록.get(랜덤_ID);
    }

    private Long 북마크_생성(long 폴더_1_Id) {
        Map<String, Object> bookmarkRequestBody = setUpBookmarkRequestBody(북마크_이름, URL_GOOGLE, 폴더_1_Id);

        return 북마크_생성_API(bookmarkRequestBody)
            .jsonPath()
            .<Integer>get("id").longValue();
    }

    private Long 폴더_생성(String 폴더_이름, String 색상) {
        Map<String, Object> data = 폴더_Request_Body(폴더_이름, 색상);
        return 폴더_생성_API(data).jsonPath().<Integer>get("folderId").longValue();
    }

    private static List<Integer> 폴더_조회(Long 폴더_1_ID) {
        return 단일_폴더_조회_API(폴더_1_ID).jsonPath().getList("content.id");
    }

    private void 북마크__수정(Long 폴더_2_ID, Long 북마크_ID) {
        북마크_수정_API(북마크_ID, setUpBookmarkRequestBody(북마크_이름, URL_GOOGLE, 폴더_2_ID));
    }

}
