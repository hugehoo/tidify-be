package tidify.tidify.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tidify.tidify.domain.Label;

@Getter
@Builder
@AllArgsConstructor
public class LabelResponse {

    protected LabelResponse() {}

    private Long id;
    private String color;
    private String hex;

    public static LabelResponse of(Label label) {
        return LabelResponse.builder()
            .id(label.getId())
            .color(label.getColor())
            .hex(label.getHex())
            .build();
    }
}
