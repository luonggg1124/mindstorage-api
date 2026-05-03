package com.server.services.others.data;

import java.util.Map;

public interface DataService {
    float[] generateEmbedding(String text);

    String toVectorString(float[] embedding);

    /** Plain text for embedding: strips HTML tags and decodes common entities. */
    String plainTextFromHtml(String html);

    /** Chuyển giá trị JSON-serializable thành {@code Map} cho jsonb / API. */
    Map<String, Object> objectToMap(Object value);
}
