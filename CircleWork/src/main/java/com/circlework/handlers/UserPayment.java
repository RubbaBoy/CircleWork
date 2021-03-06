package com.circlework.handlers;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;
import com.circlework.Objects;
import com.sun.net.httpserver.HttpExchange;

import java.math.BigDecimal;
import java.util.Map;

public class UserPayment extends BasicHandler {

    private final BigDecimal MONTHLY_CHARGE = new BigDecimal(500);

    @Override
    public void registerPaths() {
        registerPath(new String[]{"client_token"}, "GET", Objects.Empty.class, this::clientToken);
        registerPath(new String[]{"pay"}, "POST", UserPaymentRequest.class, this::userPayment);
    }

    void clientToken(HttpExchange exchange, String[] path, String method, Objects.Empty $, String token) throws Exception {
        setBody(exchange, Map.of("token", ""));
    }

    void userPayment(HttpExchange exchange, String[] path, String method, UserPaymentRequest body, String token) throws Exception {
        if (!authService.validateToken(token)) {
            setBody(exchange, Map.of("message", "invalid authtoken"), 418);
            return;
        }

        var userSession = authService.getUserFromToken(token).orElseThrow();

        BraintreeGateway gateway = new BraintreeGateway(
                Environment.SANDBOX,
                "f6rwhjv3cwnydzx8",
                "2732nvxdjdyyy2m3",
                "b94b8326fd986178b101d56a51ea98dc"
        );

        TransactionRequest request = new TransactionRequest()
                .amount(MONTHLY_CHARGE)//$5 per month
                .paymentMethodNonce(body.nonce)
                .options()//TODO possibly set options for payments
                .done();

        Result<Transaction> result = gateway.transaction().sale(request);

        if (result.isSuccess()) {
            Transaction transaction = result.getTarget();
            setBody(exchange, new UserPaymentResponse(MONTHLY_CHARGE), 200);
        } else if (result.getTransaction() != null) {
            Transaction transaction = result.getTransaction();

            System.out.println("error processing transaction");

            setBody(exchange, Map.of("message", "error processing payment"), 402);//error processing payment
        } else {
            setBody(exchange, Map.of("message", "error processing payment"), 402);
        }

    }

    record UserPaymentRequest(String nonce) {}

    record UserPaymentResponse(BigDecimal amount) {}
}
