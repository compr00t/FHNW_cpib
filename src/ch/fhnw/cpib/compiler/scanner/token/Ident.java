package ch.fhnw.cpib.compiler.scanner.token;

import ch.fhnw.cpib.compiler.scanner.enums.Terminals;

public class Ident extends BaseToken {

private final String value;
    
    public Ident(String i) {
        super(Terminals.IDENT);
        value = i;
    }

    public String getValue() {
        return value;
    }
    
    public String toString() {
        return "(" + super.toString() + ", \"" + value + "\")";
    }

    public String toString(final String indent) {
        return indent
                + "<Ident name=\""
                + value
                + "\"/>\n";
    }
}
