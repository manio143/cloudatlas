package pl.edu.mimuw.cloudatlas.cloudAtlasAPI;

import pl.edu.mimuw.cloudatlas.model.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface CloudAtlasAPI extends Remote {
    List<String> getZones() throws RemoteException;

    AttributesMap getAttributes(String pathName) throws RemoteException;

    Map<String, List<Attribute>> getQueries() throws RemoteException;

    void installQueries(String queries) throws RemoteException;

    void uninstallQuery(String queryName) throws RemoteException;

    void setAttribute(String pathName, String attr, Value val) throws RemoteException;

    void setFallbackContacts(ValueSet contacts) throws RemoteException;
}