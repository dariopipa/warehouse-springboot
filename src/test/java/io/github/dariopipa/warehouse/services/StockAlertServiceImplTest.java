package io.github.dariopipa.warehouse.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import io.github.dariopipa.warehouse.dtos.requests.SendEmailDTO;
import io.github.dariopipa.warehouse.entities.Product;
import io.github.dariopipa.warehouse.entities.StockAlert;
import io.github.dariopipa.warehouse.enums.RolesEnum;
import io.github.dariopipa.warehouse.repositories.StockAlertRepository;
import io.github.dariopipa.warehouse.services.interfaces.AuthService;
import io.github.dariopipa.warehouse.services.interfaces.EmailService;

@ExtendWith(MockitoExtension.class)
class StockAlertServiceImplTest {

    @Mock
    private StockAlertRepository stockAlertRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private StockAlertServiceImpl stockAlertService;

    private Product product;
    private List<String> managerEmails;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(stockAlertService, "DEFAULT_COMPANY_EMAIL", "warehouse@company.com");

        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setLowStockThreshold(10);

        managerEmails = Arrays.asList("manager1@company.com", "manager2@company.com");
    }

    @Test
    void test_AlertStockLow_WhenStockAboveThreshold_ShouldNotSendAlert() {
        stockAlertService.alertStockLow(product, 15);

        verify(authService, never()).findEmailsByRole(any());
        verify(emailService, never()).sendEmail(any());
        verify(stockAlertRepository, never()).save(any());
    }

    @Test
    void test_AlertStockLow_WhenStockAtThreshold_ShouldNotSendAlert() {
        stockAlertService.alertStockLow(product, 10);

        verify(authService, never()).findEmailsByRole(any());
        verify(emailService, never()).sendEmail(any());
        verify(stockAlertRepository, never()).save(any());
    }

    @Test
    void test_AlertStockLow_WhenStockBelowThreshold_ShouldSendEmailAndSaveAlert() {
        when(authService.findEmailsByRole(RolesEnum.ROLE_MANAGER)).thenReturn(managerEmails);

        stockAlertService.alertStockLow(product, 5);

        verify(authService, times(1)).findEmailsByRole(RolesEnum.ROLE_MANAGER);
        verify(emailService, times(1)).sendEmail(argThat(email -> email.getTo().equals(managerEmails) &&
                email.getFrom().equals("warehouse@company.com") &&
                email.getSubject().equals("Low Stock Alert: Test Product") &&
                email.getBody().contains("Current quantity: 5") &&
                email.getBody().contains("Threshold: 10")));
        verify(stockAlertRepository, times(1)).save(argThat(alert -> alert.getProduct().equals(product) &&
                alert.getEmailSent()));
    }

    @Test
    void test_AlertStockLow_WhenEmailServiceThrowsException_ShouldSaveAlertWithEmailSentFalse() {
        when(authService.findEmailsByRole(RolesEnum.ROLE_MANAGER)).thenReturn(managerEmails);
        doThrow(new RuntimeException("Email service error")).when(emailService).sendEmail(any(SendEmailDTO.class));

        stockAlertService.alertStockLow(product, 3);

        verify(authService, times(1)).findEmailsByRole(RolesEnum.ROLE_MANAGER);
        verify(emailService, times(1)).sendEmail(any(SendEmailDTO.class));
        verify(stockAlertRepository, times(1)).save(argThat(alert -> alert.getProduct().equals(product) &&
                !alert.getEmailSent()));
    }

    @Test
    void test_AlertStockLow_WhenAuthServiceThrowsException_ShouldSaveAlertWithEmailSentFalse() {
        when(authService.findEmailsByRole(RolesEnum.ROLE_MANAGER))
                .thenThrow(new RuntimeException("Auth service error"));

        stockAlertService.alertStockLow(product, 2);

        verify(authService, times(1)).findEmailsByRole(RolesEnum.ROLE_MANAGER);
        verify(emailService, never()).sendEmail(any());
        verify(stockAlertRepository, times(1)).save(argThat(alert -> alert.getProduct().equals(product) &&
                !alert.getEmailSent()));
    }

    @Test
    void test_AlertStockLow_ShouldCreateCorrectEmailBody() {
        when(authService.findEmailsByRole(RolesEnum.ROLE_MANAGER)).thenReturn(managerEmails);

        stockAlertService.alertStockLow(product, 7);

        verify(emailService, times(1)).sendEmail(argThat(email -> {
            String body = email.getBody();
            return body.contains("Warning: The stock for product 'Test Product' is low!") &&
                    body.contains("Current quantity: 7") &&
                    body.contains("Threshold: 10") &&
                    body.contains("Please restock as soon as possible.");
        }));
    }

    @Test
    void test_AlertStockLow_WithZeroQuantity_ShouldTriggerAlert() {
        when(authService.findEmailsByRole(RolesEnum.ROLE_MANAGER)).thenReturn(managerEmails);

        stockAlertService.alertStockLow(product, 0);

        verify(authService, times(1)).findEmailsByRole(RolesEnum.ROLE_MANAGER);
        verify(emailService, times(1)).sendEmail(any(SendEmailDTO.class));
        verify(stockAlertRepository, times(1)).save(any(StockAlert.class));
    }

    @Test
    void test_AlertStockLow_WithDifferentThreshold_ShouldRespectProductThreshold() {
        product.setLowStockThreshold(25);
        when(authService.findEmailsByRole(RolesEnum.ROLE_MANAGER)).thenReturn(managerEmails);

        stockAlertService.alertStockLow(product, 24);

        verify(emailService, times(1)).sendEmail(argThat(email -> email.getBody().contains("Current quantity: 24") &&
                email.getBody().contains("Threshold: 25")));
    }

    @Test
    void test_AlertStockLow_WithEmptyManagerEmails_ShouldStillSaveAlert() {
        when(authService.findEmailsByRole(RolesEnum.ROLE_MANAGER)).thenReturn(Arrays.asList());

        stockAlertService.alertStockLow(product, 5);

        verify(emailService, times(1)).sendEmail(argThat(email -> email.getTo().isEmpty()));
        verify(stockAlertRepository, times(1)).save(any(StockAlert.class));
    }
}
