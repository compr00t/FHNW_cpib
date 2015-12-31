package ch.fhnw.cpib.compiler.scanner.token;

import ch.fhnw.cpib.compiler.scanner.enums.*;

public class Symbol extends BaseToken {

    Symbol(Terminals t) {
        super(t);
    }
    
    @Override
    public String toString() {
        return getTerminal().toString();
    }
    
    @Override
    public boolean equals(Object o) {
        if(o != null && o.getClass() == this.getClass()) {
            return super.equals((BaseToken) o);
        }
        return true;
    }
    
    public static class RParen extends Symbol {

        public RParen() {
            super(Terminals.RPAREN);
        } 
    }
    
    public static class LParen extends Symbol {

        public LParen() {
            super(Terminals.LPAREN);
        } 
    }
    
    public static class LSBracket extends Symbol {

        public LSBracket() {
            super(Terminals.LSBRACKET);
        } 
    }
    
    public static class RSBracket extends Symbol {

        public RSBracket() {
            super(Terminals.RSBRACKET);
        } 
    }
    
    public static class Comma extends Symbol {

        public Comma() {
            super(Terminals.COMMA);
        } 
    }
    
    public static class Colon extends Symbol {

        public Colon() {
            super(Terminals.COLON);
        } 
    }
    
    public static class Semicolon extends Symbol {

        public Semicolon() {
            super(Terminals.SEMICOLON);
        } 
    }
    
    public static class Becomes extends Symbol {

        public Becomes() {
            super(Terminals.BECOMES);
        } 
    }
    
}
