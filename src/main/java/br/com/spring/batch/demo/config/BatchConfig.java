package br.com.spring.batch.demo.config;

import br.com.spring.batch.demo.domain.Cep;
import br.com.spring.batch.demo.dto.CepDto;
import br.com.spring.batch.demo.listener.CustomJobListener;
import br.com.spring.batch.demo.listener.StepListener;
import br.com.spring.batch.demo.processor.CepProcessor;
import br.com.spring.batch.demo.repository.CepRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import java.util.Arrays;

@Slf4j
@Configuration
@EnableBatchProcessing
public class BatchConfig {

    public static final String DELIMITER = "\t";
    public static final String INPUT = "file:src/main/resources/csv/utfcepos.csv";
    public static final String OUTPUT = "file:src/main/resources/xml/cepOtput.xml";
    public static final int CHUNK_SIZE = 10000;
    protected static final String[] NAMES = {"cep", "estado", "cidade", "bairro", "logradouro"};
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final CepRepository cepRepository;
    private final ModelMapper modelMapper;

    private final Resource outputXml;
    private final Resource inputCsv;

    public BatchConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
                       CepRepository cepRepository, ModelMapper modelMapper,
                       @Value(OUTPUT) Resource outputXml,
                       @Value(INPUT) Resource inputCsv,
                       @Value("${spring.application.name}") String consul) {

        log.info("Start Application {}", consul);
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.cepRepository = cepRepository;
        this.modelMapper = modelMapper;
        this.outputXml = outputXml;
        this.inputCsv = inputCsv;
    }

    @Bean("jobCep")
    public Job job(TaskExecutor taskExecutor) {
        return jobBuilderFactory.get("jobCep")
                .incrementer(new RunIdIncrementer())
                .listener(new CustomJobListener())
                .flow(step1(taskExecutor))
                .next(step2())
                .end()
                .build();
    }

    @Bean("step1-chunk")
    public Step step1(TaskExecutor taskExecutor) {
        return stepBuilderFactory.get("step1")
                .<CepDto, Cep>chunk(CHUNK_SIZE)
                .reader(reader())
                .processor(processor())
                .writer(writerMongodb()) //compositeItemWriter())
                .faultTolerant()
                .skipLimit(1).skip(FlatFileParseException.class)
                .taskExecutor(taskExecutor)
//                .throttleLimit(5)
//                .retry(ConnectTimeoutException.class).retryLimit(3)
                .listener(new StepListener())
                .build();
    }

    @Bean("step2-tasklet")
    public Step step2() {
        return stepBuilderFactory
                .get("step2")
                .tasklet((contribution, chunkContext) -> {
                    log.info("Email -> Fim do Processo!!");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean("reader")
    public FlatFileItemReader<CepDto> reader() {

        final BeanWrapperFieldSetMapper beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper();
        beanWrapperFieldSetMapper.setTargetType(CepDto.class);

        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
        delimitedLineTokenizer.setNames(NAMES);
        delimitedLineTokenizer.setDelimiter(DELIMITER);

        return new FlatFileItemReaderBuilder<CepDto>()
                .name("readerMapper")
                .resource(inputCsv)
                .lineTokenizer(delimitedLineTokenizer)
                .fieldSetMapper(beanWrapperFieldSetMapper)
//                .linesToSkip(1)
                .build();
    }

    @Bean("processor")
    public CepProcessor processor() {
        return new CepProcessor(this.modelMapper);
    }

    @Bean(name = "writerXml", destroyMethod = "")
    public ItemWriter<Cep> writerXml() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(Cep.class);
        StaxEventItemWriter<Cep> itemWriter = new StaxEventItemWriter<>();
        itemWriter.setMarshaller(marshaller);
        itemWriter.setRootTagName("ceps");
        itemWriter.setResource(outputXml);
        return itemWriter;
    }

    @Bean("writerMongodb")
    public ItemWriter<Cep> writerMongodb() {
        return itens -> {
            log.info("Writer itens size -> {}", itens.size());
            cepRepository.saveAll(itens).subscribe();
        };
    }

    public CompositeItemWriter<Cep> compositeItemWriter() {
        final CompositeItemWriter writer = new CompositeItemWriter();
        writer.setDelegates(Arrays.asList(writerMongodb(), writerXml()));
        return writer;
    }

//    @Bean
//    public JdbcBatchItemWriter<Cep> writer(final DataSource dataSource) {
//        return new JdbcBatchItemWriterBuilder<Cep>()
//                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
//                .sql("INSERT INTO ceps (cep, estado, cidade, bairro, logradouro) VALUES (:cep, :estado, :cidade, :bairro, :logradouro)")
//                .dataSource(dataSource)
//                .build();
//    }

}
