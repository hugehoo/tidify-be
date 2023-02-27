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
import tidify.tidify.common.security.User;

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
        Long userId = 24L; // userId
        Folder folder = Folder.of(request.getFolderName(), request.getLabel(), userId);
        folderRepository.save(folder);
        return FolderResponse.of(folder);
    }

    @Transactional
    public FolderResponse modifyFolder(Long id, FolderRequest request, User user) {
        Long userId = 24L;
        Folder folder = getFolder(id, userId);
        folder.modify(request.getFolderName(), request.getLabel());

        return FolderResponse.of(folder);
    }

    @Transactional
    public void deleteFolder(Long id, User user) {
        Long userId = 24L;
        Folder folder = getFolder(id, userId);
        folder.delete();
    }

    private Folder getFolder(Long id, Long userId) {
        return folderRepository
            .findFolderByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorTypes.FOLDER_NOT_FOUND, id));
    }
}
