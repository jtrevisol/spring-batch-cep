package br.com.spring.batch.demo.processor;

import br.com.spring.batch.demo.domain.Cep;
import br.com.spring.batch.demo.dto.CepDto;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.batch.item.ItemProcessor;


@Slf4j
public class CepProcessor implements ItemProcessor<CepDto, Cep> {

    private final ModelMapper modelMapper;

    public CepProcessor(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @SneakyThrows
    @Override
    public Cep process(CepDto dto) {
        log.info("process mapping dto: {}", dto);
        return modelMapper.map(dto, Cep.class);
    }
}
