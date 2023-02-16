package tidify.tidify.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tidify.tidify.domain.Folder;
import tidify.tidify.dto.FolderRequest;
import tidify.tidify.dto.FolderResponse;
import tidify.tidify.exception.UserNotFoundException;
import tidify.tidify.repository.FolderRepository;
import tidify.tidify.repository.UserRepository;
import tidify.tidify.security.User;

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;
    private final UserRepository userRepository;

    public List<FolderResponse> getFolders(String email) {
        User user = userRepository
            .findWithUserRolesByEmailAndDel(email, false)
            .orElseThrow(UserNotFoundException::new);

        return folderRepository.findFoldersWithCount(user.getId());
    }

    @Transactional
    public FolderResponse createFolder(FolderRequest request, User user) {
        // userId
        Long userId = 24L;
        Folder folder = Folder.of(request.getFolderName(), request.getLabel(), userId);
        folderRepository.save(folder);
        return FolderResponse.of(folder);
    }
}
