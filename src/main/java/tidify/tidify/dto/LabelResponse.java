package tidify.tidify.dto;

import tidify.tidify.domain.Label;

public record LabelResponse (Long id, String color, String hex){
    public static LabelResponse of(Label label) {
        return new LabelResponse(label.getId(), label.getColor(), label.getHex());
    }
}
