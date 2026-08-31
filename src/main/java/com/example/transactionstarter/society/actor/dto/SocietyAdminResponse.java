package com.example.transactionstarter.society.actor.dto;

import com.example.transactionstarter.society.actor.domain.SocietyAdmin;

/** Data returned to the client after a SocietyAdmin is created. */
public class SocietyAdminResponse {

    private final String id;
    private final String name;
    private final String societyId;
    private final String role;

    public SocietyAdminResponse(String id, String name, String societyId, String role) {
        this.id = id;
        this.name = name;
        this.societyId = societyId;
        this.role = role;
    }

    public static SocietyAdminResponse from(SocietyAdmin admin) {
        return new SocietyAdminResponse(admin.getId(), admin.getName(), admin.getSocietyId(), admin.getRole());
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSocietyId() {
        return societyId;
    }

    public String getRole() {
        return role;
    }
}
