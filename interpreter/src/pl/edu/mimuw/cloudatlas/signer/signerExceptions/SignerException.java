package pl.edu.mimuw.cloudatlas.signer.signerExceptions;

public abstract class SignerException extends RuntimeException {
    public SignerException(String message) {
        super(message);
    }
}