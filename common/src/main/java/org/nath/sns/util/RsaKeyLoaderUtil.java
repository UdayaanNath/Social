package org.nath.sns.util;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public final class RsaKeyLoaderUtil {

    private static final JcaPEMKeyConverter CONVERTER = new JcaPEMKeyConverter();

    private RsaKeyLoaderUtil() {}

    public static RSAPrivateKey loadPrivateKey(File pemFile) throws IOException {
        try (FileReader reader = new FileReader(pemFile);
             PEMParser parser = new PEMParser(reader)) {
            Object parsedObject = parser.readObject();
            PrivateKey key;
            if (parsedObject instanceof PrivateKeyInfo) {
                key = CONVERTER.getPrivateKey((PrivateKeyInfo) parsedObject);
            } else if (parsedObject instanceof PEMKeyPair) {
                KeyPair pair = CONVERTER.getKeyPair((PEMKeyPair) parsedObject);
                key = pair.getPrivate();
            } else {
                throw new IllegalArgumentException("Unsupported private key structure");
            }
            return (RSAPrivateKey) key;
        }
    }

    public static RSAPublicKey loadPublicKey(File pemFile) throws IOException {
        try (FileReader reader = new FileReader(pemFile);
             PEMParser parser = new PEMParser(reader)) {
            Object parsedObject = parser.readObject();
            PublicKey key;
            if (parsedObject instanceof SubjectPublicKeyInfo) {
                key = CONVERTER.getPublicKey((SubjectPublicKeyInfo) parsedObject);
            } else if (parsedObject instanceof PEMKeyPair) {
                KeyPair pair = CONVERTER.getKeyPair((PEMKeyPair) parsedObject);
                key = pair.getPublic();
            } else {
                throw new IllegalArgumentException("Unsupported public key structure");
            }
            return (RSAPublicKey) key;
        }
    }
}