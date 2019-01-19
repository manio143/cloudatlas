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
    private String agentHost;
    private String signerHost;
    private boolean isAgentSet;
    private boolean isSignerSet;

    public ClientStructures() {
    }

    public void setAgentHost(String agentHost) {
        this.agentHost = agentHost;
    }

    public String getAgentHost() {
        return agentHost;
    }

    public void setSignerHost(String host) {
        this.signerHost = host;
    }

    public String getSignerHost() {
        return signerHost;
    }

    public void setIsAgentSet(boolean isAgentSet) {
        this.isAgentSet = isAgentSet;
    }

    public boolean getIsAgentSet() {
        return isAgentSet;
    }

    public void setIsSignerSet(boolean isSignerSet) {
        this.isSignerSet = isSignerSet;
    }

    public boolean getIsSignerSet() {
        return isSignerSet;
    }
}
