package ch.fhnw.cpib.compiler.exception;

public class GrammarError extends Exception{

    private static final long serialVersionUID = 1L;
    private final String message;
    
    public GrammarError(String message){
        super();
        this.message = message;
    }
    @Override
    public String getMessage(){
        return message+ ".";
    }

}