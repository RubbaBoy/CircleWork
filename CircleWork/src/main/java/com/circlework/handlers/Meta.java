package com.circlework.handlers;

import com.circlework.manager.AuthService;
import com.circlework.manager.CircleService;
import com.sun.net.httpserver.HttpExchange;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

public class Meta extends BasicHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Meta.class);

    private final ExecutorService executor = ForkJoinPool.commonPool();
    private final Map<String, MetaResponse> cache = new ConcurrentHashMap<>();

    @Override
    void registerPaths() {
        registerPath(new String[] {}, "GET", MetaRequest.class, this::getMeta);
    }

    void getMeta(HttpExchange exchange, String[] path, String method, MetaRequest request, String token) throws IOException {
        final var url = request.url();
        if (cache.containsKey(url)) {
            setBody(exchange, cache.get(url));
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {
                var client = HttpClient.newHttpClient();
                var httpRequest = HttpRequest
                        .newBuilder(new URI(url))
                        .GET()
                        .build();

                var response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                var body = response.body();

                var doc = Jsoup.parse(body);
                Elements ogTags = doc.select("meta[property^=og:]");
                if (ogTags.size() <= 0) {
                    return;
                }

                // Set OGTags you want
                String title = null;
                String desc = null;
                String image = null;

                for (Element tag : ogTags) {
                    String text = tag.attr("property");
                    switch (text) {
                        case "og:image" -> image = tag.attr("content");
                        case "og:description" -> desc = tag.attr("content");
                        case "og:title" -> title = tag.attr("content");
                    }
                }

                var metaResponse = new MetaResponse(image, desc, title);
                cache.put(request.url(), metaResponse);
                setBody(exchange, metaResponse);
            } catch (Exception e) {
                handleError(exchange, e);
            }
        }, executor);
    }

    record MetaRequest(String url) {}

    record MetaResponse(String image, String description, String title) {}
}
