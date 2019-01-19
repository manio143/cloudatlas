package pl.edu.mimuw.cloudatlas.signer;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.LinkedList;
import java.util.List;
import javax.crypto.Cipher;

public class SignedQueryRequest implements Serializable {
    private final byte[] signature;

    public final Long queryID;
    public final String queryName;
    public final String select;
    public final List<String> columns;

    private static final long serialVersionUID = 1;

    public SignedQueryRequest(byte[] signature, long queryID, String queryName, String select, List<String> columns) {
        this.signature = signature;
        this.queryID = queryID;
        this.queryName = queryName;
        this.select = select;
        this.columns = columns;
    }

    private static byte[] getBytes(String select, String queryName, Long queryID) {
        List<Byte> bytes = new LinkedList<>();
        for (byte b : select.getBytes()) {
            bytes.add(b);
        }
        for (byte b : queryName.getBytes()) {
            bytes.add(b);
        }
        for (byte b : new BigInteger(queryID.toString()).toByteArray()) {
            bytes.add(b);
        }
        byte[] byteArray = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            byteArray[i] = bytes.get(i);
        }
        return byteArray;
    }

    public boolean isValid(PublicKey pub) {
        try {
            MessageDigest digestGenerator = MessageDigest.getInstance("SHA-256");
            byte[] digest = digestGenerator.digest(getBytes(select, queryName, queryID));

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

    public static SignedQueryRequest createNew(PrivateKey priv,
                                               long queryID,
                                               String queryName,
                                               String select,
                                               List<String> columns) {
        try {
            MessageDigest digestGenerator = MessageDigest.getInstance("SHA-256");
            byte[] digest = digestGenerator.digest(getBytes(select, queryName, queryID));

            Cipher signCipher = Cipher.getInstance("RSA");
            signCipher.init(Cipher.ENCRYPT_MODE, priv);
            byte[] signature = signCipher.doFinal(digest);

            return new SignedQueryRequest(signature, queryID, queryName, select, columns);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}