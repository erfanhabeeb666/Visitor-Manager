package com.erfan.VisitorManagement.Jobs;

import com.erfan.VisitorManagement.Repos.VisitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class RetentionJob {

    private final VisitRepository visitRepository;

    @Value("${retention.days:90}")
    private int retentionDays;

    // Run daily at 02:15 AM
    @Scheduled(cron = "0 15 2 * * *")
    public void purgeOldVisits() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(retentionDays);
        long deleted = visitRepository.deleteByCreatedAtBefore(cutoff);
        if (deleted > 0) {
            log.info("Retention job: deleted {} visits older than {} days (cutoff={})", deleted, retentionDays, cutoff);
        }
    }
}
