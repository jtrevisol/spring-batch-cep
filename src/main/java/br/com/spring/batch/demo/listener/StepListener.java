package br.com.spring.batch.demo.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;

import static org.springframework.batch.core.BatchStatus.STARTED;

@Slf4j
public class StepListener extends StepExecutionListenerSupport {

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        if (stepExecution.getStatus() == STARTED) {
            log.info("Step concluido!!!");
        }
        return ExitStatus.COMPLETED;
    }
}
