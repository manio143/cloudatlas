package pl.edu.mimuw.cloudatlas.cloudAtlasAPI;

import pl.edu.mimuw.cloudatlas.signer.SignedQueryRequest;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface SignerAPI extends Remote {
    SignedQueryRequest installQueries(String query) throws RemoteException;

    SignedQueryRequest uninstallQueries(String query) throws RemoteException;

    Collection<SignedQueryRequest> getInstalledQueries() throws RemoteException;
}