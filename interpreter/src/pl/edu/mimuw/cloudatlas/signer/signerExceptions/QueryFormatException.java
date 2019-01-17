package pl.edu.mimuw.cloudatlas.signer.signerExceptions;

public class QueryFormatException extends SignerException {
    public QueryFormatException() {
        super("Query incorrectly formulated, splitting select into correct parts failed!");
    }
}
