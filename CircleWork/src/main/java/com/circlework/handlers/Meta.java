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
        LOGGER.debug("Bruh!");
        final var url = request.url();
        if (cache.containsKey(url)) {
            setBody(exchange, cache.get(url));
        }

        CompletableFuture.runAsync(() -> {
            try {
                LOGGER.debug("brtuh");
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
                e.printStackTrace();
                try {
                    setBody(exchange, Map.of("message", e.getMessage()), 500);
                } catch (IOException ex) {
                    throw new UncheckedIOException(ex);
                }
            }
        }, executor);
    }

    static final class MetaRequest {
        private final String url;

        MetaRequest(String url) {this.url = url;}

        public String url() {return url;}

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (MetaRequest) obj;
            return Objects.equals(this.url, that.url);
        }

        @Override
        public int hashCode() {
            return Objects.hash(url);
        }

        @Override
        public String toString() {
            return "MetaRequest[" +
                    "url=" + url + ']';
        }
    }

    static final class MetaResponse {
        private final String image;
        private final String description;
        private final String title;

        MetaResponse(String image, String description, String title) {
            this.image = image;
            this.description = description;
            this.title = title;
        }

        public String image() {return image;}

        public String description() {return description;}

        public String title() {return title;}

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (MetaResponse) obj;
            return Objects.equals(this.image, that.image) &&
                    Objects.equals(this.description, that.description) &&
                    Objects.equals(this.title, that.title);
        }

        @Override
        public int hashCode() {
            return Objects.hash(image, description, title);
        }

        @Override
        public String toString() {
            return "MetaResponse[" +
                    "image=" + image + ", " +
                    "description=" + description + ", " +
                    "title=" + title + ']';
        }
    }
}
