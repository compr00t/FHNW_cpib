package ch.fhnw.cpib.compiler.scanner.exception;

public class LexicalError extends Exception {

    private static final long serialVersionUID = -7199114117172422355L;

    private final int lineNumber;
    private final int charNumber;
    private final char c;
    private final String message;
    
    public LexicalError(String message, char c, int lineNumber, int charNumber) {
        super();
        
        this.lineNumber = lineNumber;
        this.message = message;
        this.charNumber = charNumber;
        this.c = c;
    }
    
    public String getMessage() {
        return "["+ lineNumber + ":" + charNumber + " - LexicalError] " + message + " " + c;
    }
}
