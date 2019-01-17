package pl.edu.mimuw.cloudatlas.signer.signerExceptions;

public class RestrictedColumn extends SignerException {
    public RestrictedColumn(String column) {
        super("Restricted column name: " + column);
    }
}
