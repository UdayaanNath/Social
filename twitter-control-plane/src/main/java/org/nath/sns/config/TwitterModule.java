package org.nath.sns.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.apache.kafka.clients.producer.Producer;
import org.nath.sns.manager.KafkaTweetsProducerManager;
import org.nath.sns.resource.TweetsResource;
import org.nath.sns.resource.TwitterServiceHealthResource;
import org.nath.sns.service.JwtTokenService;
import org.nath.sns.util.JwtAuthenticatorUtil;
import org.nath.sns.util.RsaKeyLoaderUtil;

import java.io.File;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class TwitterModule extends AbstractModule
{
    private final TwitterServiceConfiguration twitterServiceConfiguration;

    public TwitterModule(TwitterServiceConfiguration twitterServiceConfiguration) {
        this.twitterServiceConfiguration = twitterServiceConfiguration;
    }

    @Override
    protected void configure() {
        // Bind your dependencies here
        bind(TwitterServiceConfiguration.class).toInstance(twitterServiceConfiguration);

        bind(TwitterServiceHealthResource.class).in(Singleton.class);
        bind(TweetsResource.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    public JwtTokenService getJwtTokenService() throws Exception{
        // 1. Load RSA Keys
        File privateKeyFile = new File(twitterServiceConfiguration.getJwt().getPrivateKeyPath());
        File publicKeyFile = new File(twitterServiceConfiguration.getJwt().getPublicKeyPath());

        RSAPrivateKey privateKey = RsaKeyLoaderUtil.loadPrivateKey(privateKeyFile);
        RSAPublicKey publicKey = RsaKeyLoaderUtil.loadPublicKey(publicKeyFile);

        // 2. Initialize Auth0 Token Service
        JwtTokenService tokenService = new JwtTokenService(
                privateKey,
                publicKey,
                twitterServiceConfiguration.getJwt().getExpirationMs()
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
    public KafkaTweetsProducerManager getKafkaProducerManager() {
        return new KafkaTweetsProducerManager(twitterServiceConfiguration.getKafka().getBootstrapServers());
    }

    @Provides
    @Singleton
    public Producer<Long, String> getKafkaProducer(KafkaTweetsProducerManager kafkaProducerManager) {
        return kafkaProducerManager.getProducer();
    }
}
