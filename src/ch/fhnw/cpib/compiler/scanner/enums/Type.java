package ch.fhnw.cpib.compiler.scanner.enums;

public enum Type {
    
    INT64("INT64"), BOOL("BOOL");
    
    Type(String toString) {
        this.toString = toString;
    }
    
    private String toString;
    
    @Override
    public String toString() {
        return toString;
    }
}
