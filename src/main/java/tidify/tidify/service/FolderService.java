package tidify.tidify.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tidify.tidify.domain.Folder;
import tidify.tidify.dto.FolderRequest;
import tidify.tidify.dto.FolderResponse;
import tidify.tidify.exception.ErrorTypes;
import tidify.tidify.exception.ResourceNotFoundException;
import tidify.tidify.repository.FolderRepository;
import tidify.tidify.domain.User;

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;

    public Page<FolderResponse> getFolders(User user, Pageable pageable) {
        return folderRepository.findFoldersWithCount(user, pageable);
    }

    @Transactional
    public FolderResponse createFolder(FolderRequest request, User user) {
        Folder folder = Folder.of(request.getFolderName(), request.getLabel(), user);
        folderRepository.save(folder);
        return FolderResponse.of(folder);
    }

    @Transactional
    public FolderResponse modifyFolder(Long id, FolderRequest request, User user) {
        Folder folder = getFolder(id, user);
        folder.modify(request.getFolderName(), request.getLabel());

        return FolderResponse.of(folder);
    }

    @Transactional
    public void deleteFolder(Long id, User user) {
        Folder folder = getFolder(id, user);
        folder.delete();
    }

    private Folder getFolder(Long id, User user) {
        return folderRepository
            .findFolderByIdAndUser(id, user) // 이미 delete 된 건 못지우게 방어로직 추가
            .orElseThrow(() -> new ResourceNotFoundException(ErrorTypes.FOLDER_NOT_FOUND, id));
    }
}
