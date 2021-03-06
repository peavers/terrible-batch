/* Licensed under Apache-2.0 */
package io.terrible.batch.thumbnails.schedulers;

import io.terrible.batch.data.domain.MediaFile;
import io.terrible.batch.data.repository.MediaFileRepository;
import io.terrible.batch.thumbnails.processors.ThumbnailProcessor;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class ThumbnailGeneratorScheduler {

  private final ExecutorService executor;

  private final MediaFileRepository mediaFileRepository;

  private final ThumbnailProcessor processor;

  @Async
  @Scheduled(fixedDelay = 900000)
  public void execute() {

    log.info("ThumbnailGeneratorScheduler started");

    mediaFileRepository.findAllByOrderByCreatedTimeDesc().stream()
        .filter(mediaFile -> !mediaFile.isIgnored())
        .filter(mediaFile -> mediaFile.getThumbnails().size() < 12)
        .forEach(consume());

    log.info("ThumbnailGeneratorScheduler finished");
  }

  private Consumer<MediaFile> consume() {
    return mediaFile ->
        executor.submit(() -> mediaFileRepository.save(processor.process(mediaFile)));
  }
}
