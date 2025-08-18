package io.github.dariopipa.warehouse.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SkuGeneratorServiceImplTest {

    @InjectMocks
    private SkuGeneratorServiceImpl skuGeneratorService;

    @Test
    void test_GenerateSku_ShouldReturnCorrectFormat() {
        String sku = skuGeneratorService.generateSku("Laptop", "Electronics");

        assertNotNull(sku);
        assertTrue(sku.matches("LA-EL-\\d{4}-\\d{5}"));
    }

    @Test
    void test_GenerateSku_ShouldUseFirstTwoCharactersOfProductName() {
        String sku = skuGeneratorService.generateSku("Phone", "Electronics");

        assertTrue(sku.startsWith("PH-"));
    }

    @Test
    void test_GenerateSku_ShouldUseFirstTwoCharactersOfProductType() {
        String sku = skuGeneratorService.generateSku("Laptop", "Computers");

        assertTrue(sku.contains("-CO-"));
    }

    @Test
    void test_GenerateSku_ShouldIncludeCurrentYear() {
        String sku = skuGeneratorService.generateSku("Mouse", "Accessories");

        assertTrue(sku.contains("-" + String.valueOf(java.time.LocalDate.now().getYear()) + "-"));
    }

    @Test
    void test_GenerateSku_ShouldHandleShortProductNames() {
        String sku = skuGeneratorService.generateSku("TV", "Electronics");

        assertTrue(sku.startsWith("TV-EL-"));
    }

    @Test
    void test_GenerateSku_ShouldHandleLowercaseInput() {
        String sku = skuGeneratorService.generateSku("laptop", "electronics");

        assertTrue(sku.startsWith("LA-EL-"));
    }

    @Test
    void test_GenerateSku_ShouldHandleMixedCaseInput() {
        String sku = skuGeneratorService.generateSku("LaPtOp", "ElEcTrOnIcS");

        assertTrue(sku.startsWith("LA-EL-"));
    }

    @Test
    void test_GenerateSku_ShouldHaveFiveDigitTimestamp() {
        String sku = skuGeneratorService.generateSku("Test", "Product");

        String[] parts = sku.split("-");
        assertEquals(4, parts.length);
        assertEquals(5, parts[3].length());
        assertTrue(parts[3].matches("\\d{5}"));
    }

    @Test
    void test_GenerateSku_WithSpecialCharactersInName_ShouldUseFirstTwoChars() {
        String sku = skuGeneratorService.generateSku("A-Product", "B.Type");

        assertTrue(sku.startsWith("A--B.-"));
    }
}
