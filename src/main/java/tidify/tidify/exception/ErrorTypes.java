package tidify.tidify.exception;

import lombok.Getter;

@Getter
public enum ErrorTypes {

    // Http Status 2xx OK (No Content)
    FOLDER_NOT_FOUND("204", "Folder id %s is not exists"),
    BOOKMARK_NOT_FOUND("204", "Bookmark id %s is not exists"),

    // Http Status 4xx (Request)
    BAD_REQUEST_EXCEPTION("400", "Bad Request Exception"),
    ILLEGAL_ARGUMENT_EXCEPTION("400", "Illegal Argument Exception"),
    USER_NOT_FOUND_EXCEPTION("400", "User Not Found Exception"),
    UN_AUTHORIZATION_EXCEPTION("401",  "Un Authorization Exception"),
    HANDLE_ACCESS_DENIED_EXCEPTION("403", "Handle Access Is Denied Exception"),
    METHOD_NOT_ALLOWED("405", "Method Not Allowed Exception"),

    // Http Status 5xx (Internal)
    INTERNAL_SERVER_ERROR("500", "Server Error");

    private final String code;
    private final String message;

    ErrorTypes(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
