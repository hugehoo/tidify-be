package tidify.tidify.service;

import java.util.List;

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

    public List<FolderResponse> getFolders(User userInfo) {
        // String email = userInfo.getEmail();
        // User user = userRepository
        //     .findWithUserRolesByEmailAndDel(email, false)
        //     .orElseThrow(() -> new ResourceNotFoundException(ErrorTypes.USER_NOT_FOUND_EXCEPTION, email));
        Long userId = 24L;
        return folderRepository.findFoldersWithCount(userId);
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