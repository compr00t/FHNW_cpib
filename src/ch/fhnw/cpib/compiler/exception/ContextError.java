package ch.fhnw.cpib.compiler.exception;

public class ContextError extends Exception {

    private static final long serialVersionUID = 1L;

    public ContextError(String message, int lineNumber) {
    	System.out.println("["+ lineNumber + " - ContextError] " + message);
    	System.exit(1);
    }
}