package ch.fhnw.cpib.compiler.scanner.token;

import ch.fhnw.cpib.compiler.scanner.enums.Terminals;
import ch.fhnw.cpib.compiler.scanner.enums.OperatorAttribute;

public class AddOpr extends BaseToken {

private final OperatorAttribute value;
    
    AddOpr(OperatorAttribute v) {
        super(Terminals.ADDOPR);
        value = v;
    }

    OperatorAttribute getValue() {
        return value;
    }
    
    public String toString() {
        return "(" + super.toString() + "," + value + ")";
    }
}
