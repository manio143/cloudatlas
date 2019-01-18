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

    public ClientStructures() {
    }
}
