/**
 *
 */
package br.com.spring.batch.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
@Slf4j
public class TaskExecutorConfiguration {
    @Bean
    public TaskExecutor simpleAsyncTaskExecutor() {
        SimpleAsyncTaskExecutor simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor();
        simpleAsyncTaskExecutor.setConcurrencyLimit(100);
        simpleAsyncTaskExecutor.setThreadNamePrefix("Task-Custom-");
        return simpleAsyncTaskExecutor;
    }
}
