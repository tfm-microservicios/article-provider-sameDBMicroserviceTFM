package es.upm.miw.business_controllers;

import es.upm.miw.TestConfig;
import es.upm.miw.documents.Article;
import es.upm.miw.documents.Tax;
import es.upm.miw.dtos.ArticleDto;
import es.upm.miw.dtos.ArticleMinimumDto;
import es.upm.miw.dtos.out.ArticleSearchOutputDto;
import es.upm.miw.exceptions.ConflictException;
import es.upm.miw.exceptions.NotFoundException;
import es.upm.miw.repositories.ArticleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestConfig
class ArticleControllerIT {

    @Autowired
    private ArticleController articleController;

    @Autowired
    private ArticleRepository articleRepository;

    private ArticleDto articleDto;
    private Article article;


    @BeforeEach
    void seed() {
        this.articleDto = new ArticleDto("non exist", "descrip", "ref", BigDecimal.TEN, null, Tax.SUPER_REDUCED);
        this.article = new Article();
        this.article.setCode("99999999");
        this.articleRepository.save(this.article);
    }

    @Test
    void testConflictRequestException() {
        this.articleDto.setCode("8400000000017");
        assertThrows(ConflictException.class, () -> this.articleController.createArticle(this.articleDto));
    }

    @Test
    void testProviderNotFoundException() {
        this.articleDto.setProvider("non exist");
        assertThrows(NotFoundException.class, () -> this.articleController.createArticle(articleDto));
    }

    @Test
    void testInitStock() {
        assertNotNull(this.articleController.createArticle(articleDto).getStock());
        this.articleRepository.deleteById(this.articleDto.getCode());
    }

    @Test
    void testUpdateArticleNotFoundException() {
        assertThrows(NotFoundException.class, () -> this.articleController.update("miw", articleDto));
    }

    @Test
    void testUpdateArticleWithProviderNotFoundException() {
        this.articleDto.setProvider("non exist");
        assertThrows(NotFoundException.class, () -> this.articleController.update(this.article.getCode(), articleDto));
    }

    @Test
    void testUpdateArticle() {
        this.articleDto.setDescription("miw");
        ArticleDto article = this.articleController.update(this.article.getCode(), articleDto);

        assertNotNull(article);
        assertEquals("miw", article.getDescription());
    }

    @Test
    void testDeleteArticleNotExist() {
        List<ArticleSearchOutputDto> articleBeforeDelete = this.articleController.readAll();
        this.articleController.delete("miw");
        List<ArticleSearchOutputDto> articleAfterDelete = this.articleController.readAll();
        assertEquals(articleBeforeDelete.size(), articleAfterDelete.size());
    }

    @Test
    void testDeleteArticleExist() {
        List<ArticleSearchOutputDto> articleBeforeDelete = this.articleController.readAll();
        this.articleController.delete(this.article.getCode());
        List<ArticleSearchOutputDto> articleAfterDelete = this.articleController.readAll();
        assertTrue(articleBeforeDelete.size() > articleAfterDelete.size());
    }

    @Test
    void  testReadArticlesMinimum (){
        List<ArticleMinimumDto> articleMinimumDtos = this.articleController.readArticlesMinimum();
        assertNotNull(articleMinimumDtos);
    }

}
