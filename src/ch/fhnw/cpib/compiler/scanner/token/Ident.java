package ch.fhnw.cpib.compiler.scanner.token;

import ch.fhnw.cpib.compiler.scanner.Terminals;

public class Ident extends BaseToken {

private final String value;
    
    Ident(String i) {
        super(Terminals.IDENT);
        value = i;
    }

    String getValue() {
        return value;
    }
    
    public String toString() {
        return "(" + super.toString() + ", \"" + value + "\")";
    }
}