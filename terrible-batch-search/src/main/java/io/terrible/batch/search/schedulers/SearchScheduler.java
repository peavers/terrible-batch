/* Licensed under Apache-2.0 */
package io.terrible.batch.search.schedulers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class SearchScheduler {

  private final SimpleJobLauncher simpleJobLauncher;

  @Qualifier("searchJob")
  private final Job searchJob;

  @Scheduled(fixedDelayString = "${batch.search.delay}")
  public void schedule()
      throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
          JobRestartException, JobInstanceAlreadyCompleteException {

    simpleJobLauncher.run(
        searchJob, new JobParametersBuilder().addDate("date", new Date()).toJobParameters());
  }
}