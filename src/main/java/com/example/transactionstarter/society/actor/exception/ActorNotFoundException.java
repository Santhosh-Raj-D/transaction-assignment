package com.example.transactionstarter.society.actor.exception;

/** Thrown when a Resident/Merchant/etc. cannot be found by ID. */
public class ActorNotFoundException extends RuntimeException {

    public ActorNotFoundException(String actorType, String id) {
        super(actorType + " not found: " + id);
    }
}
