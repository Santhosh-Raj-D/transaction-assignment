package com.example.transactionstarter.society.actor.dto;

import com.example.transactionstarter.society.actor.domain.Resident;

/** Data returned to the client after a Resident is created/fetched. */
public class ResidentResponse {

    private final String id;
    private final String name;
    private final String flatId;
    private final String contact;

    public ResidentResponse(String id, String name, String flatId, String contact) {
        this.id = id;
        this.name = name;
        this.flatId = flatId;
        this.contact = contact;
    }

    public static ResidentResponse from(Resident resident) {
        return new ResidentResponse(resident.getId(), resident.getName(), resident.getFlatId(), resident.getContact());
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFlatId() {
        return flatId;
    }

    public String getContact() {
        return contact;
    }
}
