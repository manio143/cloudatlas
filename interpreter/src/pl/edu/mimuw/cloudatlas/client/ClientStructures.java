package pl.edu.mimuw.cloudatlas.client;

import pl.edu.mimuw.cloudatlas.cloudAtlasAPI.CloudAtlasAPI;
import pl.edu.mimuw.cloudatlas.cloudAtlasAPI.SignerAPI;
import pl.edu.mimuw.cloudatlas.model.AttributesMap;

import java.util.Map;
import java.util.TreeMap;

public class ClientStructures {
    public CloudAtlasAPI cloudAtlas;
    public SignerAPI signer;
    public final TreeMap<Long,Map<String, AttributesMap>> results = new TreeMap<>();
    private String host;
    private String signerHost;

    public ClientStructures() {
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public void setSignerHost(String host) {
        this.signerHost = host;
    }

    public String getSignerHost() {
        return signerHost;
    }
}
