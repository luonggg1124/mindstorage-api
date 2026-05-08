package com.server.repositories.notification.json;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import com.server.models.enums.InvitationStatus;
import com.server.models.enums.InvitationType;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class InvitationNotificationData {
    private UUID invitationId;
    private InvitationStatus invitationStatus;
    private UUID entityId;
    private InvitationType entityType;
    private String entityName;
    private Long senderId;
    private String senderName;

    /**
     * Khớp khóa JSON cho thông báo lời mời và {@code updateInvitationStatusData}.
     */
    public static Map<String, Object> toMap(
            UUID invitationId,
            InvitationStatus invitationStatus,
            UUID entityId,
            InvitationType entityType,
            String entityName,
            Long senderId,
            String senderName) {
        Map<String, Object> m = new LinkedHashMap<>();
        if (invitationId != null) {
            m.put("invitationId", invitationId.toString());
        }
        if (invitationStatus != null) {
            m.put("invitationStatus", invitationStatus.name());
        }
        if (entityId != null) {
            m.put("entityId", entityId.toString());
        }
        if (entityType != null) {
            m.put("entityType", entityType.name());
        }
        m.put("entityName", entityName != null ? entityName : "");
        if (senderId != null) {
            m.put("senderId", senderId);
        }
        m.put("senderName", senderName != null ? senderName : "");
        return m;
    }
}
