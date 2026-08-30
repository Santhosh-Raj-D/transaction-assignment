package com.example.transactionstarter.society.actor.dto;

import com.example.transactionstarter.society.actor.domain.Merchant;

/** Data returned to the client after a Merchant is created/fetched. */
public class MerchantResponse {

    private final String id;
    private final String businessName;
    private final String category;
    private final String settlementAccount;

    public MerchantResponse(String id, String businessName, String category, String settlementAccount) {
        this.id = id;
        this.businessName = businessName;
        this.category = category;
        this.settlementAccount = settlementAccount;
    }

    public static MerchantResponse from(Merchant merchant) {
        return new MerchantResponse(merchant.getId(), merchant.getBusinessName(), merchant.getCategory(), merchant.getSettlementAccount());
    }

    public String getId() {
        return id;
    }

    public String getBusinessName() {
        return businessName;
    }

    public String getCategory() {
        return category;
    }

    public String getSettlementAccount() {
        return settlementAccount;
    }
}
