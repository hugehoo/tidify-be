package tidify.tidify.domain;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.Setter;

@Getter
// @Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false, columnDefinition = "INT(11)")
    protected Long id;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date createTimestamp;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = true, columnDefinition = "TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP")
    private Date updateTimestamp;

    // @Setter
    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0", length = 1)
    private boolean del;

    @PrePersist
    protected void onCreate() {
        createTimestamp = Timestamp.valueOf(LocalDateTime.now());
    }

    @PreUpdate
    protected void onUpdate() {
        updateTimestamp = Timestamp.valueOf(LocalDateTime.now());
    }

    protected void delete() {
        this.del = true;
    }
}
