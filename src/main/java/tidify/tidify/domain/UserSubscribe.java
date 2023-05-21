package tidify.tidify.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import lombok.Getter;

@Getter
@Entity(name = "user_subscribe")
public class UserSubscribe extends BaseEntity {


    // @Column(name = "user_id")
    @OneToOne(fetch = FetchType.LAZY)
    private User userId;


    @Column(name = "folder_id")
    @OneToMany
    private List<Folder> folderId;

}
