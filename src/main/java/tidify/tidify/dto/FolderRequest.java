package tidify.tidify.dto;


import lombok.Builder;
import lombok.Getter;
import tidify.tidify.domain.LabelType;

@Getter
@Builder
public class FolderRequest {

    private String folderName;
    private LabelType label;

}
