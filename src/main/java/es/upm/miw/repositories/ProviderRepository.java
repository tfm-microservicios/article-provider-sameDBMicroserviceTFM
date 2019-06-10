package es.upm.miw.repositories;

import es.upm.miw.documents.Provider;
import es.upm.miw.dtos.ProviderMinimunDto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProviderRepository extends MongoRepository<Provider, String> {

    Optional<Provider> findByCompany(String company);

    List<ProviderMinimunDto> findByActiveTrue();

    @Query(value = "{}", fields = "{ 'company' : 1, 'nif' : 1}")
    List<ProviderMinimunDto> findAllProviders();

}
