package es.upm.miw.rest_controllers;

import es.upm.miw.business_controllers.ArticleController;
import es.upm.miw.dtos.ArticleDto;
import es.upm.miw.dtos.ArticleMinimumDto;
import es.upm.miw.dtos.out.ArticleSearchOutputDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('OPERATOR')")
@RestController
@RequestMapping(ArticleResource.ARTICLES)
public class ArticleResource {

    public static final String ARTICLES = "/articles";
    public static final String CODE_ID = "/{code}";
    public static final String MINIMUM = "/minimum";
    public static final String PROVIDER_ID = "/provider_id/{id}";

    @Autowired
    private ArticleController articleController;

    @PostMapping
    public ArticleDto createArticle(@Valid @RequestBody ArticleDto articleDto) {
        return this.articleController.createArticle(articleDto);
    }

    @DeleteMapping(value = CODE_ID)
    public void delete(@PathVariable String code) {
        this.articleController.delete(code);
    }

    @GetMapping
    public List<ArticleSearchOutputDto> readAll() {
        return this.articleController.readAll();
    }

    @GetMapping(value = CODE_ID)
    public ArticleDto readArticle(@PathVariable String code) {
        return this.articleController.readArticle(code);
    }

    @GetMapping(value = MINIMUM)
    public List<ArticleMinimumDto> readArticlesMinimum() {
        return this.articleController.readArticlesMinimum();
    }

    @PutMapping(value = CODE_ID)
    public ArticleDto update(@PathVariable String code, @Valid @RequestBody ArticleDto articleDto) {
        return this.articleController.update(code, articleDto);
    }

}
