package pl.edu.mimuw.cloudatlas.signer;

import pl.edu.mimuw.cloudatlas.cloudAtlasAPI.SignerAPI;

import java.rmi.RemoteException;
import java.security.PrivateKey;

public class SignerAgent implements SignerAPI {
    private final PrivateKey key;

    public SignerAgent(PrivateKey key) {
        this.key = key;
    }

    public SignedQueryRequest installQueries(String query) throws RemoteException {
        // TODO validate
        return SignedQueryRequest.create(query, key);
    }

    public SignedQueryRequest uninstallQueries(String query) throws RemoteException {
        // TODO validate
        return SignedQueryRequest.create(query, key);
    }
}