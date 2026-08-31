package com.example.transactionstarter.society.actor.dto;

import com.example.transactionstarter.society.actor.domain.PropertyManager;

/** Data returned to the client after a PropertyManager is created. */
public class PropertyManagerResponse {

    private final String id;
    private final String name;
    private final String societyId;

    public PropertyManagerResponse(String id, String name, String societyId) {
        this.id = id;
        this.name = name;
        this.societyId = societyId;
    }

    public static PropertyManagerResponse from(PropertyManager manager) {
        return new PropertyManagerResponse(manager.getId(), manager.getName(), manager.getSocietyId());
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
