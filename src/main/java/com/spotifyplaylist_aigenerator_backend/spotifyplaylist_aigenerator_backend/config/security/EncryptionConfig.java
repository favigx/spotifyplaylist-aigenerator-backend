package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
public class EncryptionConfig {

    @Value("${encryptionsecretkey}")
    private String encryptionsecretkey;

    @Bean
    public SecretKey secretKey() {
        return new SecretKeySpec(encryptionsecretkey.getBytes(), "AES");
    }
}