package com.society.leagues.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;

public interface UserSocialConnectionRepository extends MongoRepository<UserSocialConnection, String> {
    List<UserSocialConnection> findByUserId(String userId);

    List<UserSocialConnection> findByUserIdAndProviderId(String userId, String providerId);

    List<UserSocialConnection> findByProviderIdAndProviderUserId(String providerId, String providerUserId);

    UserSocialConnection findByUserIdAndProviderIdAndProviderUserId(String userId, String providerId, String providerUserId);

    List<UserSocialConnection> findByProviderIdAndProviderUserIdIn(String providerId, Collection<String> providerUserIds);
}