package ch.fhnw.cpib.compiler.exception;

public class GrammarError extends Exception{
    
    public GrammarError(String message, int lineNumber){
    	System.out.println("["+ lineNumber + " - GrammarError] " + message);
    	System.exit(1);
    }
}