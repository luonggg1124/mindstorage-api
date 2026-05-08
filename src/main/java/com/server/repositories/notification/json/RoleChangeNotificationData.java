package com.server.repositories.notification.json;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import com.server.models.enums.InvitationType;
import com.server.models.enums.RoleAction;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Cấu trúc {@code data} (jsonb) cho thông báo đổi vai trò. UI đọc từ {@code data}, không dùng title/content.
 */
@Data
@NoArgsConstructor
public class RoleChangeNotificationData {
    private InvitationType entityType;
    private UUID entityId;
    private String entityName;
    private Long targetUserId;
    private String targetUserName;
    private String oldRole;
    private String newRole;
    private Long changedByUserId;
    private String changedByName;

    public static RoleChangeNotificationData fromMap(Map<String, Object> m) {
        RoleChangeNotificationData d = new RoleChangeNotificationData();
        if (m == null || m.isEmpty()) {
            return d;
        }
        Object et = m.get("entityType");
        if (et != null) {
            try {
                d.setEntityType(InvitationType.valueOf(et.toString()));
            } catch (IllegalArgumentException ignored) {
            }
        }
        Object eid = m.get("entityId");
        if (eid != null) {
            try {
                d.setEntityId(UUID.fromString(eid.toString()));
            } catch (IllegalArgumentException ignored) {
            }
        }
        Object name = m.get("entityName");
        if (name != null) {
            d.setEntityName(name.toString());
        }
        d.setTargetUserId(toLong(m.get("targetUserId")));
        Object tun = m.get("targetUserName");
        if (tun != null) {
            d.setTargetUserName(tun.toString());
        }
        Object or = m.get("oldRole");
        if (or != null) {
            d.setOldRole(or.toString());
        }
        Object nr = m.get("newRole");
        if (nr != null) {
            d.setNewRole(nr.toString());
        }
        d.setChangedByUserId(toLong(m.get("changedByUserId")));
        Object cb = m.get("changedByName");
        if (cb != null) {
            d.setChangedByName(cb.toString());
        }
        return d;
    }

    public Map<String, Object> toMap() {
        return toMapStrings(
                entityType,
                entityId,
                entityName != null ? entityName : "",
                targetUserId,
                targetUserName != null ? targetUserName : "",
                oldRole != null ? oldRole : "",
                newRole != null ? newRole : "",
                changedByUserId,
                changedByName != null ? changedByName : "");
    }

    public static Map<String, Object> toMap(
            InvitationType entityType,
            UUID entityId,
            String entityName,
            Long targetUserId,
            String targetUserName,
            RoleAction oldRole,
            RoleAction newRole,
            Long changedByUserId,
            String changedByName) {
        return toMapStrings(
                entityType,
                entityId,
                entityName != null ? entityName : "",
                targetUserId,
                targetUserName != null ? targetUserName : "",
                oldRole != null ? oldRole.name() : "",
                newRole != null ? newRole.name() : "",
                changedByUserId,
                changedByName != null ? changedByName : "");
    }

    public static Map<String, Object> mergeDedup(
            Map<String, Object> existingData,
            InvitationType entityType,
            UUID entityId,
            String entityName,
            Long targetUserId,
            String targetUserName,
            RoleAction oldRoleFallback,
            RoleAction newRole,
            Long changedByUserId,
            String changedByName) {
        String preservedOld = "";
        if (existingData != null && existingData.get("oldRole") != null) {
            preservedOld = existingData.get("oldRole").toString();
        } else if (oldRoleFallback != null) {
            preservedOld = oldRoleFallback.name();
        }
        String newRoleStr = newRole != null ? newRole.name() : "";
        String en = entityName != null ? entityName : "";
        String tun = targetUserName != null ? targetUserName : "";
        String cb = changedByName != null ? changedByName : "";
        return toMapStrings(
                entityType,
                entityId,
                en,
                targetUserId,
                tun,
                preservedOld,
                newRoleStr,
                changedByUserId,
                cb);
    }

    private static Map<String, Object> toMapStrings(
            InvitationType entityType,
            UUID entityId,
            String entityName,
            Long targetUserId,
            String targetUserName,
            String oldRoleStr,
            String newRoleStr,
            Long changedByUserId,
            String changedByName) {
        Map<String, Object> m = new LinkedHashMap<>();
        if (entityType != null) {
            m.put("entityType", entityType.name());
        }
        if (entityId != null) {
            m.put("entityId", entityId.toString());
        }
        m.put("entityName", entityName != null ? entityName : "");
        if (targetUserId != null) {
            m.put("targetUserId", targetUserId);
        }
        m.put("targetUserName", targetUserName != null ? targetUserName : "");
        m.put("oldRole", oldRoleStr != null ? oldRoleStr : "");
        m.put("newRole", newRoleStr != null ? newRoleStr : "");
        if (changedByUserId != null) {
            m.put("changedByUserId", changedByUserId);
        }
        m.put("changedByName", changedByName != null ? changedByName : "");
        return m;
    }

    private static Long toLong(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof Number n) {
            return n.longValue();
        }
        try {
            return Long.parseLong(o.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
