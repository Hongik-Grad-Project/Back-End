package trackers.demo.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import trackers.demo.project.domain.Project;
import trackers.demo.project.domain.repository.ProjectRepository;
import trackers.demo.project.domain.type.CompletedStatusType;

import java.time.LocalDate;
import java.util.List;

import static trackers.demo.project.domain.type.CompletedStatusType.*;

@Service
@RequiredArgsConstructor
public class ProjectStatusScheduler {

    private final ProjectRepository projectRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void updateCompletedStatus() {
        LocalDate today = LocalDate.now();

        List<Project> projects = projectRepository.findByEndDateBeforeAndCompletedStatus(today, COMPLETED);
        for(final Project project : projects) {
            project.updateCompletedStatus(CLOSED);
        }

        projectRepository.saveAll(projects);
    }
}
