package pl.edu.mimuw.cloudatlas.signer.signerExceptions;

public class ColumnException extends SignerException {
    public ColumnException(String column) {
        super("A select with column " + column + " already installed!");
    }
}
