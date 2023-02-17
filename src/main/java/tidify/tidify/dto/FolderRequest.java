package tidify.tidify.dto;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Getter;
import tidify.tidify.domain.LabelType;

@Getter
@Builder
public class FolderRequest {

    @NotBlank
    private String folderName;

    @NotNull
    private LabelType label;

}
