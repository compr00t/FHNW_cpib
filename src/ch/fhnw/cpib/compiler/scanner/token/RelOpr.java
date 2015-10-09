package ch.fhnw.cpib.compiler.scanner.token;

import ch.fhnw.cpib.compiler.scanner.enums.Terminals;
import ch.fhnw.cpib.compiler.scanner.enums.Operator;

public class RelOpr extends BaseToken {

private final Operator value;
    
    RelOpr(Operator v) {
        super(Terminals.RELOPR);
        value = v;
    }

    Operator getValue() {
        return value;
    }
    
    public String toString() {
        return "(" + super.toString() + "," + value + ")";
    }
    
}
