package tidify.tidify.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tidify.tidify.domain.Folder;
import tidify.tidify.domain.User;
import tidify.tidify.dto.BookmarkResponse;
import tidify.tidify.dto.CustomPage;
import tidify.tidify.dto.FolderRequest;
import tidify.tidify.dto.FolderResponse;
import tidify.tidify.exception.ErrorTypes;
import tidify.tidify.exception.ResourceNotFoundException;
import tidify.tidify.repository.FolderRepository;

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;

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
        // 자기 userId 로 공유 folder_id 조회 -> 기존 folders + 구독 folders
        // folderResponse 에도 플래그 붙여줘야 할듯.
        // 구독 폴더라도, 자기가 만든 폴더면 다른 플래그가 필요할듯.
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

    private void updateBookmarkAsNoneFolder(User user, Folder folder) {
        folderRepository.updateBookmarksAsNoneFolder(user.getId(), folder.getId());
    }

    private Folder getFolder(Long id, User user) {
        return folderRepository
            .findFolderByIdAndUser(id, user) // 이미 delete 된 건 못지우게 방어로직 추가
            .orElseThrow(() -> new ResourceNotFoundException(ErrorTypes.FOLDER_NOT_FOUND, id));
    }

    @Transactional
    public void setSharingFolder(User user, Long id) {
        Folder folder = folderRepository.findFolderByIdAndUser(id, user)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorTypes.FOLDER_NOT_FOUND, id));
        folder.setSharingFolder();
    }

    @Transactional
    public void unSetSharingFolder(User user, Long id) {
        Folder folder = folderRepository.findFolderByIdAndUser(id, user)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorTypes.FOLDER_NOT_FOUND, id));
        folder.unSetSharingFolder();
    }
}
