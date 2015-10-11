package ch.fhnw.cpib.compiler.scanner.token;

import ch.fhnw.cpib.compiler.scanner.enums.Terminals;

public abstract class Keywords extends BaseToken {

    public Keywords(Terminals t) {
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

    public static class Sentinel extends Keywords {

        public Sentinel() {
            super(Terminals.SENTINEL);
        } 
    }
    
    public static class RParen extends Keywords {

        public RParen() {
            super(Terminals.RPAREN);
        } 
    }
    
    public static class LParen extends Keywords {

        public LParen() {
            super(Terminals.LPAREN);
        } 
    }
    
    public static class Comma extends Keywords {

        public Comma() {
            super(Terminals.COMMA);
        } 
    }
    
    public static class Colon extends Keywords {

        public Colon() {
            super(Terminals.COLON);
        } 
    }
}