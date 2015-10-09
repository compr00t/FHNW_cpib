package ch.fhnw.cpib.compiler.scanner.enums;

public enum Operator {
    
    // Addition
    PLUS("PLUS"), MINUS("MINUS"),
    
    // Multiplication
    TIMES("TIMES"), MOD("MOD"), DIV("DIV"),
    
    // Relational
    EQ("EQ"), NE("NE"), LT("LT"), GT("GT"), LE("LE"), GE("GE"),
    
    // Boolean
    AND("AND"), OR("OR"), CAND("CAND"), COR("COR"),
    
    // Other
    DOT("DOT");
    
    private String toString;
    
    Operator(String toString) {
        this.toString = toString;
    }
        
    @Override
    public String toString() {
        return toString;
    }

}
