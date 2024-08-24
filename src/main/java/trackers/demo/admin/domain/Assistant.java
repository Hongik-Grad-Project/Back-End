package trackers.demo.admin.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trackers.demo.global.common.entity.BaseCreateTimeEntity;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Assistant extends BaseCreateTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    private String assistantId;

    private String model;

    public Assistant(final String name, final String assistantId, final String model) {
        this.name = name;
        this.assistantId = assistantId;
        this.model = model;
    }
}
