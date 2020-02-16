package com.javiersvg.tourofheroes;

import org.springframework.data.annotation.Id;
import org.springframework.security.oauth2.core.ClaimAccessor;

public class User {
    private ClaimAccessor claimAccessor;

    public User(ClaimAccessor claimAccessor) {
        this.claimAccessor = claimAccessor;
    }

    @Id
    public String getId() {
        return this.claimAccessor.getClaimAsString("email");
    }

    public String getName() {
        return this.claimAccessor.getClaimAsString("name");
    }

    public String getImageUrl() {
        return this.claimAccessor.getClaimAsString("picture");
    }
}
