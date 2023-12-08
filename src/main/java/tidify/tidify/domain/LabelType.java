package tidify.tidify.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

@JsonFormat(shape = Shape.OBJECT)
public enum LabelType {
    ASHBLUE,
    BLUE,
    PURPLE,
    GREEN,
    YELLOW,
    ORANGE,
    RED,
    INDIGO,
    MINT,
    PINK;
}
