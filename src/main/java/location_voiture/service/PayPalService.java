package location_voiture.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.AmountWithBreakdown;
import com.paypal.orders.ApplicationContext;
import com.paypal.orders.LinkDescription;
import com.paypal.orders.Order;
import com.paypal.orders.OrderRequest;
import com.paypal.orders.OrdersCreateRequest;
import com.paypal.orders.OrdersGetRequest;
import com.paypal.orders.PurchaseUnitRequest;

@Service
public class PayPalService {

    private static final Logger logger = LoggerFactory.getLogger(PayPalService.class);

    private final PayPalHttpClient payPalHttpClient;

    @Value("${paypal.cancel.url:http://localhost:8082/Clientes/paypal/cancel}")
    private String cancelUrl;

    @Value("${paypal.return.url:http://localhost:8082/Clientes/paypal/success}")
    private String returnUrl;

    @Autowired
    public PayPalService(PayPalHttpClient payPalHttpClient) {
        this.payPalHttpClient = payPalHttpClient;
        logger.info("PayPalService initialisé avec succès.");
    }

    public String createOrder(String totalAmount, String currency, String description) throws IOException {
        try {
            double amount = Double.parseDouble(totalAmount);
            if (amount <= 0) {
                throw new IllegalArgumentException("Le montant doit être positif.");
            }

            OrderRequest orderRequest = new OrderRequest();
            orderRequest.checkoutPaymentIntent("CAPTURE");

            ApplicationContext applicationContext = new ApplicationContext()
                    .brandName("RentCar")
                    .landingPage("BILLING")
                    .cancelUrl(cancelUrl)
                    .returnUrl(returnUrl)
                    .userAction("PAY_NOW");

            PurchaseUnitRequest purchaseUnit = new PurchaseUnitRequest()
                    .description(description)
                    .amountWithBreakdown(new AmountWithBreakdown()
                            .currencyCode(currency)
                            .value(String.format("%.2f", amount)));

            List<PurchaseUnitRequest> purchaseUnits = new ArrayList<>();
            purchaseUnits.add(purchaseUnit);

            orderRequest.applicationContext(applicationContext);
            orderRequest.purchaseUnits(purchaseUnits);

            OrdersCreateRequest request = new OrdersCreateRequest().requestBody(orderRequest);
            HttpResponse<Order> response = payPalHttpClient.execute(request);
            Order order = response.result();

            for (LinkDescription link : order.links()) {
                if ("approve".equals(link.rel())) {
                    logger.info("Commande PayPal créée avec succès. Lien d'approbation: {}", link.href());
                    return link.href();
                }
            }
            throw new RuntimeException("Lien d'approbation non trouvé pour la commande.");
        } catch (NumberFormatException e) {
            logger.error("Montant invalide: {}", totalAmount, e);
            throw new IllegalArgumentException("Le montant doit être un nombre valide.", e);
        } catch (IOException e) {
            logger.error("Erreur lors de la création de la commande PayPal: {}", e.getMessage());
            throw e;
        }
    }

    public boolean validateTransaction(String transactionId) throws IOException {
        try {
            OrdersGetRequest request = new OrdersGetRequest(transactionId);
            HttpResponse<Order> response = payPalHttpClient.execute(request);
            Order order = response.result();
            boolean isCaptured = "COMPLETED".equals(order.status());
            logger.info("Validation de la transaction {}: {}", transactionId, isCaptured);
            return isCaptured;
        } catch (IOException e) {
            logger.error("Erreur lors de la validation de la transaction {}: {}", transactionId, e.getMessage());
            throw e;
        }
    }
}