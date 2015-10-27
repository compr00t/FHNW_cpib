package ch.fhnw.cpib.compiler.exception;

public class GrammarError extends Exception{
	
    private static final long serialVersionUID = 6423109607643663680L;
    
	private final int lineNumber;
    private final int charNumber;
    
    private final char c;
    
    private final String message;
	
	public GrammarError(String message, char c, int lineNumber, int charNumber) {
		super();
		
		this.lineNumber = lineNumber;
        this.message = message;
        this.charNumber = charNumber;
        this.c = c;
	}

	public String getMessage() {
        return "["+ lineNumber + ":" + charNumber + " - GrammaticalError] " + message + " " + c;
    }
}