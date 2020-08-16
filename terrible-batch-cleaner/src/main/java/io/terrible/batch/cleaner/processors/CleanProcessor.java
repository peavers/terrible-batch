/* Licensed under Apache-2.0 */
package io.terrible.batch.cleaner.processors;

import io.terrible.batch.data.domain.MediaFile;
import io.terrible.batch.data.repository.MediaFileRepository;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;

@Slf4j
@RequiredArgsConstructor
public class CleanProcessor implements ItemProcessor<MediaFile, MediaFile> {

  private final MediaFileRepository mediaFileRepository;

  @Override
  public MediaFile process(@NonNull final MediaFile input) {

    final Path path = Paths.get(input.getPath());

    if (Files.notExists(path)) {
      log.info("Cannot find {} - Removing record", input.getName());

      if (StringUtils.isNotBlank(input.getThumbnailPath())) {
        FileUtils.deleteQuietly(new File(input.getThumbnailPath()));
      }

      mediaFileRepository.delete(input);
    }

    return null;
  }
}
