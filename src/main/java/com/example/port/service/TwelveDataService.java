package com.example.port.service;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

@Service
public class TwelveDataService {

    private final AsyncHttpClient asyncHttpClient;

    @Value("${twelve.data.api.key}")
    private String apiKey;

    public TwelveDataService() {
        this.asyncHttpClient = new DefaultAsyncHttpClient();
    }

    public Double fetchLatestPrice(String symbol) {
        String url = "https://api.twelvedata.com/time_series";
        Request request = asyncHttpClient.prepareGet(url)
                .addQueryParam("apikey", apiKey)
                .addQueryParam("interval", "1day")
                .addQueryParam("symbol", symbol)
                .addQueryParam("outputsize", "1")
                .addQueryParam("format", "CSV")
                .build();

        try {
            Response response = asyncHttpClient.executeRequest(request).get();
            if (response.getStatusCode() == 200) {
                String csvData = response.getResponseBody();
                String[] lines = csvData.split("\\r?\\n");
                if (lines.length > 1) {
                    String[] fields = lines[1].split(";");
                    if (fields.length >= 5) {
                        return Double.parseDouble(fields[4]); // Close price is at index 4 in your CSV
                    }
                }
                throw new RuntimeException("No data found for symbol: " + symbol);
            } else {
                throw new RuntimeException("Failed to fetch latest price from Twelve Data API. Status code: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch latest price from Twelve Data API", e);
        }
    }

    public List<Map<String, String>> fetchHistoricalData(String symbol, String startDate, String endDate) {
        String url = "https://api.twelvedata.com/time_series";
        Request request = asyncHttpClient.prepareGet(url)
                .addQueryParam("apikey", apiKey)
                .addQueryParam("interval", "1day")
                .addQueryParam("symbol", symbol)
                .addQueryParam("start_date", startDate)
                .addQueryParam("end_date", endDate)
                .addQueryParam("format", "JSON")
                .build();

        try {
            Response response = asyncHttpClient.executeRequest(request).get();
            if (response.getStatusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(response.getResponseBody());
                List<Map<String, String>> values = mapper.convertValue(
                        jsonNode.get("values"),
                        new TypeReference<List<Map<String, String>>>() {}
                );
                return values;
            } else {
                throw new RuntimeException("Failed to fetch historical data from Twelve Data API. Status code: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch historical data from Twelve Data API", e);
        }
    }

    public void close() {
        try {
            asyncHttpClient.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to close AsyncHttpClient", e);
        }
    }
}