package com.server.services.others.data;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.HtmlUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataServiceImplement implements DataService {

    private static final Pattern HTML_SCRIPT = Pattern.compile("(?is)<script[^>]*>.*?</script>");
    private static final Pattern HTML_STYLE = Pattern.compile("(?is)<style[^>]*>.*?</style>");
    private static final Pattern HTML_TAG = Pattern.compile("<[^>]+>");
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private final ObjectMapper objectMapper;

    @Value("${embedding.api.url:http://127.0.0.1:9000/api/embedding}")
    private String embeddingUrl;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GenerateEmbeddingResponse {
        private List<Double> embedding;

        public List<Double> getEmbedding() {
            return embedding;
        }

        public void setEmbedding(List<Double> embedding) {
            this.embedding = embedding;
        }
    }

    @Override
    public float[] generateEmbedding(String text) {
        if (text == null || text.isBlank()) {
            log.warn("generateEmbedding skipped: text is null or blank");
            return null;
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.ALL));
        HttpEntity<Map<String, String>> request = new HttpEntity<>(Map.of("text", text), headers);

        ResponseEntity<String> httpResponse;
        try {
            httpResponse = restTemplate.exchange(
                    embeddingUrl,
                    HttpMethod.POST,
                    request,
                    String.class);
        } catch (Exception e) {
            log.info("Embedding HTTP call failed url={} err={}", embeddingUrl, e.toString());
            return null;
        }

        int status = httpResponse.getStatusCode().value();
        MediaType contentType = httpResponse.getHeaders().getContentType();
        String rawBody = httpResponse.getBody();

        if (!httpResponse.getStatusCode().is2xxSuccessful()) {
            log.info("Embedding API non-2xx status={} contentType={} bodySnippet={}",
                    status, contentType, snippet(rawBody, 400));
            return null;
        }
        if (rawBody == null || rawBody.isBlank()) {
            log.info("Embedding API 2xx but empty body status={} contentType={}", status, contentType);
            return null;
        }

        GenerateEmbeddingResponse parsed;
        try {
            parsed = objectMapper.readValue(rawBody, GenerateEmbeddingResponse.class);
        } catch (Exception e) {
            log.info("Embedding JSON parse failed: {} contentType={} bodySnippet={}",
                    e.toString(), contentType, snippet(rawBody, 400));
            return null;
        }

        if (parsed == null) {
            log.info("Embedding mapper returned null bodySnippet={}", snippet(rawBody, 400));
            return null;
        }

        List<Double> list = parsed.getEmbedding();
        if (list == null || list.isEmpty()) {
            log.info("Embedding field missing or empty after parse; bodySnippet={}", snippet(rawBody, 400));
            return null;
        }

        float[] vector = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            vector[i] = list.get(i).floatValue();
        }
        return vector;
    }

    @Override
    public String plainTextFromHtml(String html) {
        if (html == null || html.isBlank()) {
            return "";
        }
        String s = HTML_SCRIPT.matcher(html).replaceAll(" ");
        s = HTML_STYLE.matcher(s).replaceAll(" ");
        s = HTML_TAG.matcher(s).replaceAll(" ");
        s = HtmlUtils.htmlUnescape(s);
        s = s.replace('\u00a0', ' ').trim();
        s = WHITESPACE.matcher(s).replaceAll(" ").trim();
        return s;
    }

    private static String snippet(String s, int max) {
        if (s == null) {
            return "null";
        }
        String t = s.replace('\n', ' ').replace('\r', ' ').trim();
        return t.length() <= max ? t : t.substring(0, max) + "…";
    }

    public String toVectorString(List<Float> embedding) {
        return "[" + embedding.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")) + "]";
    }

    @Override
    public String toVectorString(float[] embedding) {
        if (embedding == null || embedding.length == 0) {
            return "[]";
        }
        return "[" + IntStream.range(0, embedding.length)
                .mapToObj(i -> Float.toString(embedding[i]))
                .collect(Collectors.joining(",")) + "]";
    }

}
