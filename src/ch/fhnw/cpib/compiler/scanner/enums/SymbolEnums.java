package ch.fhnw.cpib.compiler.scanner.enums;

public enum SymbolEnums {
    
    PLUS("+"), MINUS("-"), TIMES("*"), MOD("%"), DIV("/"),
    
    EQ("="), NE("/="), LT("<"), GT(">"), LE("<="), GE(">="),
    
    AND("&&"), OR("||"), CAND("&?"), COR("|?"),
    
    DOT("."),
    
    LPAREN("("), RPAREN(")"), COMMA(","), SEMICOLON(";"), COLON(":"), BECOMES(":=");
    
    
    private String pattern;
    
    SymbolEnums(String pattern) {
        pattern = this.pattern;
    }
}
