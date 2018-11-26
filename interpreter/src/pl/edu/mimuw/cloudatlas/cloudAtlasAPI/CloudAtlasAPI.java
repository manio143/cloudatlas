package pl.edu.mimuw.cloudatlas.cloudAtlasAPI;

import pl.edu.mimuw.cloudatlas.model.AttributesMap;
import pl.edu.mimuw.cloudatlas.model.Value;
import pl.edu.mimuw.cloudatlas.model.ValueContact;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;


public interface CloudAtlasAPI extends Remote {
    Set<String> zones() throws RemoteException;

    AttributesMap getAttributes(String pathName) throws RemoteException;

    void installQueries(String queries) throws RemoteException;

    void uninstallQuery(String queryName) throws RemoteException;

    void setAttribute(String pathName, String attr, Value val) throws RemoteException;

    void setFallbackContacts(List<ValueContact> contacts) throws RemoteException;
}