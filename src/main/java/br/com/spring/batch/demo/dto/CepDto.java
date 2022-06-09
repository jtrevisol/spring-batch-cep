package br.com.spring.batch.demo.dto;

import lombok.Data;

@Data
public class CepDto {

    private String cep;
    private String estado;
    private String cidade;
    private String bairro;
    private String logradouro;
}
