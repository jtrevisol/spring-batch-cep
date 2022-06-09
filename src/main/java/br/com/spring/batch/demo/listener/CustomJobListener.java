package br.com.spring.batch.demo.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;

import static org.springframework.batch.core.BatchStatus.COMPLETED;
import static org.springframework.batch.core.BatchStatus.STARTED;

@Slf4j
public class CustomJobListener extends JobExecutionListenerSupport {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == STARTED) {
            log.info("CEP BATCH PROCESS STARTED...!");
        }
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == COMPLETED) {
            log.info("CEP BATCH PROCESS COMPLETED...!");

            if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
                log.info("==========JOB FINISHED=======");
                log.info("JobId      : {}", jobExecution.getJobParameters().getString("run.id"));
                log.info("Inicio: {}", jobExecution.getCreateTime());
                log.info("Fim: {}", jobExecution.getEndTime());
                log.info("Leitura: {}", jobExecution.getStepExecutions().stream().findAny().get().getReadCount());
                log.info("Escrita: {}", jobExecution.getStepExecutions().stream().findAny().get().getWriteCount());
                log.info("==============================");
            }
        }
    }
}
