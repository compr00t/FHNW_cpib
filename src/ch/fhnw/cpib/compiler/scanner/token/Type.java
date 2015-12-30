package ch.fhnw.cpib.compiler.scanner.token;

import ch.fhnw.cpib.compiler.scanner.enums.Terminals;
import ch.fhnw.cpib.compiler.scanner.enums.TypeAttribute;

public class Type extends BaseToken {

    private final TypeAttribute attribute;

    public Type(TypeAttribute attribute) {
        super(Terminals.TYPE);
        this.attribute = attribute;
    }

    public TypeAttribute getAttribute() {
        return attribute;
    }

    public String toString() {
        return "(TYPE, " + attribute.toString() + ")";
    }

    public String toString(final String indent) {
        return indent + "<Type type=\"" + attribute.toString() + "\"/>\n";
    }
}
