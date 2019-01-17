package pl.edu.mimuw.cloudatlas.signer.signerExceptions;

public class UninstallException extends SignerException {
    public UninstallException(String queryName) {
        super("No such query to uninstall: " + queryName);
    }
}
