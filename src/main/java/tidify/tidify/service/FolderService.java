package tidify.tidify.service;

import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tidify.tidify.domain.Folder;
import tidify.tidify.domain.FolderSubscribe;
import tidify.tidify.domain.User;
import tidify.tidify.dto.BookmarkResponse;
import tidify.tidify.dto.CustomPage;
import tidify.tidify.dto.FolderRequest;
import tidify.tidify.dto.FolderResponse;
import tidify.tidify.exception.ErrorTypes;
import tidify.tidify.exception.ResourceNotFoundException;
import tidify.tidify.repository.FolderRepository;
import tidify.tidify.repository.FolderSubscribeRepository;
import tidify.tidify.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;
    private final FolderSubscribeRepository folderSubscribeRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public FolderResponse getFolderById(User user, Long folderId) {
        Folder folder = folderRepository.findFolderByIdAndUser(folderId, user)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorTypes.FOLDER_NOT_FOUND, folderId));
        return FolderResponse.of(folder);
    }

    public CustomPage getFolderWithBookmarks(User user, Long folderId, Pageable pageable) {
        Page<BookmarkResponse> bookmarks = folderRepository.findBookmarksByFolder(user, folderId, pageable);
        return CustomPage.of(bookmarks);
    }

    @Transactional(readOnly = true)
    public CustomPage getFolders(User user, Pageable pageable) {
        Page<FolderResponse> folders = folderRepository.findFoldersWithCount(user, pageable);
        return CustomPage.of(folders);
    }

    @Transactional
    public FolderResponse createFolder(FolderRequest request, User user) {
        Folder folder = folderRepository.save(
            Folder.of(request.getFolderName(), request.getLabel(), user)
        );
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
        updateBookmarkAsNoneFolder(user, folder);
    }

    @Transactional
    public boolean isMyFolder(Long id, User user) {
        Optional<Folder> optional = folderRepository.findFolderByIdAndUser(id, user);
        return optional.isPresent();
    }

    @Transactional // TODO : 동시성 이슈 피하려면(Optimistic Lock)?
    public FolderResponse enrollFavorite(User user, Long folderId) {
        Folder folder = folderRepository.findFolderByIdAndUser(folderId, user)
            .orElseThrow();
        folder.toggleStar();
        return FolderResponse.of(folder);
    }

    private void updateBookmarkAsNoneFolder(User user, Folder folder) {
        folderRepository.updateBookmarksAsNoneFolder(user.getId(), folder.getId());
    }

    private Folder getFolder(Long id, User user) {
        return folderRepository
            .findFolderByIdAndUser(id, user) // 이미 delete 된 건 못지우게 방어로직 추가
            .orElseThrow(() -> new ResourceNotFoundException(ErrorTypes.FOLDER_NOT_FOUND, id));
    }

    // TODO : 몇명이 구독하고 있는지도 보여주면 좋겠네.
    public CustomPage getSubscribed(User user, Pageable pageable) {
        Page<FolderResponse> folders = folderRepository.findSubscribedFolders(user, pageable);
        return CustomPage.of(folders);
    }

    public CustomPage getSubscribing(User user, Pageable pageable) {
        Page<FolderResponse> folders = folderRepository.findSubscribingFolders(user, pageable);
        return CustomPage.of(folders);
    }

    @Transactional
    public void subscribeFolder(Long folderId, String userEmail) {
        User user = userRepository.findUserByEmailAndDelFalse(userEmail).orElseThrow();
        Folder folder = folderRepository.findById(folderId).orElseThrow();
        if (isOwnFolder(user, folder)) {
            return;
        }
        if (folderSubscribeRepository.existsByUserAndFolder(user, folder)) {
            return;
        }

        folder.share();
        folderSubscribeRepository.save(
            FolderSubscribe.builder()
                .folder(folder)
                .user(user)
                .build()
        );
    }

    @Transactional
    public FolderSubscribe unSubscribeFolder(Long folderId, String userEmail) {
        User user = userRepository.findUserByEmailAndDelFalse(userEmail)
            .orElseThrow();
        Folder folder = folderRepository.findById(folderId)
            .orElseThrow();

        FolderSubscribe folderSubscribe = folderSubscribeRepository.findByUserAndFolder(user, folder)
            .orElseThrow();

        folderSubscribe.unsubscribe();
        return folderSubscribe;
    }

    @Transactional
    public boolean stopSharing(User user, Long folderId) {
        Folder folder = folderRepository.findById(folderId).orElseThrow();
        folder.unShare();
        return folderRepository.stopSharing(folderId);
    }

    private boolean isOwnFolder(User user, Folder folder) {
        return Objects.equals(folder.getUser().getId(), user.getId());
    }
}
