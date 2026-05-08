package com.server.constants;

import java.util.UUID;

public class R2Clouflare {
    /** Tiền tố khi có user: {@code user/{id}/{folder}}. */
    public static final String USER_FOLDER = "users";

    /** Loại file phía sau id, ví dụ {@code attachments}. */
    public static final String ATTACHMENTS_FOLDER = "attachments";

    public static final String ANONYMOUS_FOLDER = "anonymous";

    public static String getFileKeyString(String folder, String fileName) {
        return folder +
                "/" +
                UUID.randomUUID().toString() +
                "-" + fileName;
    }

    /**
     * Có user: {@code user/{userId}/{folder}} hoặc {@code user/{userId}} nếu không có folder.
     * Anonymous: {@code anonymous/{folder}} hoặc {@code anonymous}.
     */
    public static String getFolder(String folder, Long userId) {
        String tail = folder == null ? "" : folder.replaceAll("^/+|/+$", "");
        if (userId != null) {
            if (tail.isEmpty()) {
                return USER_FOLDER + "/" + userId;
            }
            return USER_FOLDER + "/" + userId + "/" + tail;
        }
        if (tail.isEmpty()) {
            return ANONYMOUS_FOLDER;
        }
        return ANONYMOUS_FOLDER + "/" + tail;
    }
}
