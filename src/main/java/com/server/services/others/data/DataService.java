package com.server.services.others.data;

public interface DataService {
    float[] generateEmbedding(String text);

    String toVectorString(float[] embedding);

    /** Plain text for embedding: strips HTML tags and decodes common entities. */
    String plainTextFromHtml(String html);
}
