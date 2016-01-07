package ch.fhnw.cpib.compiler.exception;

public class GrammarError extends Exception{
    
    public GrammarError(String message, int lineNumber){
    	System.out.println("["+ lineNumber + " - ContextError] " + message);
    	System.exit(1);
    }
}