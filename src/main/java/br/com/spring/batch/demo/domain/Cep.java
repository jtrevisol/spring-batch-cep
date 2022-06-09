package br.com.spring.batch.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ceps")

@SuppressWarnings("restriction")
@XmlRootElement(name = "ceps")
public class Cep {

    @Id
    private ObjectId objectId;

    //    @Indexed(unique = false, direction = IndexDirection.DESCENDING)
    private String cep;
    private String sigla;
    private String estado;
    private String bairro;
    private String logradouro;

}
