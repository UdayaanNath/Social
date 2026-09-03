package org.nath.sns.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.hibernate.SessionFactory;
import org.nath.sns.dao.UserDAO;
import org.nath.sns.dao.UserRoleDAO;
import org.nath.sns.resource.AppHealthResource;
import org.nath.sns.resource.AuthResource;
import org.nath.sns.resource.UsersResource;
import org.nath.sns.resource.UserRolesResource;
import org.nath.sns.service.AuthenticationService;
import org.nath.sns.service.JwtTokenService;
import org.nath.sns.service.UserService;
import org.nath.sns.service.UserRoleService;
import org.nath.sns.util.JwtAuthenticatorUtil;
import org.nath.sns.util.RoleAuthorizerUtil;
import org.nath.sns.util.RsaKeyLoaderUtil;

import java.io.File;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class IdentityModule extends AbstractModule {

    private final SessionFactory sessionFactory;
    private final IdentityConfig identityConfig;

    public IdentityModule(IdentityConfig identityConfig, SessionFactory sessionFactory) {
        this.identityConfig = identityConfig;
        this.sessionFactory = sessionFactory;
    }

    @Override
    protected void configure() {
        // Bind other specific interfaces here if needed
        bind(IdentityConfig.class).toInstance(identityConfig);
        bind(SessionFactory.class).toInstance(sessionFactory);

        bind(AppHealthResource.class).in(Singleton.class);
        bind(UserService.class).in(Singleton.class);
        bind(UsersResource.class).in(Singleton.class);
        bind(UserRoleService.class).in(Singleton.class);
        bind(UserRolesResource.class).in(Singleton.class);
        bind(AuthenticationService.class).in(Singleton.class);
        bind(AuthResource.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    public UserDAO provideUserDAO() {
        return new UserDAO(sessionFactory);
    }

    @Provides
    @Singleton
    public UserRoleDAO provideUserRoleDAO() {
        return new UserRoleDAO(sessionFactory);
    }

    @Provides
    @Singleton
    public JwtTokenService getJwtTokenService() throws Exception{
        // 1. Load RSA Keys
        File privateKeyFile = new File(identityConfig.getJwtConfig().getPrivateKeyPath());
        File publicKeyFile = new File(identityConfig.getJwtConfig().getPublicKeyPath());

        RSAPrivateKey privateKey = RsaKeyLoaderUtil.loadPrivateKey(privateKeyFile);
        RSAPublicKey publicKey = RsaKeyLoaderUtil.loadPublicKey(publicKeyFile);

        // 2. Initialize Auth0 Token Service
        JwtTokenService tokenService = new JwtTokenService(
                privateKey,
                publicKey,
                identityConfig.getJwtConfig().getExpirationMs()
        );

        return tokenService;
    }

    @Provides
    @Singleton
    public JwtAuthenticatorUtil getJwtAuthenticator(JwtTokenService jwtTokenService) {
        return new JwtAuthenticatorUtil(jwtTokenService);
    }

    @Provides
    @Singleton
    public RoleAuthorizerUtil getRoleAuthorizerUtil() {
        return new RoleAuthorizerUtil();
    }
}
