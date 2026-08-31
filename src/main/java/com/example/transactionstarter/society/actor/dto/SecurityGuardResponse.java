package com.example.transactionstarter.society.actor.dto;

import com.example.transactionstarter.society.actor.domain.SecurityGuard;

/** Data returned to the client after a SecurityGuard is created. */
public class SecurityGuardResponse {

    private final String id;
    private final String name;
    private final String societyId;

    public SecurityGuardResponse(String id, String name, String societyId) {
        this.id = id;
        this.name = name;
        this.societyId = societyId;
    }

    public static SecurityGuardResponse from(SecurityGuard guard) {
        return new SecurityGuardResponse(guard.getId(), guard.getName(), guard.getSocietyId());
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
}
