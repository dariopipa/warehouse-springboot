package io.github.dariopipa.warehouse.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import io.github.dariopipa.warehouse.audit.AuditLogger;
import io.github.dariopipa.warehouse.dtos.requests.CreateProductDTO;
import io.github.dariopipa.warehouse.dtos.requests.UpdateProductRequestDTO;
import io.github.dariopipa.warehouse.dtos.requests.UpdateQuantityRequestDTO;
import io.github.dariopipa.warehouse.dtos.responses.ProductGetOneResponseDTO;
import io.github.dariopipa.warehouse.entities.Product;
import io.github.dariopipa.warehouse.entities.ProductType;
import io.github.dariopipa.warehouse.enums.AuditAction;
import io.github.dariopipa.warehouse.enums.EntityType;
import io.github.dariopipa.warehouse.enums.OperationsType;
import io.github.dariopipa.warehouse.exceptions.ConflictException;
import io.github.dariopipa.warehouse.exceptions.EntityNotFoundException;
import io.github.dariopipa.warehouse.repositories.ProductRepository;
import io.github.dariopipa.warehouse.services.interfaces.ProductTypeService;
import io.github.dariopipa.warehouse.services.interfaces.SkuGeneratorService;
import io.github.dariopipa.warehouse.services.interfaces.StockAlertService;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductTypeService productTypeService;

    @Mock
    private SkuGeneratorService skuGeneratorService;

    @Mock
    private AuditLogger auditLogger;

    @Mock
    private StockAlertService stockAlertService;

    @InjectMocks
    private ProductServiceImpl productService;

    private CreateProductDTO createProductDTO;
    private UpdateProductRequestDTO updateProductRequestDTO;
    private UpdateQuantityRequestDTO updateQuantityRequestDTO;
    private Product product;
    private ProductType productType;
    private ProductGetOneResponseDTO productResponseDTO;

    @BeforeEach
    void setUp() {
        productType = new ProductType();
        productType.setId(1L);
        productType.setName("Electronics");

        createProductDTO = new CreateProductDTO();
        createProductDTO.setName("Test Product");
        createProductDTO.setDescription("Test Description");
        createProductDTO.setQuantity(100);
        createProductDTO.setLowStockThreshold(10);
        createProductDTO.setWeight(1.5);
        createProductDTO.setHeight(10.0);
        createProductDTO.setLength(20.0);
        createProductDTO.setProductTypeId(1L);

        updateProductRequestDTO = new UpdateProductRequestDTO();
        updateProductRequestDTO.setName("Updated Product");
        updateProductRequestDTO.setDescription("Updated Description");
        updateProductRequestDTO.setProductTypeId(1L);

        updateQuantityRequestDTO = new UpdateQuantityRequestDTO();
        updateQuantityRequestDTO.setQuantity(50);
        updateQuantityRequestDTO.setOperation(OperationsType.INCREASE);

        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setSku("SKU123");
        product.setQuantity(100);
        product.setProductType(productType);

        productResponseDTO = new ProductGetOneResponseDTO();
        productResponseDTO.setId(1L);
        productResponseDTO.setName("Test Product");
        productResponseDTO.setDescription("Test Description");
        productResponseDTO.setSku("SKU123");
        productResponseDTO.setQuantity(100);
    }

    @Test
    void test_Save_ShouldCreateProductAndReturnId() {
        when(productTypeService.getProductType(1L)).thenReturn(productType);
        when(skuGeneratorService.generateSku(anyString(), anyString())).thenReturn("SKU123");
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Long result = productService.save(createProductDTO, 1L);

        assertEquals(1L, result);
        verify(productRepository).save(any(Product.class));
        verify(auditLogger).log(1L, AuditAction.CREATE, EntityType.PRODUCT, 1L);
    }

    @Test
    void test_Save_WithDataIntegrityViolation_ShouldThrowConflictException() {
        when(productTypeService.getProductType(1L)).thenReturn(productType);
        when(skuGeneratorService.generateSku(anyString(), anyString())).thenReturn("SKU123");
        when(productRepository.save(any(Product.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate SKU"));

        assertThrows(ConflictException.class, () -> {
            productService.save(createProductDTO, 1L);
        });
    }

    @Test
    void test_Update_ShouldUpdateProductSuccessfully() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productTypeService.getProductType(1L)).thenReturn(productType);

        productService.update(1L, updateProductRequestDTO, 1L);

        verify(productRepository).findById(1L);
        verify(auditLogger).log(1L, AuditAction.UPDATE, EntityType.PRODUCT, 1L);
    }

    @Test
    void test_Update_WithNonExistentProduct_ShouldThrowException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            productService.update(1L, updateProductRequestDTO, 1L);
        });
    }

    @Test
    void test_Delete_ShouldDeleteProductSuccessfully() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.delete(1L, 1L);

        verify(productRepository).delete(product);
        verify(auditLogger).log(1L, AuditAction.DELETE, EntityType.PRODUCT, 1L);
    }

    @Test
    void test_Delete_WithNonExistentProduct_ShouldThrowException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            productService.delete(1L, 1L);
        });
    }

    @Test
    void test_GetCollection_ShouldReturnPaginatedResponse() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(List.of(product));
        when(productRepository.findAll(pageable)).thenReturn(productPage);

        Page<ProductGetOneResponseDTO> result = productService.getCollection(pageable);

        assertEquals(1, result.getContent().size());
        verify(productRepository).findAll(pageable);
    }

    @Test
    void test_GetById_ShouldReturnProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductGetOneResponseDTO result = productService.getById(1L);

        assertEquals("Test Product", result.getName());
        verify(productRepository).findById(1L);
    }

    @Test
    void test_GetById_WithNonExistentProduct_ShouldThrowException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            productService.getById(1L);
        });
    }

    @Test
    void test_UpdateQuantity_WithIncreaseOperation_ShouldIncreaseQuantity() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.updateQuantity(1L, updateQuantityRequestDTO, 1L);

        verify(productRepository).findById(1L);
        verify(auditLogger).logQuantityUpdate(1L, EntityType.PRODUCT, 1L, OperationsType.INCREASE, 150);
    }

    @Test
    void test_UpdateQuantity_WithDecreaseOperation_ShouldDecreaseQuantity() {
        updateQuantityRequestDTO.setOperation(OperationsType.DECREASE);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.updateQuantity(1L, updateQuantityRequestDTO, 1L);

        verify(productRepository).findById(1L);
        verify(auditLogger).logQuantityUpdate(1L, EntityType.PRODUCT, 1L, OperationsType.DECREASE, 50);
    }

    @Test
    void test_UpdateQuantity_WithNonExistentProduct_ShouldThrowException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            productService.updateQuantity(1L, updateQuantityRequestDTO, 1L);
        });
    }

    @Test
    void test_GetProductEntityById_ShouldReturnProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product result = productService.getProductEntityById(1L);

        assertEquals(product, result);
        verify(productRepository).findById(1L);
    }

    @Test
    void test_GetProductEntityById_WithNonExistentProduct_ShouldThrowException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            productService.getProductEntityById(1L);
        });
    }
}
