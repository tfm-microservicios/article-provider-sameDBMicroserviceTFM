package es.upm.miw.data_services;

import es.upm.miw.documents.*;

import java.util.ArrayList;
import java.util.List;

public class DatabaseGraph {

    private List<Article> articleList;
    private List<Provider> providerList;

    public DatabaseGraph() {
        this.articleList = new ArrayList<>();
        this.providerList = new ArrayList<>();
    }

    public List<Article> getArticleList() {
        return articleList;
    }

    public void setArticleList(List<Article> articleList) {
        this.articleList = articleList;
    }

    public List<Provider> getProviderList() {
        return providerList;
    }

    public void setProviderList(List<Provider> providerList) {
        this.providerList = providerList;
    }
}
