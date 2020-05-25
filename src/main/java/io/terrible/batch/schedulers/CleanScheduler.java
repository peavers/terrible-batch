/* Licensed under Apache-2.0 */
package io.terrible.batch.schedulers;

import io.terrible.batch.domain.Directory;
import io.terrible.batch.repository.DirectoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class CleanScheduler {

  private final DirectoryRepository directoryRepository;

  private final SimpleJobLauncher simpleJobLauncher;

  @Qualifier("cleanJob")
  private final Job cleanJob;

  @Scheduled(fixedDelayString = "${batch.delay}")
  public void schedule() {

    final Directory directory = directoryRepository.findAll().get(0);

    if (directory != null) {
      execute(directory);
    }
  }

  private void execute(Directory directory) {

    final JobParameters jobParameters =
        new JobParametersBuilder()
            .addDate("date", new Date())
            .addString("directory", directory.getPath())
            .toJobParameters();

    try {
      simpleJobLauncher.run(cleanJob, jobParameters);
    } catch (Exception e) {
      log.error("Unable to run {} {} {}", cleanJob.getName(), e.getMessage(), e);
    }
  }
}