package tidify.tidify.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tidify.tidify.domain.Folder;
import tidify.tidify.dto.FolderRequest;
import tidify.tidify.dto.FolderResponse;
import tidify.tidify.common.exception.ErrorTypes;
import tidify.tidify.common.exception.ResourceNotFoundException;
import tidify.tidify.repository.FolderRepository;
import tidify.tidify.repository.UserRepository;
import tidify.tidify.domain.User;

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;
    private final UserRepository userRepository;

    public Page<FolderResponse> getFolders(User user, Pageable pageable) {
        // String email = userInfo.getEmail();
        // User user = userRepository.findUserByEmailAndDelFalse(email)
        //     .orElseThrow(() -> new ResourceNotFoundException(ErrorTypes.USER_NOT_FOUND_EXCEPTION, email));
        return folderRepository.findFoldersWithCount(user.getId(), pageable);
    }

    @Transactional
    public FolderResponse createFolder(FolderRequest request, User user) {
        Folder folder = Folder.of(request.getFolderName(), request.getLabel(), user.getId());
        folderRepository.save(folder);
        return FolderResponse.of(folder);
    }

    @Transactional
    public FolderResponse modifyFolder(Long id, FolderRequest request, User user) {
        Folder folder = getFolder(id, user.getId());
        folder.modify(request.getFolderName(), request.getLabel());

        return FolderResponse.of(folder);
    }

    @Transactional
    public void deleteFolder(Long id, User user) {
        Folder folder = getFolder(id, user.getId());
        folder.delete();
    }

    private Folder getFolder(Long id, Long userId) {
        return folderRepository
            .findFolderByIdAndUserId(id, userId) // 이미 delete 된 건 못지우게 방어로직 추가
            .orElseThrow(() -> new ResourceNotFoundException(ErrorTypes.FOLDER_NOT_FOUND, id));
    }
}
