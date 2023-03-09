package tidify.tidify.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {

    private final String errorCode;

    public ResourceNotFoundException(ErrorTypes type, Object... args) {
        super(String.format(type.getMessage(), args));
        this.errorCode = type.getCode();
    }
}
