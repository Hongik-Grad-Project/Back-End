package trackers.demo.project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import trackers.demo.global.common.entity.BaseTimeEntity;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE project_tag SET status = 'DELETED' WHERE id = ?")
@Where(clause = "status = 'USABLE'")
public class ProjectTag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    public ProjectTag(Long id, Project project, Tag tag) {
        this.id = id;
        this.project = project;
        this.tag = tag;
    }
}
