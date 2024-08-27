package trackers.demo.global.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import trackers.demo.global.common.entity.type.StatusType;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;
import static trackers.demo.global.common.entity.type.StatusType.DELETED;
import static trackers.demo.global.common.entity.type.StatusType.USABLE;

@Getter
@MappedSuperclass
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BaseCreateTimeEntity {

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Enumerated(value = STRING)
    private StatusType status = USABLE;

    protected BaseCreateTimeEntity(final StatusType status) {
        this.status = status;
    }

    public boolean isDeleted() {
        return this.status.equals(DELETED);
    }

    public void changeStatusToDeleted() {
        this.status = DELETED;
    }
}
