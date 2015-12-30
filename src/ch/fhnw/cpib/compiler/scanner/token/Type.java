package ch.fhnw.cpib.compiler.scanner.token;

import ch.fhnw.cpib.compiler.scanner.enums.Terminals;
import ch.fhnw.cpib.compiler.scanner.enums.TypeAttribute;

public class Type extends BaseToken {

    private final TypeAttribute value;

    public Type(TypeAttribute attribute) {
        super(Terminals.TYPE);
        this.value = attribute;
    }

    public TypeAttribute getValue() {
        return value;
    }

    public String toString() {
        return "(TYPE, " + value.toString() + ")";
    }

    public String toString(final String indent) {
        return indent + "<Type type=\"" + value.toString() + "\"/>\n";
    }
}
