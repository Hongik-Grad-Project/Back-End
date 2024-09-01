package trackers.demo.global.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
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
public class BaseTimeEntity extends BaseCreateTimeEntity{

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

}
