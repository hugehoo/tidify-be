package tidify.tidify.acceptance;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
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

    /**
     * given 데이터베이스에 북마크가 저장돼 있을 때,
     * when 유저의 북마크 목록 조회시
     * then 북마크의 이름/URL 정보는 항상 존재한다.
     */
    @Test
    void 북마크_조회_테스트() {
        // given : set up by DB

        // when
        JsonPath response = 북마크_조회().jsonPath();
        List<String> 누락_데이터 = Stream.concat(이름이_누락된_북마크(response), URL이_누락된_북마크(response))
            .toList();

        // then
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
        String 이름 = "뉴진스의 하입보이요";
        String URL = "www.google.com";
        Map<String, Object> data = setUpRequestBody(이름, URL, 0L);

        // when
        ExtractableResponse<Response> response = 북마크_생성(data);
        String url = response.jsonPath().get("url");
        String name = response.jsonPath().get("name");

        // then
        assertEquals(URL, url);
        assertEquals(이름, name);
    }

    /**
     * given 북마크 생성 시 이름을 null 로 지정하여
     * when 북마크를 저장하면
     * then 저장된 북마크의 이름은 url 과 동일하다.
     */
    @Test
    void 북마크_생성_이름_누락_테스트() {
        // given
        String 이름 = null;
        String URL = "www.google.com";
        Map<String, Object> data = setUpRequestBody(이름, URL, 0L);

        // when
        ExtractableResponse<Response> response = 북마크_생성(data);
        String url = response.jsonPath().get("url");
        String name = response.jsonPath().get("name");

        // then
        assertThat(url).isEqualTo(name);
    }

    /**
     * given 기존 북마크의 이름이 있을 때
     * when 북마크의 이름을 수정하면
     * then 수정된 이름이 저장된다.
     */
    @Test
    void 북마크_수정_테스트() {
        // given
        String 이름 = "수정된 이름";
        String URL = "www.google.com";
        Map<String, Object> data = setUpRequestBody(이름, URL, 0L);

        // when
        ExtractableResponse<Response> response = 북마크_수정(data);

        // then
        JsonPath jsonPath = response.jsonPath();
        String name = jsonPath.get("name");
        assertEquals(이름, name);
    }

    /**
     * given 기존 북마크의 이름이 있을 때
     * when 북마크의 이름을 지우면
     * then url 값으로 이름이 저장된다.
     */
    @Test
    void 북마크_이름_NULL_수정_테스트() {
        // given
        String 이름 = null;
        String URL = "www.google.com";
        Map<String, Object> data = setUpRequestBody(이름, URL, 0L);

        // when
        ExtractableResponse<Response> response = 북마크_수정(data);

        // then
        JsonPath jsonPath = response.jsonPath();
        String name = jsonPath.get("name");
        assertEquals(URL, name);
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
        ExtractableResponse<Response> response = 북마크_삭제(삭제대상_ID);

        // then
        int statusCode = response.statusCode();
        assertThat(HttpStatus.NO_CONTENT.value()).isEqualTo(statusCode);
    }

    private static int ID_랜덤_선택(List<Integer> 북마크_ID_목록) {
        int 랜덤_ID = new Random().nextInt(북마크_ID_목록.size());
        return 북마크_ID_목록.get(랜덤_ID);
    }

    private static ExtractableResponse<Response> 북마크_삭제(long bookmarkId) {
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

    private static ExtractableResponse<Response> 북마크_수정(Map<String, Object> body) {
        Long bookmarkId = 29L;
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

    private static ExtractableResponse<Response> 북마크_생성(Map<String, Object> body) {

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

    private static ExtractableResponse<Response> 북마크_조회() {
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

    private static Stream<String> URL이_누락된_북마크(JsonPath jsonPath) {
        return jsonPath.<String>getList("content.url").stream().filter(Objects::isNull);
    }

    private static Stream<String> 이름이_누락된_북마크(JsonPath jsonPath) {
        return jsonPath.<String>getList("content.name").stream().filter(Objects::isNull);
    }

    private Map<String, Object> setUpRequestBody(String name, String url, Long folderId) {

        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("url", url);
        map.put("folderId", folderId);

        return map;
    }

}
