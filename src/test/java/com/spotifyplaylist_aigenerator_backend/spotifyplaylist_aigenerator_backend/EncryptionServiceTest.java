package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.services.EncryptionService;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.*;

public class EncryptionServiceTest {

    private EncryptionService encryptionService;
    private SecretKey secretKey;

    @BeforeEach
    public void setUp() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        secretKey = keyGen.generateKey();

        encryptionService = new EncryptionService(secretKey);
    }

    @Test
    public void testEncryptAndDecrypt() throws Exception {
        String originalData = "Test data for encryption";

        String encryptedData = encryptionService.encrypt(originalData);

        String decryptedData = encryptionService.decrypt(encryptedData);

        assertEquals(originalData, decryptedData);
    }

    @Test
    public void testDecrypt_InvalidData() {
        String invalidEncryptedData = "InvalidBase64String";

        assertThrows(Exception.class, () -> {
            encryptionService.decrypt(invalidEncryptedData);
        });
    }
}