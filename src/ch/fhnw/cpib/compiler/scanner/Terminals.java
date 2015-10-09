package ch.fhnw.cpib.compiler.scanner;

public enum Terminals {
    
    LPAREN("LPAREN"), RPAREN("RPAREN"), COMMA("COMMA"), SEMICOLON("SEMICOLON"), 
    COLON("COLON"), DEBUGIN("DEBUGIN"), DEBUGOUT("DEBUGOUT"), BECOMES("BECOMES"),
    MULTOPR("MULTOPR"), ADDOPR("ADDOPR"), RELOPR("RELOPR"), BOOLOPR("BOOLOPR"),
    LITERAL("LITERAL"), IDENT("IDENT"), PROGRAM("PROGRAM"), TYPE("TYPE"), 
    CALL("CALL"), NOTOPR("NOT"), IF("IF"), ELSE("ELSE"), ENDIF("ENDIF"),
    FLOWMODE("FLOWMODE"), CHANGEMODE("CHANGEMODE"), MECHMODE("MECHMODE"),
    FUN("FUN"), GLOBAL("GLOBAL"), LOCAL("LOCAL"), INIT("INIT"),
    PROC("PROC"), RETURNS("RETURNS"), SKIP("SKIP"), THEN("THEN"),
    WHILE("WHILE"), DO("DO"), ENDWHILE("ENDWHILE"), ENDPROGRAM("ENDPROGRAM"),
    ENDFUN("ENDFUN"),ENDPROC("ENDPROC"),SENTINEL("SENTINEL");
    
    Terminals(String toString) {
        this.toString = toString;
    }
    
    private String toString;
    
    @Override
    public String toString() {
        return toString;
    } 
}