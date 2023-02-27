package tidify.tidify.dto;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tidify.tidify.domain.LabelType;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolderRequest {

    @NotBlank
    private String folderName;

    @NotNull
    private LabelType label;

}
