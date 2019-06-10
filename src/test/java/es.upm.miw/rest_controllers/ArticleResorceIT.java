package es.upm.miw.rest_controllers;

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
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ApiTestConfig
class ArticleResorceIT {

    @Autowired
    private RestService restService;

    @Autowired
    private ArticleRepository articleRepository;

    private ArticleDto articleDto;
    private Article article;

    @BeforeEach
    void seed() {
        this.articleDto = new ArticleDto("miw-dto", "descrip", "ref", BigDecimal.TEN, null, Tax.SUPER_REDUCED);
        this.article = new Article();
        this.article.setCode("99999999");
        this.articleRepository.save(this.article);
    }

    @Test
    void testReadArticleOne() {
        ArticleDto articleDto = this.restService.loginAdmin().restBuilder(new RestBuilder<ArticleDto>()).clazz(ArticleDto.class)
                .path(ArticleResource.ARTICLES).path(ArticleResource.CODE_ID).expand("1")
                .get().build();
        assertNotNull(articleDto);
    }

    @Test
    void testReadArticleNonExist() {
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () ->
                this.restService.loginAdmin().restBuilder(new RestBuilder<NotFoundException>()).clazz(NotFoundException.class)
                        .path(ArticleResource.ARTICLES).path(ArticleResource.CODE_ID).expand("kk")
                        .get().build());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void testCreateArticleRepeated() {
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () ->
                this.restService.loginAdmin().restBuilder(new RestBuilder<ConflictException>()).clazz(ConflictException.class)
                        .path(ArticleResource.ARTICLES)
                        .body(new ArticleDto("8400000000017", "repeated", "", BigDecimal.TEN, 10, Tax.GENERAL))
                        .post().build());
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    }


    @Test
    void testCreateArticleNegativePrice() {
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () ->
                this.restService.loginAdmin().restBuilder()
                        .path(ArticleResource.ARTICLES)
                        .body(new ArticleDto("4800000000011", "new", "", new BigDecimal("-1"), 10, Tax.REDUCED))
                        .post().build());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void testCreateArticleWithoutCodeNextCodeEanNotImplemented() {
        HttpServerErrorException.InternalServerError exception = assertThrows(HttpServerErrorException.InternalServerError.class, () ->
                this.restService.loginAdmin().restBuilder(new RestBuilder<HttpServerErrorException.InternalServerError>())
                        .clazz(HttpServerErrorException.InternalServerError.class)
                        .path(ArticleResource.ARTICLES)
                        .body(new ArticleDto(null, "new", "", BigDecimal.TEN, 10, Tax.FREE))
                        .post().build());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
    }

    @Test
    void testUpdateArticleNotExist() {
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () ->
                this.restService.loginAdmin().restBuilder(new RestBuilder<ArticleDto>()).clazz(ArticleDto.class)
                        .path(ArticleResource.ARTICLES).path("/miw").body(this.articleDto).put().build());

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void testUpdateArticleExist() {
        this.articleDto.setDescription("miw");
        ArticleDto articleOutputDto = this.restService.loginAdmin().restBuilder(new RestBuilder<ArticleDto>()).clazz(ArticleDto.class)
                .path(ArticleResource.ARTICLES).path(ArticleResource.CODE_ID).expand("99999999").body(this.articleDto).put().build();

        assertEquals(this.articleDto.getDescription(), articleOutputDto.getDescription());
    }

    @Test
    void testDeleteArticleExist() {
        List<ArticleSearchOutputDto> articlesBeforeDelete = readAllArticles();

        this.restService.loginAdmin().restBuilder(new RestBuilder<ArticleDto>()).clazz(ArticleDto.class)
                .path(ArticleResource.ARTICLES).path(ArticleResource.CODE_ID).expand("99999999").delete().build();

        List<ArticleSearchOutputDto> articlesAfterDelete = readAllArticles();

        assertTrue(articlesBeforeDelete.size() > articlesAfterDelete.size());

    }

    @Test
    void testDeleteArticleNotExist() {
        List<ArticleSearchOutputDto> articlesBeforeDelete = readAllArticles();

        this.restService.loginAdmin().restBuilder(new RestBuilder<ArticleDto>()).clazz(ArticleDto.class)
                .path(ArticleResource.ARTICLES).path(ArticleResource.CODE_ID).expand("miw").delete().build();

        List<ArticleSearchOutputDto> articlesAfterDelete = readAllArticles();

        assertEquals(articlesBeforeDelete.size(), articlesAfterDelete.size());

    }

    private List<ArticleSearchOutputDto> readAllArticles() {
        return Arrays.asList(this.restService.loginAdmin().restBuilder(new RestBuilder<ArticleSearchOutputDto[]>())
                .clazz(ArticleSearchOutputDto[].class).path(ArticleResource.ARTICLES)
                .get().build());
    }

    @Test
    void testReadAllArticles() {
        List<ArticleSearchOutputDto> articles = readAllArticles();

        assertNotNull(articles);
        assertTrue(articles.size() > 0);
    }

    @Test
    void testReadArticlesMinimum (){
        ArticleMinimumDto [] articleMinimumDto = this.restService.loginAdmin().restBuilder(new RestBuilder<ArticleMinimumDto[]>())
                .clazz(ArticleMinimumDto[].class).path(ArticleResource.ARTICLES).path(ArticleResource.MINIMUM)
                .get().build();
        assertNotNull(articleMinimumDto);
    }

}
