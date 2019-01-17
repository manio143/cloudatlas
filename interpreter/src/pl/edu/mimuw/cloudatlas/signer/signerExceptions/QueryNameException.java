package pl.edu.mimuw.cloudatlas.signer.signerExceptions;

public class QueryNameException extends SignerException {
    public QueryNameException() {
        super("Query with that name is already installed!");
    }
}
