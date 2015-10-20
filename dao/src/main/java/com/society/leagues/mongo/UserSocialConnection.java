package com.society.leagues.mongo;

public class UserSocialConnection  {
     String userId;
     String providerId;
     String providerUserId;
     String displayName;
     String profileUrl;
     String imageUrl;
     String accessToken;
     String secret;
     String refreshToken;
     Long expireTime;

    public String getUserId() {
        return userId;
    }

    public String getProviderId() {
        return providerId;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getSecret() {
        return secret;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public Long getExpireTime() {
        return expireTime;
    }
}
