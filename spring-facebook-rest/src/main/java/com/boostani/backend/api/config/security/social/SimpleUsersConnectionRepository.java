package com.boostani.backend.api.config.security.social;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.AuthenticationException;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.mem.TemporaryConnectionRepository;

import com.boostani.backend.api.persistance.model.FacebookUser;
import com.boostani.backend.api.persistance.model.User;
import com.boostani.backend.api.service.social.SocialUserService;

/**
 * A Simplified version of the JdbcUsersConnectionRepository that does not persist multiple connections to/from users.
 * This repository works with a one-to-one relation between between User and Connection
 * Note that it uses the JPA based UserService so no custom SQL is used
 */
public class SimpleUsersConnectionRepository implements UsersConnectionRepository {

    private SocialUserService userService;

    private ConnectionFactoryLocator connectionFactoryLocator;
    private ConnectionSignUp connectionSignUp;

    public SimpleUsersConnectionRepository(SocialUserService userService, ConnectionFactoryLocator connectionFactoryLocator) {
        this.userService = userService;
        this.connectionFactoryLocator = connectionFactoryLocator;
    }

    @Override
    public List<String> findUserIdsWithConnection(Connection<?> connection) {
        try {
            FacebookUser user = userService.loadUserByConnectionKey(connection.getKey());
            user.setAccessToken(connection.createData().getAccessToken());
            userService.updateUserDetails(user);
            return Arrays.asList(user.getUserId());
        } catch (AuthenticationException ae) {
            return Arrays.asList(connectionSignUp.execute(connection));
        }
    }

    @Override
    public Set<String> findUserIdsConnectedTo(String providerId, Set<String> providerUserIds) {
        Set<String> keys = new HashSet<>();
        for (String userId: providerUserIds) {
            ConnectionKey ck = new ConnectionKey(providerId, userId);
            try {
                keys.add(userService.loadUserByConnectionKey(ck).getUserId());
            } catch (AuthenticationException ae) {
                //ignore
            }
        }
        return keys;
    }

    @Override
    public ConnectionRepository createConnectionRepository(String userId) {
        final ConnectionRepository connectionRepository = new TemporaryConnectionRepository(connectionFactoryLocator);
        final User user = (User) userService.loadUserByUserId(userId);
        final ConnectionData connectionData = new ConnectionData(
                user.getProviderId(),
                user.getProviderUserId(),
                null, null, null,
                user.getAccessToken(),
                null, null, null);

        final Connection connection = connectionFactoryLocator
                .getConnectionFactory(user.getProviderId()).createConnection(connectionData);
        connectionRepository.addConnection(connection);
        return connectionRepository;
    }

    public void setConnectionSignUp(ConnectionSignUp connectionSignUp) {
        this.connectionSignUp = connectionSignUp;
    }
}
