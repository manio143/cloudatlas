package pl.edu.mimuw.cloudatlas.signer.signerExceptions;

public class QueryNameException extends SignerException {
    public QueryNameException(String queryName) {
        super("Query with name " + queryName + " is already installed!");
    }
}
