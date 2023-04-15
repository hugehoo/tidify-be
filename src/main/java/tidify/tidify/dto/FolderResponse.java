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
    private String color;
    private Long count;

    public static FolderResponse of(Folder folder) {
        return FolderResponse.builder()
            .folderId(folder.getId())
            .folderName(folder.getName())
            // .label(folder.getLabel())
            .color(folder.getLabel().getColor().toUpperCase())
            .build();
    }

    @Builder
    public FolderResponse(Long folderId, String folderName,
        // LabelType label
        String color
    ) {
        this.folderId = folderId;
        this.folderName = folderName;
        // this.label = label;
        this.color = color;
    }

    @QueryProjection
    public FolderResponse(Folder folder, Long count) {
        this.folderId = folder.getId();
        this.folderName = folder.getName();
        // this.label = folder.getLabel();
        this.color = folder.getLabel().getColor().toUpperCase();
        this.count = count;
    }
}
