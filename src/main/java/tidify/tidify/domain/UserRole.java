package tidify.tidify.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "user_role")
@DynamicUpdate
public class UserRole extends BaseEntity implements GrantedAuthority {

    @Id
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "FK_USER_ROLE_USER"))
    private User user;

    @Column(name = "role_name", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private RoleType roleName;

    @Override
    @JsonIgnore
    public String getAuthority() {
        return this.roleName.name();
    }
}
