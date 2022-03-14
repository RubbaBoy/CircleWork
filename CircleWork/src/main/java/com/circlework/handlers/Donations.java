package com.circlework.handlers;

import com.circlework.manager.AuthService;
import com.circlework.manager.CircleService;
import com.circlework.Objects;
import com.circlework.Row;
import com.circlework.SQLUtility;
import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class Donations extends BasicHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Donations.class);

    @Override
    public void registerPaths() {
        registerPath(new String[] {""}, "GET", Objects.Empty.class, this::get);
    }

    void get(HttpExchange exchange, String[] path, String method, Objects.Empty request, String token) throws Exception {
        if(!authService.validateToken(token)){
            setBody(exchange, Map.of("message", "invalid authtoken"), 418);
            return;
        }

        int raisedTotal = 0;
        int monthlyTotal = 0;

            var goalRows = SQLUtility.executeQuery("SELECT rasied_total, raised_monthly from charities");
            for (Row curRow: goalRows){
                raisedTotal += curRow.<Integer>get(0);
                monthlyTotal += curRow.<Integer>get(1);
            }

            LOGGER.info("donation listing {}", new DonationResponse(raisedTotal, monthlyTotal));

            setBody(exchange, new DonationResponse(raisedTotal, monthlyTotal), 200);
    }

    static final class DonationResponse {
        private final int total;
        private final int month;

        DonationResponse(int total, int month) {
            this.total = total;
            this.month = month;
        }

        public int total() {return total;}

        public int month() {return month;}

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (DonationResponse) obj;
            return this.total == that.total &&
                    this.month == that.month;
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(total, month);
        }

        @Override
        public String toString() {
            return "DonationResponse[" +
                    "total=" + total + ", " +
                    "month=" + month + ']';
        }
    }



}
