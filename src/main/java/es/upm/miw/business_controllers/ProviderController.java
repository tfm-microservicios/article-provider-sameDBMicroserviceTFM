package es.upm.miw.business_controllers;

import es.upm.miw.documents.Provider;
import es.upm.miw.dtos.ProviderDto;
import es.upm.miw.dtos.ProviderMinimunDto;
import es.upm.miw.dtos.in.OrderMinimumValidationInputDto;
import es.upm.miw.exceptions.BadRequestException;
import es.upm.miw.exceptions.ConflictException;
import es.upm.miw.exceptions.NotFoundException;
import es.upm.miw.repositories.ArticleRepository;
import es.upm.miw.repositories.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
public class ProviderController {

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private ArticleRepository articleRepository;

    public ProviderDto read(String id) {
        return new ProviderDto(this.providerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Provider id(" + id + ")")));
    }

    public List<ProviderMinimunDto> readAll() {
        return this.providerRepository.findAllProviders();
    }

    public List<ProviderMinimunDto> readAllActives() {
        return this.providerRepository.findByActiveTrue();
    }

    public ProviderDto create(ProviderDto providerDto) {
        String company = providerDto.getCompany();
        if (this.providerRepository.findByCompany(company).isPresent())
            throw new ConflictException("Provider company (" + company + ")");
        Provider provider = new Provider(providerDto);
        this.providerRepository.save(provider);
        return new ProviderDto(provider);
    }

    public ProviderDto update(String id, ProviderDto providerDto) {
        if (id == null || !id.equals(providerDto.getId()))
            throw new BadRequestException("Provider id (" + providerDto.getId() + ")");
        if (!this.providerRepository.findById(id).isPresent())
            throw new NotFoundException("Provider id (" + id + ")");
        String company = providerDto.getCompany();
        Optional<Provider> provider = this.providerRepository.findByCompany(company);
        if (provider.isPresent() && !provider.get().getId().equals(id))
            throw new ConflictException("Provider company (" + company + ")");
        Provider result = this.providerRepository.save(new Provider(providerDto));
        return new ProviderDto(result);
    }

    public void delete(String code) {
        if (this.providerRepository.findById(code).isPresent()) {
            this.providerRepository.deleteById(code);
        }
    }

    public void validatePresence(List<OrderMinimumValidationInputDto> dtos) {
        for (OrderMinimumValidationInputDto dto : dtos) {
            if (!this.providerRepository.findById(dto.getProviderId()).isPresent()) {
                throw new NotFoundException("ProviderId" + dto.getProviderId());
            }
            for (String articleId : dto.getArticleIds()) {
                if (!this.articleRepository.findById(articleId).isPresent()){
                    throw new NotFoundException("ArticleId" + articleId);
                }
            }
        }
    }

}
