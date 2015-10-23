package com.society.leagues.conf.spring.social.mongo;

import com.society.leagues.mongo.UserSocialConnection;
import com.society.leagues.mongo.UserSocialConnectionRepository;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.*;
import org.springframework.social.security.SocialAuthenticationServiceLocator;

import java.util.*;

public class MongoUsersConnectionRepository implements UsersConnectionRepository {

    private final UserSocialConnectionRepository userSocialConnectionRepository;

    private final SocialAuthenticationServiceLocator socialAuthenticationServiceLocator;

    private final TextEncryptor textEncryptor;

    private ConnectionSignUp connectionSignUp;

    public MongoUsersConnectionRepository(UserSocialConnectionRepository userSocialConnectionRepository,
                                          SocialAuthenticationServiceLocator socialAuthenticationServiceLocator
            , TextEncryptor textEncryptor){
        this.userSocialConnectionRepository = userSocialConnectionRepository;
        this.socialAuthenticationServiceLocator = socialAuthenticationServiceLocator;
        this.textEncryptor = textEncryptor;
    }

    /**
     * The command to execute to create a new local user profile in the event no user id could be mapped to a connection.
     * Allows for implicitly creating a user profile from connection data during a provider sign-in attempt.
     * Defaults to null, indicating explicit sign-up will be required to complete the provider sign-in attempt.
     * @see #findUserIdsWithConnection(Connection)
     */
    public void setConnectionSignUp(ConnectionSignUp connectionSignUp) {
        this.connectionSignUp = connectionSignUp;
    }

    public List<String> findUserIdsWithConnection(Connection<?> connection) {
        ConnectionKey key = connection.getKey();
        List<UserSocialConnection> userSocialConnectionList =
                this.userSocialConnectionRepository.findByProviderIdAndProviderUserId(key.getProviderId(), key.getProviderUserId());
        List<String> localUserIds = new ArrayList<String>();
        for (UserSocialConnection userSocialConnection : userSocialConnectionList){
            localUserIds.add(userSocialConnection.getUserId());
        }

        if (localUserIds.size() == 0 && connectionSignUp != null) {
            String newUserId = connectionSignUp.execute(connection);
            if (newUserId != null)
            {
                createConnectionRepository(newUserId).addConnection(connection);
                return Arrays.asList(newUserId);
            }
        }
        return localUserIds;
    }

    public Set<String> findUserIdsConnectedTo(String providerId, Set<String> providerUserIds) {
        final Set<String> localUserIds = new HashSet<String>();

        List<UserSocialConnection> userSocialConnectionList =
                this.userSocialConnectionRepository.findByProviderIdAndProviderUserIdIn(providerId, providerUserIds);
        for (UserSocialConnection userSocialConnection : userSocialConnectionList){
            localUserIds.add(userSocialConnection.getUserId());
        }
        return localUserIds;
    }

    public ConnectionRepository createConnectionRepository(String userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }
        return new MongoConnectionRepository(userId, userSocialConnectionRepository, socialAuthenticationServiceLocator, textEncryptor);
    }
}
