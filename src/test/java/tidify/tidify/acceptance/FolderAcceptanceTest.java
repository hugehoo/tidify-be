package tidify.tidify.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.*;
import static tidify.tidify.acceptance.TDFSteps.*;
import static tidify.tidify.common.SecretConstants.*;
import static tidify.tidify.utils.TestConstants.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.transaction.annotation.Transactional;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import tidify.tidify.domain.Bookmark;
import tidify.tidify.exception.ErrorTypes;
import tidify.tidify.repository.BookmarkRepository;
import tidify.tidify.repository.FolderRepository;

@Transactional
@AutoConfigureRestDocs
@DisplayName("폴더 도메인 인수 테스트")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ExtendWith(RestDocumentationExtension.class)
public class FolderAcceptanceTest {

    protected RequestSpecification spec;

    final String 폴더_이름 = "뉴진스의 폴더";
    final String 수정할_폴더_이름 = "뉴진스의 Omg 폴더";
    final String 북마크_이름 = "뉴진스의 북마크";
    final String 색상 = "GREEN";
    final String URL = "www.google.com";

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;
        this.spec = new RequestSpecBuilder()
            .addFilter(documentationConfiguration(restDocumentation))
            .build();
    }

    @Autowired
    BookmarkRepository bookmarkRepository;

    @Autowired
    FolderRepository folderRepository;


    /**
     * given 폴더가 미리 데이터베이스에 저장돼 있을 때,
     * when 폴더 목록 조회시
     * then 폴더의 이름과 라벨은 null 이 될 수 없다.
     */
    @Test
    void 폴더의_이름_라벨_반드시_존재() {
        // given : set up by DB

        // when
        ExtractableResponse<Response> 폴더 = 폴더_조회_API(spec);

        var 이름_누락_폴더 = 이름_누락_폴더_조회(폴더);
        var 라벨_누락_폴더 = 라벨_누락_폴더_조회(폴더);
        List<Object> 값_누락된_폴더 = Stream.concat(이름_누락_폴더, 라벨_누락_폴더).toList();

        // then
        assertThat(값_누락된_폴더.size()).isEqualTo(0);
    }

    /**
     * given 폴더의 이름과 라벨을 지정하고
     * when 폴더를 저장하면
     * then 지정한 이름과 라벨의 폴더가 생성된다.
     */
    @Test
    void 폴더_생성_테스트() {
        // given
        Map<String, Object> 폴더_정보 = 폴더_Request_Body(폴더_이름, 색상);

        // when
        String 저장된_폴더명 = 폴더_생성_API(spec, 폴더_정보)
            .jsonPath()
            .get(DATA_FOLDER_NAME);

        // then
        assertEquals(저장된_폴더명, 폴더_이름);
    }

    /**
     * given 생성된 폴더가 존재할 때,
     * when 해당 폴더의 이름을 수정하면
     * then 폴더의 이름이 수정된다.
     */
    @Test
    void 폴더_수정_테스트() {

        // given
        Long 폴더_ID = 폴더_생성(폴더_이름, 색상);

        // when
        Map<String, Object> data = 폴더_Request_Body(수정할_폴더_이름, 색상);
        String 수정된_폴더명 = 폴더_수정_API(spec, 폴더_ID, data)
            .jsonPath()
            .get(DATA_FOLDER_NAME);

        // then
        assertEquals(수정된_폴더명, 수정할_폴더_이름);
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
        String 폴더_이름_누락 = null;
        Map<String, Object> data = 폴더_Request_Body(폴더_이름_누락, 색상);

        // when
        String statusCode = 폴더_생성_API(spec, data).jsonPath().get(ERROR_CODE);

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
        String 폴더_라벨_누락 = null;
        Map<String, Object> data = 폴더_Request_Body(폴더_이름, 폴더_라벨_누락);

        // when
        String statusCode = 폴더_생성_API(spec, data).jsonPath().get(ERROR_CODE);

        // then
        assertThat(ErrorTypes.SQL_VIOLATION_EXCEPTION.getCode()).isEqualTo((statusCode));
    }

    /**
     * given 폴더에 북마크가 존재할 때,
     * when 해당 폴더를 삭제하면
     * then 북마크는 폴더 미지정 상태(null)로 업데이트 된다.
     */
    @Test
    @DisplayName("폴더를 삭제하면, 해당 폴더의 북마크는 폴더 미지정 상태로 변경된다.")
    void 폴더_삭제_테스트() {

        // given
        Long 폴더_ID = 폴더_생성(폴더_이름, 색상);
        Long 북마크_ID = 북마크_생성(폴더_ID);

        // when
        폴더_삭제_API(spec, 폴더_ID);

        // then
        Bookmark 북마크 = 북마크_조회(북마크_ID);
        assertThat(북마크.getId()).isEqualTo(북마크_ID);
        assertThat(북마크.getFolder()).isNull();
    }

    @AfterEach
    void ClearDB() {
        folderRepository.deleteByUserId(USER_ID);
        bookmarkRepository.deleteByUserId(USER_ID);
    }

    private Stream<Object> 라벨_누락_폴더_조회(ExtractableResponse<Response> response) {
        return response.jsonPath()
            .getList(DATA_CONTENT_COLOR)
            .stream()
            .filter(Objects::isNull);
    }

    private Stream<Object> 이름_누락_폴더_조회(ExtractableResponse<Response> response) {
        return response.jsonPath()
            .getList(DATA_CONTENT_FOLDER_NAME)
            .stream()
            .filter(Objects::isNull);
    }

    private Bookmark 북마크_조회(Long 북마크_ID) {
        return bookmarkRepository.findBookmarkByIdAndDel(북마크_ID, false);
    }

    private Long 북마크_생성(Long 폴더_ID) {

        return 북마크_생성_API(spec, 북마크_Request_Body(북마크_이름, URL, 폴더_ID))
            .jsonPath()
            .<Integer>get(DATA_BOOKMARK_ID)
            .longValue();
    }

    private Long 폴더_생성(String 폴더_이름, String 색상) {

        return 폴더_생성_API(spec, 폴더_Request_Body(폴더_이름, 색상))
            .jsonPath()
            .<Integer>get(DATA_FOLDER_ID)
            .longValue();
    }
}
