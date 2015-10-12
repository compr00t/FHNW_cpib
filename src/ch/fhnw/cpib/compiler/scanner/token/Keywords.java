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
    
    
}