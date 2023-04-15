package tidify.tidify.acceptance;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.*;
import static tidify.tidify.common.Constants.*;
import static tidify.tidify.common.Constants.REFRESH_TOKEN;
import static tidify.tidify.common.SecretConstants.*;

import java.util.HashMap;
import java.util.Map;

import org.springframework.restdocs.restassured3.RestDocumentationFilter;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ResponseBodyExtractionOptions;
import io.restassured.specification.RequestSpecification;

public class TDFSteps {

    static RestDocumentationFilter restDocsFilter(String apiIdentifier) {
        return document(apiIdentifier,
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()));
    }

    static ExtractableResponse<Response> 폴더_조회_API(RequestSpecification spec) {
        return RestAssured.given(spec).log().all()
            .filter(restDocsFilter("folder-get"))
            .headers(
                X_AUTH_TOKEN,
                ACCESS_TOKEN,
                "refreshToken",
                REFRESH_TOKEN,
                "Content-Type", ContentType.JSON,
                "Accept", ContentType.JSON)
            .when()
            .get("/app/folders")
            .then()
            .log()
            .all()
            .extract();
    }

    static ExtractableResponse<Response> 단일_폴더_조회_API(RequestSpecification spec, Long folderId) {

        return RestAssured.given(spec).log().all()
            .filter(restDocsFilter("single-folder-get"))
            .headers(
                X_AUTH_TOKEN,
                ACCESS_TOKEN,
                "refreshToken",
                REFRESH_TOKEN,
                "Content-Type", ContentType.JSON,
                "Accept", ContentType.JSON)
            .when()
            .get("/app/folders/{folderId}/bookmarks", folderId)
            .then()
            .log()
            .all()
            .extract();
    }

    static ExtractableResponse<Response> 폴더_생성_API(RequestSpecification spec, Map<String, Object> body) {

        return RestAssured.given(spec).log().all()
            .filter(restDocsFilter("folder-post"))
            .headers(
                X_AUTH_TOKEN,
                ACCESS_TOKEN,
                "refreshToken",
                REFRESH_TOKEN,
                "Content-Type", ContentType.JSON,
                "Accept", ContentType.JSON)
            .body(body)
            .when()
            .post("/app/folders")
            .then()
            .log()
            .all()
            .extract();
    }

    static ExtractableResponse<Response> 폴더_수정_API(RequestSpecification spec, Long folderId, Map<String, Object> body) {

        return RestAssured.given(spec).log().all()
            .filter(restDocsFilter("folder-patch"))
            .headers(
                X_AUTH_TOKEN,
                ACCESS_TOKEN,
                "refreshToken",
                REFRESH_TOKEN,
                "Content-Type", ContentType.JSON,
                "Accept", ContentType.JSON)
            .body(body)
            .when()
            .patch("/app/folders/{folderId}", folderId)
            .then()
            .log()
            .all()
            .extract();
    }

    static ExtractableResponse<Response> 폴더_삭제_API(RequestSpecification spec, Long folderId) {
        return RestAssured.given(spec).log().all()
            .filter(restDocsFilter("folder-delete"))
            .headers(
                X_AUTH_TOKEN, ACCESS_TOKEN,
                "refreshToken", REFRESH_TOKEN,
                "Content-Type", ContentType.JSON,
                "Accept", ContentType.JSON)
            .when()
            .delete("/app/folders/{folderId}", folderId)
            .then()
            .log()
            .all()
            .extract();
    }

    static ExtractableResponse<Response> 북마크_삭제_API(RequestSpecification spec, long bookmarkId) {
        return RestAssured.given(spec).log().all()
            .filter(restDocsFilter("bookmark-delete"))
            .headers(
                X_AUTH_TOKEN,
                ACCESS_TOKEN,
                "refreshToken",
                REFRESH_TOKEN,
                "Content-Type", ContentType.JSON,
                "Accept", ContentType.JSON)
            .when()
            .delete("/app/bookmarks/{bookmarkId}", bookmarkId)
            .then().log().all().extract();
    }

    static ExtractableResponse<Response> 북마크_수정_API(RequestSpecification spec, Long bookmarkId,
        Map<String, Object> body) {

        return RestAssured.given(spec).log().all()
            .filter(restDocsFilter("bookmark-patch"))
            .headers(
                X_AUTH_TOKEN,
                ACCESS_TOKEN,
                "refreshToken",
                REFRESH_TOKEN,
                "Content-Type", ContentType.JSON,
                "Accept", ContentType.JSON)
            .body(body)
            .when().patch("/app/bookmarks/{bookmarkId}", bookmarkId)
            .then().log().all().extract();
    }

    static ExtractableResponse<Response> 북마크_생성_API(RequestSpecification spec, Map<String, Object> body) {

        return RestAssured.given(spec).log().all()
            .filter(restDocsFilter("bookmark-post"))
            .headers(
                X_AUTH_TOKEN,
                ACCESS_TOKEN,
                "refreshToken",
                REFRESH_TOKEN,
                "Content-Type", ContentType.JSON,
                "Accept", ContentType.JSON)
            .body(body)
            .when().post("/app/bookmarks/")
            .then().log().all().extract();
    }

    static ExtractableResponse<Response> 북마크_조회(RequestSpecification spec) {
        return RestAssured.given(spec).log().all()
            .filter(restDocsFilter("bookmark-get"))
            .headers(
                X_AUTH_TOKEN,
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

    static ResponseBodyExtractionOptions 북마크_검색_API(String 검색어) {

        return RestAssured.given().log().all()
            .headers(
                X_AUTH_TOKEN,
                ACCESS_TOKEN,
                "refreshToken",
                REFRESH_TOKEN,
                "Content-Type", ContentType.JSON,
                "Accept", ContentType.JSON)
            .queryParam("keyword", 검색어)
            .when()
            .get("/app/bookmarks/search")
            .then()
            .log()
            .all()
            .extract();
    }

    static Map<String, Object> 북마크_Request_Body(String name, String url, Long folderId) {

        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("url", url);
        map.put("folderId", folderId);

        return map;
    }

    static Map<String, Object> 폴더_Request_Body(String folderName, String color) {

        Map<String, Object> map = new HashMap<>();
        map.put("folderName", folderName);
        map.put("label", color);

        return map;
    }

}
