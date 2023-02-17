package tidify.tidify.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Builder;
import lombok.Getter;
import tidify.tidify.domain.Folder;
import tidify.tidify.domain.LabelType;

@Getter
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

    @Builder
    public FolderResponse(String folderName, LabelType label) {
        this.folderName = folderName;
        this.label = label;
    }

    @QueryProjection
    public FolderResponse(Folder folder, Long count) {
        this.folderId = folder.getId();
        this.folderName = folder.getName();
        this.label = folder.getLabel();
        this.count = count;
    }
}
