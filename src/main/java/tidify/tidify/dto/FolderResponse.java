package tidify.tidify.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Builder;
import lombok.Getter;
import tidify.tidify.domain.Folder;
import tidify.tidify.domain.LabelType;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FolderResponse {

    private Long folderId;
    private String folderName;
    private LabelType label;
    private Long count;

    public static FolderResponse of(Folder folder) {
        return FolderResponse.builder()
            .folderName(folder.getName())
            .label(folder.getLabel())
            .build();
    }

    @QueryProjection
    public FolderResponse(Long folderId, String folderName, LabelType label, Long count) {
        this.folderId = folderId;
        this.folderName = folderName;
        this.label = label;
        this.count = count;
    }
}
