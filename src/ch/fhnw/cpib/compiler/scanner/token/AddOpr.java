package ch.fhnw.cpib.compiler.scanner.token;

import ch.fhnw.cpib.compiler.scanner.enums.Terminals;
import ch.fhnw.cpib.compiler.scanner.enums.Operator;

public class AddOpr extends BaseToken {

private final Operator value;
    
    AddOpr(Operator v) {
        super(Terminals.ADDOPR);
        value = v;
    }

    Operator getValue() {
        return value;
    }
    
    public String toString() {
        return "(" + super.toString() + "," + value + ")";
    }
}
