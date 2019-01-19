package pl.edu.mimuw.cloudatlas.cloudAtlasAPI;

import pl.edu.mimuw.cloudatlas.model.*;
import pl.edu.mimuw.cloudatlas.signer.SignedQueryRequest;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface CloudAtlasAPI extends Remote {
    List<String> getZones() throws RemoteException;

    AttributesMap getAttributes(String pathName) throws RemoteException;

    Map<String, List<String>> getQueries() throws RemoteException;

    void installQueries(SignedQueryRequest queries) throws RemoteException;

    void tryInstallQuery(SignedQueryRequest query) throws RemoteException;

    void uninstallQuery(SignedQueryRequest queryName) throws RemoteException;

    void setAttribute(String pathName, String attr, Value val) throws RemoteException;

    void setFallbackContacts(ValueSet contacts) throws RemoteException;
}