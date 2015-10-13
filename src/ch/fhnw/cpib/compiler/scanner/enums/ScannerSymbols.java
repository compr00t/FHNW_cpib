package ch.fhnw.cpib.compiler.scanner.enums;

public enum ScannerSymbols {

    LPAREN('('), RPAREN(')'), LBRACE('{'), RBRACE('}'), 
    COMMA(','), COLON(':'), SEMICOLON(';'), EQUALS('='), 
    ASTERISK('*'), PLUS('+'), MINUS('-'), MOD('%'),
    SLASH('/'), LT('<'), GT('>'), DOT('.');
    
    private int charValue;
    
    ScannerSymbols(int charValue){
        this.charValue = charValue;
    }
    
    public char getCharValue(){
        return (char)charValue;
    }
    
    public static boolean contains(int value){
        for(ScannerSymbols s : values()){
            if(value == s.charValue){
                return true;
            }
        }
        return false;
    }
}
