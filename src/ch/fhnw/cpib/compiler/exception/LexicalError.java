package ch.fhnw.cpib.compiler.exception;

public class LexicalError extends Exception {

    private static final long serialVersionUID = -7199114117172422355L;

    public LexicalError(String message, char c, int lineNumber, int charNumber) {
    	System.out.println("["+ lineNumber + ":" + charNumber + " - LexicalError] " + message + ": " + c);
    	System.exit(1);
    }
}
