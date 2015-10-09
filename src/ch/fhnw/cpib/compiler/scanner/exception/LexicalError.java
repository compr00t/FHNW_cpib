package ch.fhnw.cpib.compiler.scanner.exception;

public class LexicalError extends Exception {
    
    private final int lineNumber;
    private final String message;
    
    public LexicalError(String message, int lineNumber) {
        super();
        
        this.lineNumber = lineNumber;
        this.message = message;
    }
    
    public String getMessage() {
        return message + "at line " + lineNumber + ".";
    }
}
