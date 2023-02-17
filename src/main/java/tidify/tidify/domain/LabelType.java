package tidify.tidify.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

@JsonFormat(shape = Shape.OBJECT)
public enum LabelType {

    SKYBLUE("skyblue", "#379af0"),
    BLUE("blue", "#1c64ea"),
    PURPLE("purple", "#504ed2"),
    GREEN("green", "#35c759"),
    YELLOW("yellow", "#ffcc00"),
    ORANGE("orange", "#ff9500"),
    RED("red", "#fe3b2f"),
    BLACK("black", "#000000");

    private String color;
    private String hex;

    LabelType(String color, String hex) {
        this.color = color;
        this.hex = hex;
    }

    @JsonCreator
    public static LabelType from(String val){
        for(LabelType label : LabelType.values()){
            if(label.name().equals(val)){
                return label;
            }
        }
        return null;
    }

    public String getColor() {
        return color;
    }

    public String getHex() {
        return hex;
    }
}
