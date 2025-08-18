package io.github.dariopipa.warehouse.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import io.github.dariopipa.warehouse.audit.AuditLogger;
import io.github.dariopipa.warehouse.dtos.requests.ProductTypesDTO;
import io.github.dariopipa.warehouse.dtos.responses.ProductTypeResponseDTO;
import io.github.dariopipa.warehouse.entities.ProductType;
import io.github.dariopipa.warehouse.enums.AuditAction;
import io.github.dariopipa.warehouse.enums.EntityType;
import io.github.dariopipa.warehouse.exceptions.ConflictException;
import io.github.dariopipa.warehouse.exceptions.EntityNotFoundException;
import io.github.dariopipa.warehouse.repositories.ProductTypeRepository;

@ExtendWith(MockitoExtension.class)
class ProductTypeServiceImplTest {

    @Mock
    private ProductTypeRepository productTypeRepository;

    @Mock
    private AuditLogger auditLogger;

    @InjectMocks
    private ProductTypeServiceImpl productTypeService;

    private ProductTypesDTO productTypesDTO;
    private ProductType productType;
    private ProductTypeResponseDTO productTypeResponseDTO;

    @BeforeEach
    void setUp() {
        productTypesDTO = new ProductTypesDTO();
        productTypesDTO.setName("Electronics");

        productType = new ProductType();
        productType.setId(1L);
        productType.setName("Electronics");

        productTypeResponseDTO = new ProductTypeResponseDTO();
        productTypeResponseDTO.setId(1L);
        productTypeResponseDTO.setName("Electronics");
    }

    @Test
    void test_GetCollection_ShouldPaginatedResponse() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductType> productTypePage = new PageImpl<>(List.of(productType));
        when(productTypeRepository.findAll(pageable)).thenReturn(productTypePage);

        Page<ProductTypeResponseDTO> result = productTypeService.getCollection(pageable);

        assertEquals(1, result.getContent().size());
        assertEquals("Electronics", result.getContent().get(0).getName());
        verify(productTypeRepository).findAll(pageable);
    }

    @Test
    void test_GetById_ShouldReturnProductType() {
        when(productTypeRepository.findById(1L)).thenReturn(Optional.of(productType));

        ProductTypeResponseDTO result = productTypeService.getById(1L);

        assertEquals("Electronics", result.getName());
        verify(productTypeRepository).findById(1L);
    }

    @Test
    void test_GetById_WithNonExistentProductType_ShouldThrowException() {
        when(productTypeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            productTypeService.getById(1L);
        });
    }

    @Test
    void test_Save_ShouldCreateProductTypeAndReturnId() {
        when(productTypeRepository.save(any(ProductType.class))).thenReturn(productType);

        Long result = productTypeService.save(productTypesDTO, 1L);

        assertEquals(1L, result);
        verify(productTypeRepository).save(any(ProductType.class));
        verify(auditLogger).log(1L, AuditAction.CREATE, EntityType.PRODUCT_TYPE, 1L);
    }

    @Test
    void test_SaveWithSameName_ShouldThrowConflictException() {
        when(productTypeRepository.existsByName("Electronics")).thenReturn(true);

        assertThrows(ConflictException.class, () -> {
            productTypeService.save(productTypesDTO, 1L);
        });
    }

    @Test
    void test_Update_ShouldUpdateProductTypeSuccessfully() {
        when(productTypeRepository.findById(1L)).thenReturn(Optional.of(productType));

        productTypeService.update(1L, productTypesDTO, 1L);

        verify(productTypeRepository).findById(1L);
        verify(auditLogger).log(1L, AuditAction.UPDATE, EntityType.PRODUCT_TYPE, 1L);
    }

    @Test
    void test_Update_WithNonExistentProductType_ShouldThrowException() {
        when(productTypeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            productTypeService.update(1L, productTypesDTO, 1L);
        });
    }

    @Test
    void test_Update_WithSameName_ShouldThrowConflictException() {
        when(productTypeRepository.findById(1L)).thenReturn(Optional.of(productType));
        when(productTypeRepository.existsByNameAndIdNot("Electronics", 1L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> {
            productTypeService.update(1L, productTypesDTO, 1L);
        });
    }

    @Test
    void test_Delete_ShouldDeleteProductTypeSuccessfully() {
        when(productTypeRepository.findById(1L)).thenReturn(Optional.of(productType));

        productTypeService.delete(1L, 1L);

        verify(productTypeRepository).delete(productType);
        verify(auditLogger).log(1L, AuditAction.DELETE, EntityType.PRODUCT_TYPE, 1L);
    }

    @Test
    void test_Delete_WithNonExistentProductType_ShouldThrowException() {
        when(productTypeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            productTypeService.delete(1L, 1L);
        });
    }

    @Test
    void test_GetProductType_ShouldReturnProductTypeEntity() {
        when(productTypeRepository.findById(1L)).thenReturn(Optional.of(productType));

        ProductType result = productTypeService.getProductType(1L);

        assertEquals(productType, result);
        verify(productTypeRepository).findById(1L);
    }

    @Test
    void test_GetProductType_WithNonExistentProductType_ShouldThrowException() {
        when(productTypeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            productTypeService.getProductType(1L);
        });
    }
}
