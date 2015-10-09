package ch.fhnw.cpib.compiler.scanner.token;

import ch.fhnw.cpib.compiler.scanner.Terminals;

public class Literal extends BaseToken {

    private final int value;
    
    Literal(int v) {
        super(Terminals.LITERAL);
        value = v;
    }

    int getValue() {
        return value;
    }
    
    public String toString() {
        return "(" + super.toString() + "," + value + ")";
    }
}