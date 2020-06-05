/* Licensed under Apache-2.0 */
package io.terrible.batch.search.jobs;

import io.micrometer.core.instrument.search.Search;
import io.terrible.batch.data.domain.MediaFile;
import io.terrible.batch.search.listeners.SearchJobListener;
import io.terrible.batch.search.processors.SearchProcessor;
import io.terrible.batch.search.services.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@EnableScheduling
@EnableBatchProcessing
@RequiredArgsConstructor
public class SearchBatch {

  private final JobBuilderFactory jobBuilderFactory;

  private final StepBuilderFactory stepBuilderFactory;

  private final MongoTemplate mongoTemplate;

  private final SearchService searchService;

  @StepScope
  @Bean(name = "searchReader")
  public ItemReader<MediaFile> reader() {

    final MongoItemReader<MediaFile> reader = new MongoItemReader<>();

    final Map<String, Sort.Direction> map = new HashMap<>();
    map.put("_id", Sort.Direction.DESC);

    reader.setTemplate(mongoTemplate);
    reader.setSort(map);
    reader.setTargetType(MediaFile.class);
    reader.setQuery("{}");
    reader.setSaveState(false);

    return reader;
  }

  @Bean(name = "searchProcessor")
  public SearchProcessor processor() {
    return new SearchProcessor(searchService);
  }

  @Bean(name = "searchWriter")
  public ItemWriter<MediaFile> writer() {

    final MongoItemWriter<MediaFile> writer = new MongoItemWriter<>();
    writer.setCollection("media-files");
    writer.setTemplate(mongoTemplate);

    return writer;
  }

  @Bean(name = "searchStep")
  public Step searchStep() {

    return stepBuilderFactory
        .get("searchStep")
        .<MediaFile, MediaFile>chunk(100)
        .reader(reader())
        .processor(processor())
        .writer(writer())
        .build();
  }

  @Bean(name = "searchJob")
  public Job searchJob() {

    return jobBuilderFactory
        .get("searchJob")
        .listener(new SearchJobListener(searchService))
        .incrementer(new RunIdIncrementer())
        .flow(searchStep())
        .end()
        .build();
  }
}