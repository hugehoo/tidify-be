package tidify.tidify.common.exception;

public class FolderNotFoundException extends RuntimeException {
    private String message;

    public FolderNotFoundException() {
        this.message = "존재 하지 않는 폴더입니다.";
    }
}
