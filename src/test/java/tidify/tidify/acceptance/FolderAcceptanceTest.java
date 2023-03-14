package tidify.tidify.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static tidify.tidify.common.SecretConstants.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import tidify.tidify.exception.ErrorTypes;

@DisplayName("폴더 도메인 인수 테스트")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Transactional
public class FolderAcceptanceTest {

    /**
     * given 폴더가 미리 데이터베이스에 저장돼 있을 때,
     * when 폴더 목록 조회시
     * then 폴더의 이름과 라벨은 null 이 될 수 없다.
     */
    @Test
    void 폴더의_이름_라벨_반드시_존재() {
        // given : set up by DB

        // when
        ExtractableResponse<Response> response = 폴더_조회();
        List<Object> collect = Stream.concat(이름_누락_폴더(response), 라벨_누락_폴더(response)).toList();

        // then
        assertThat(collect.size()).isEqualTo(0);
    }

    /**
     * given 폴더의 이름과 라벨을 지정하고
     * when 폴더를 저장하면
     * then 지정한 이름과 라벨의 폴더가 생성된다.
     */
    @Test
    void 폴더_생성_테스트() {
        // given
        Map<String, Object> data = new HashMap<>();
        String 저장할_폴더명 = "뉴진스의 하입보이요";
        data.put("folderName", 저장할_폴더명);
        data.put("label", "GREEN");

        // when
        ExtractableResponse<Response> response = 폴더_생성(data);

        // then
        JsonPath jsonPath = response.jsonPath();
        String 저장된_폴더명 = jsonPath.get("folderName");
        assertEquals(저장된_폴더명, 저장할_폴더명);
    }

    /**
     * given 기존 폴더의
     * when 이름을 수정하면 + 빈 값으로 수정할 수는 없다.
     * then 폴더의 이름이 수정된다.
     */
    @Test
    void 폴더_수정_테스트() {
        // given
        Map<String, Object> data = new HashMap<>();
        String 수정할_폴더명 = "Omg";
        data.put("folderName", 수정할_폴더명);
        data.put("label", "GREEN");

        // when
        ExtractableResponse<Response> response = 폴더_수정(data);

        // then
        JsonPath jsonPath = response.jsonPath();
        String 수정된_폴더명 = jsonPath.get("folderName");
        assertEquals(수정된_폴더명, 수정할_폴더명);
    }

    /**
     * given 폴더를 생성할 때
     * when 이름이 누락되면
     * then 예외를 던진다.
     */
    @Test
    @DisplayName("폴더 생성시 이름이 누락되면 예외를 던진다.")
    void 이름_누락시_예외발생() {

        // given
        // Request DTO 의 validation 을 체크하는 필터를 통과하지 않고, 이미 그 후 단계에서부터 시작하는듯.
        Map<String, Object> data = new HashMap<>();
        data.put("label", "GREEN");

        // when
        String statusCode = 폴더_생성(data).jsonPath().get("errorCode");

        // then
        assertThat(ErrorTypes.SQL_VIOLATION_EXCEPTION.getCode()).isEqualTo((statusCode));
    }

    /**
     * given 폴더를 생성할 때
     * when 라벨이 누락되면
     * then 예외를 던진다.
     */
    @Test
    @DisplayName("폴더 생성시 라벨이 누락되면 예외를 던진다.")
    void 라벨_누락시_예외발생() {

        // given
        // Request DTO 의 validation 을 체크하는 필터를 통과하지 않고, 이미 그 후 단계에서부터 시작하는듯.
        Map<String, Object> data = new HashMap<>();
        data.put("folderName", "이름 짓는게 가장 어렵죠");

        // when
        String statusCode = 폴더_생성(data).jsonPath().get("errorCode");

        // then
        assertThat(ErrorTypes.SQL_VIOLATION_EXCEPTION.getCode()).isEqualTo((statusCode));
    }

    private static ExtractableResponse<Response> 폴더_수정(Map<String, Object> body) {
        Long folderId = 37L;

        return RestAssured.given().log().all()
            .headers(
                "X-Auth-Token",
                ACCESS_TOKEN,
                "refreshToken",
                REFRESH_TOKEN,
                "Content-Type", ContentType.JSON,
                "Accept", ContentType.JSON)
            .body(body)
            .when().patch("/app/folders/{folderId}", folderId)
            .then().log().all().extract();
    }

    private static ExtractableResponse<Response> 폴더_생성(Map<String, Object> body) {

        return RestAssured.given().log().all()
            .headers(
                "X-Auth-Token",
                ACCESS_TOKEN,
                "refreshToken",
                REFRESH_TOKEN,
                "Content-Type", ContentType.JSON,
                "Accept", ContentType.JSON)
            .body(body)
            .when().post("/app/folders")
            .then().log().all().extract();
    }

    private static ExtractableResponse<Response> 폴더_단일_조회(Long folderId) {
        return RestAssured.given().log().all()
            .headers(
                "X-Auth-Token",
                ACCESS_TOKEN,
                "refreshToken",
                REFRESH_TOKEN,
                "Content-Type", ContentType.JSON,
                "Accept", ContentType.JSON)
            .when().get("/app/folders/folder/{folderId}", folderId)
            .then().log().all().extract();
    }

    private static ExtractableResponse<Response> 폴더_조회() {
        return RestAssured.given().log().all()
            .headers(
                "X-Auth-Token",
                ACCESS_TOKEN,
                "refreshToken",
                REFRESH_TOKEN,
                "Content-Type", ContentType.JSON,
                "Accept", ContentType.JSON)
            .when().get("/app/folders/folder")
            .then().log().all().extract();
    }

    private static Stream<Object> 라벨_누락_폴더(ExtractableResponse<Response> response) {
        return response.jsonPath()
            .getList("content.label.color")
            .stream()
            .filter(Objects::isNull);
    }

    private static Stream<Object> 이름_누락_폴더(ExtractableResponse<Response> response) {
        return response.jsonPath()
            .getList("content.folderName")
            .stream()
            .filter(Objects::isNull);
    }
}
