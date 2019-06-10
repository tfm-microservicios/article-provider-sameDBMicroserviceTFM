package es.upm.miw.dtos.in;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import es.upm.miw.dtos.ArticleMinimumDto;
import es.upm.miw.dtos.ProviderMinimunDto;

import java.util.ArrayList;
import java.util.List;

public class OrderMinimumValidationInputDto {

    @JsonInclude(Include.NON_NULL)
    private String providerId;

    @JsonInclude(Include.NON_NULL)
    private List<String> articleIds;

    public OrderMinimumValidationInputDto() {
    }

    public OrderMinimumValidationInputDto(ProviderMinimunDto providerMinimunDto, List<ArticleMinimumDto> articleMinimumDtos) {
        this.providerId = providerMinimunDto.getId();
        this.articleIds = new ArrayList<>();
        for (ArticleMinimumDto dto : articleMinimumDtos) {
            this.articleIds.add(dto.getCode());
        }
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public List<String> getArticleIds() {
        return articleIds;
    }

    public void setArticleIds(List<String> articleIds) {
        this.articleIds = articleIds;
    }

    @Override
    public String toString() {
        return "OrderMinimumValidationInputDto{" +
                "providerId='" + providerId + '\'' +
                ", articleIds=" + articleIds +
                '}';
    }
}
