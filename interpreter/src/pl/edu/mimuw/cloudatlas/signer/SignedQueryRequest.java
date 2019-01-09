package pl.edu.mimuw.cloudatlas.signer;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.Cipher;

public class SignedQueryRequest implements Serializable {
    private final String query;
    private final byte[] signature;

    private static final long serialVersionUID = 1;

    public SignedQueryRequest(String query, byte[] signature) {
        this.query = query;
        this.signature = signature;
    }

    public getQuery() {
        return query;
    }

    public boolean isValid(PublicKey pub) {
        try {
            MessageDigest digestGenerator = MessageDigest.getInstance("SHA-256");
            byte[] bytes = query.getBytes();
            byte[] digest = digestGenerator.digest(bytes);

            Cipher verifyCipher = Cipher.getInstance("RSA");
            verifyCipher.init(Cipher.DECRYPT_MODE, pub);
            byte[] foreignDigest = verifyCipher.doFinal(signature);

            if (digest.length == foreignDigest.length) {
                for (int i = 0; i < digest.length; i++)
                    if (digest[i] != foreignDigest[i])
                        return false;
                return true;
            } else
                return false;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public static create(String query, PrivateKey priv) {
        try {
            MessageDigest digestGenerator = MessageDigest.getInstance("SHA-256");
            byte[] bytes = query.getBytes();
            byte[] digest = digestGenerator.digest(bytes);

            Cipher signCipher = Cipher.getInstance("RSA");
            signCipher.init(Cipher.ENCRYPT_MODE, priv);
            byte[] signature = signCipher.doFinal(digest);

            return new SignedQueryRequest(query, signature);
        } catch (Exception e) {
            throw e;
        }
    }
}