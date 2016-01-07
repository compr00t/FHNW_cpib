package ch.fhnw.cpib.compiler.exception;

public class ContextError extends Exception {

    private static final long serialVersionUID = 1L;

    private final String message;

    public ContextError(String message) {
        super();
    	//System.out.println("ERROR: " + message);
    	//System.exit(1);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message + ".";
    }
}