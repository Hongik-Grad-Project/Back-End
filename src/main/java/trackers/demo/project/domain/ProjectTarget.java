package trackers.demo.project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import trackers.demo.global.common.entity.BaseCreateTimeEntity;
import trackers.demo.global.common.entity.BaseTimeEntity;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE project_target SET status = 'DELETED' WHERE id = ?")
@Where(clause = "status = 'USABLE'")
public class ProjectTarget extends BaseCreateTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id")
    private Target target;

    public ProjectTarget(Long id, Project project, Target target) {
        this.id = id;
        this.project = project;
        this.target = target;
    }
}
