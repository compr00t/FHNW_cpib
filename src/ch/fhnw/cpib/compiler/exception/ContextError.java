package ch.fhnw.cpib.compiler.exception;

public class ContextError extends Exception {

    private static final long serialVersionUID = 1L;

    private final String message;

    public ContextError(String message) {
        super();
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message + ".";
    }
}
