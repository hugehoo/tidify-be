package tidify.tidify.acceptance;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.*;
import static tidify.tidify.acceptance.TDFSteps.*;
import static tidify.tidify.common.SecretConstants.*;
import static tidify.tidify.utils.TestConstants.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.transaction.annotation.Transactional;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import tidify.tidify.repository.BookmarkRepository;
import tidify.tidify.repository.FolderRepository;

@Transactional
@AutoConfigureRestDocs
@DisplayName("북마크 도메인 인수 테스트")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ExtendWith(RestDocumentationExtension.class)
public class BookmarkAcceptanceTest {

    final String 폴더_1_이름 = "뉴진스의 폴더";
    final String 폴더_2_이름 = "뉴진스의 폴더2";
    final String 색상 = "GREEN";
    final String 북마크_이름 = "뉴진스의 북마크";
    final String URL_GOOGLE = "www.google.com";
    final String URL_NAVER = "www.naver.com";
    final String 수정된_북마크_이름 = "뉴진스의 수정된 북마크";

    protected RequestSpecification spec;

    @Autowired
    BookmarkRepository bookmarkRepository;

    @Autowired
    FolderRepository folderRepository;


    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;
        spec = new RequestSpecBuilder()
            .addFilter(documentationConfiguration(restDocumentation))
            .build();
    }

    /**
     * given 데이터베이스에 북마크가 저장돼 있을 때,
     * when 유저의 북마크 목록 조회시
     * then 북마크의 이름/URL 정보는 항상 존재한다.
     */
    @Test
    void 북마크_조회_테스트() {
        // given : set up by DB

        // when
        JsonPath 북마크 = 북마크_조회(spec).jsonPath();

        // then
        var 이름_누락_북마크 = 이름_누락_북마크_조회(북마크);
        var URL_누락_북마크 = URL_누락_북마크_조회(북마크);
        List<String> 누락_데이터 = Stream.concat(이름_누락_북마크, URL_누락_북마크).toList();

        assertThat(누락_데이터).isEmpty();
    }

    /**
     * given 다른 이름의 북마크 3개 저장돼 있을 때,
     * when 특정 이름으로 검색하면
     * then 검색어에 맞는 북마크가 검색된다.
     */
    @Test
    void 북마크_검색_테스트() {
        // given
        Map<String, Object> 북마크_정보_1 = 북마크_Request_Body("민지", URL_GOOGLE, 0L);
        Map<String, Object> 북마크_정보_2 = 북마크_Request_Body("해린", URL_GOOGLE, 0L);
        Map<String, Object> 북마크_정보_3 = 북마크_Request_Body("혜인", URL_GOOGLE, 0L);
        북마크_생성_API(spec, 북마크_정보_1);
        북마크_생성_API(spec, 북마크_정보_2);
        북마크_생성_API(spec, 북마크_정보_3);

        // when
        String 검색어 = "민지";
        List<String> 검색_결과 = 북마크_검색_API(검색어).jsonPath()
            .getList(DATA_CONTENT_NAME);

        // then
        assertThat(검색_결과).contains(검색어);
    }

    /**
     * given 북마크의 이름과 URL 이 모두 지정되었을 때
     * when 북마크를 저장하면
     * then 북마크가 생성된다.
     */
    @Test
    void 북마크_생성_테스트() {
        // given
        Map<String, Object> 북마크_정보 = 북마크_Request_Body(북마크_이름, URL_GOOGLE, 0L);

        // when
        ExtractableResponse<Response> 북마크 = 북마크_생성_API(spec, 북마크_정보);

        // then
        String url = 북마크.jsonPath().get(DATA_URL);
        String name = 북마크.jsonPath().get(DATA_NAME);

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
        Map<String, Object> 북마크_정보 = 북마크_Request_Body(이름_누락, URL_GOOGLE, 0L);

        // when
        ExtractableResponse<Response> 북마크 = 북마크_생성_API(spec, 북마크_정보);

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
        Map<String, Object> 북마크_정보_수정 = 북마크_Request_Body(수정된_북마크_이름, URL_GOOGLE, 0L);
        String name = 북마크_수정_API(spec, 북마크_ID, 북마크_정보_수정)
            .jsonPath()
            .get(DATA_NAME);

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
        Long 북마크_ID = 북마크_생성(폴더_1_ID);

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
        Map<String, Object> 북마크_정보_수정 = 북마크_Request_Body(북마크_이름_NULL, URL_NAVER, 0L);
        String 수정된_북마크_이름 = 북마크_수정_API(spec, 북마크_ID, 북마크_정보_수정)
            .jsonPath()
            .get(DATA_NAME);

        // then
        assertEquals(URL_NAVER, 수정된_북마크_이름);
    }

    @AfterEach
    void ClearDB() {
        folderRepository.deleteByUserId(USER_ID);
        bookmarkRepository.deleteByUserId(USER_ID);
    }

    /**
     * given 북마크 목록 조회하여 ID 선택,
     * when 해당 ID 의 북마크를 삭제하면
     * then 북마크가 삭제된다.
     */
    @Test
    void 북마크_삭제_테스트() {
        // given
        List<Integer> 북마크_ID_목록 = 북마크_조회(spec)
            .jsonPath()
            .getList(DATA_CONTENT_BOOKMARK_ID);
        int 삭제대상_ID = ID_랜덤_선택(북마크_ID_목록);

        // when
        ExtractableResponse<Response> response = 북마크_삭제_API(spec, 삭제대상_ID);

        // then
        int statusCode = response.statusCode();
        assertThat(HttpStatus.NO_CONTENT.value()).isEqualTo(statusCode);
    }

    private Stream<String> URL_누락_북마크_조회(JsonPath jsonPath) {
        return jsonPath.<String>getList(DATA_CONTENT_URL).stream().filter(Objects::isNull);
    }

    private Stream<String> 이름_누락_북마크_조회(JsonPath jsonPath) {
        return jsonPath.<String>getList(DATA_CONTENT_NAME).stream().filter(Objects::isNull);
    }

    private int ID_랜덤_선택(List<Integer> 북마크_ID_목록) {
        int 랜덤_ID = new Random().nextInt(북마크_ID_목록.size());
        return 북마크_ID_목록.get(랜덤_ID);
    }

    private Long 북마크_생성(long 폴더_1_Id) {
        Map<String, Object> bookmarkRequestBody = 북마크_Request_Body(북마크_이름, URL_GOOGLE, 폴더_1_Id);

        return 북마크_생성_API(spec, bookmarkRequestBody)
            .jsonPath()
            .<Integer>get("data.bookmarkId").longValue();
    }

    private Long 폴더_생성(String 폴더_이름, String 색상) {
        Map<String, Object> data = 폴더_Request_Body(폴더_이름, 색상);
        return 폴더_생성_API(spec, data).jsonPath().<Integer>get(DATA_FOLDER_ID).longValue();
    }

    private List<Integer> 폴더_조회(Long 폴더_ID) {
        return 단일_폴더_조회_API(spec, 폴더_ID).jsonPath().getList(DATA_CONTENT_BOOKMARK_ID);
    }

    private void 북마크__수정(Long 폴더_2_ID, Long 북마크_ID) {
        북마크_수정_API(spec, 북마크_ID, 북마크_Request_Body(북마크_이름, URL_GOOGLE, 폴더_2_ID));
    }

}
