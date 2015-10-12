package ch.fhnw.cpib.compiler.scanner.token;

import ch.fhnw.cpib.compiler.scanner.enums.Terminals;
import ch.fhnw.cpib.compiler.scanner.enums.OperatorAttribute;

public class RelOpr extends BaseToken {

private final OperatorAttribute value;
    
    public RelOpr(OperatorAttribute v) {
        super(Terminals.RELOPR);
        value = v;
    }

    OperatorAttribute getValue() {
        return value;
    }
    
    public String toString() {
        return "(" + super.toString() + "," + value + ")";
    }
    
}
