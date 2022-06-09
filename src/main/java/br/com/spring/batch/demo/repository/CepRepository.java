package br.com.spring.batch.demo.repository;

import br.com.spring.batch.demo.domain.Cep;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CepRepository extends ReactiveMongoRepository<Cep, ObjectId> {
}
