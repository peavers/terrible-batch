/* Licensed under Apache-2.0 */
package io.terrible.batch.data.repository;

import io.terrible.batch.data.domain.MediaFile;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaFileRepository extends MongoRepository<MediaFile, String> {

  List<MediaFile> findAllByOrderByCreatedTimeDesc();
}
