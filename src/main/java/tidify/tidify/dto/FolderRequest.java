package tidify.tidify.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

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
