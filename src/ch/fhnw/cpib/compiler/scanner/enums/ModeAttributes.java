package ch.fhnw.cpib.compiler.scanner.enums;

public enum ModeAttributes {
    
    COPY("COPY"), REF("REF"),
    CONST("CONST"), VAR("VAR");
    
    ModeAttributes(String toString){
        this.toString = toString;
    }
    
    private String toString;
    
    @Override
    public String toString(){
        return toString;
    }
}
