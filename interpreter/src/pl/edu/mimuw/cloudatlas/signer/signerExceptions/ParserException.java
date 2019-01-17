package pl.edu.mimuw.cloudatlas.signer.signerExceptions;

public class ParserException extends SignerException {
    public ParserException(String select) {
        super("Error while trying to parse: " + select);
    }
}
