package es.upm.miw.business_controllers;

import es.upm.miw.TestConfig;
import es.upm.miw.dtos.ProviderDto;
import es.upm.miw.dtos.ProviderMinimunDto;
import es.upm.miw.dtos.in.OrderMinimumValidationInputDto;
import es.upm.miw.exceptions.BadRequestException;
import es.upm.miw.exceptions.ConflictException;
import es.upm.miw.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestConfig
public class ProviderControllerIT {

    @Autowired
    private ProviderController providerController;

    @Autowired
    private ArticleController articleController;

    @Test
    void testReadAll() {
        List<ProviderMinimunDto> providers = providerController.readAll();
        //System.out.println(providers);
        assertTrue(providers.size() > 1);
    }

    @Test
    void testRealAllActives() {
        List<ProviderMinimunDto> providers = providerController.readAllActives();
        assertTrue(providers.size() > 1);
    }

    @Test
    void testRead() {
        String existentId = providerController.readAll().get(0).getId();
        ProviderDto provider = providerController.read(existentId);
    }

    @Test
    void testReadNotFound() {
        assertThrows(NotFoundException.class, () -> providerController.read("non-existent-id"));
    }

    @Test
    void testCreate() {
        ProviderDto providerDto = new ProviderDto("new-company");
        this.providerController.create(providerDto);
        assertThrows(ConflictException.class, () -> this.providerController.create(providerDto));
    }

    @Test
    void testUpdate() {
        ProviderDto providerDto = new ProviderDto("update-company");
        providerDto = this.providerController.create(providerDto);
        String updatedNif = "updated-nif";
        providerDto.setNif(updatedNif);
        ProviderDto result = this.providerController.update(providerDto.getId(), providerDto);
        assertEquals(updatedNif, result.getNif());
    }

    @Test
    void testUpdateNotFound() {
        ProviderDto providerDto = new ProviderDto();
        providerDto.setId("no-existent-id");
        assertThrows(NotFoundException.class, () -> this.providerController.update(providerDto.getId(), providerDto));
    }

    @Test
    void testUpdateNoId() {
        ProviderDto providerDto = new ProviderDto("no-id");
        assertThrows(BadRequestException.class, () -> this.providerController.update(null, providerDto));
    }

    @Test
    void testUpdateDifferentId() {
        ProviderDto providerDto = new ProviderDto("different-id");
        providerDto = this.providerController.create(providerDto);
        String id = providerDto.getId();
        providerDto.setId("different-id");
        ProviderDto finalProviderDto = providerDto;
        assertThrows(BadRequestException.class, () -> this.providerController.update(id, finalProviderDto));
    }

    @Test
    void testUpdateSameCompany() {
        ProviderDto providerDto = new ProviderDto("same-company");
        providerDto = this.providerController.create(providerDto);
        providerDto = new ProviderDto("another-company");
        providerDto = this.providerController.create(providerDto);
        String id = providerDto.getId();
        providerDto.setCompany("same-company");
        ProviderDto finalProviderDto = providerDto;
        assertThrows(ConflictException.class, () -> this.providerController.update(id, finalProviderDto));
    }

    @Test
    void testDelete() {
        ProviderMinimunDto providerDto = providerController.readAll().get(0);
        assertNotNull(providerDto);
        providerController.delete(providerDto.getId());
        assertThrows(NotFoundException.class, () -> providerController.read(providerDto.getId()));
        providerController.create(new ProviderDto(providerDto.getCompany()));
    }

    @Test
    void testValidatePass() {
        List<OrderMinimumValidationInputDto> dtos = new ArrayList<>();
        dtos.add(
                new OrderMinimumValidationInputDto(
                        this.providerController.readAll().get(0),
                        this.articleController.readArticlesMinimum()
                )
        );
        assertDoesNotThrow(() -> providerController.validatePresence(dtos));
    }

    @Test
    void testValidateNotPassEmptyDto (){
        assertThrows(NullPointerException.class, ()-> providerController.validatePresence(null));
    }

    @Test
    void testValidateNotPass (){
        List<OrderMinimumValidationInputDto> dtos = new ArrayList<>();
        dtos.add(
                new OrderMinimumValidationInputDto(
                        this.providerController.readAll().get(0),
                        this.articleController.readArticlesMinimum()
                )
        );
        dtos.get(0).setArticleIds(new ArrayList<>(Arrays.asList("20")));
        assertThrows(NotFoundException.class, ()->providerController.validatePresence(dtos));
        dtos.get(0).setProviderId("20");
        assertThrows(NotFoundException.class, ()->providerController.validatePresence(dtos));
    }

}
