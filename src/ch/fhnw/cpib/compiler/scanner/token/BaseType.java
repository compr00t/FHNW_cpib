package ch.fhnw.cpib.compiler.scanner.token;

import ch.fhnw.cpib.compiler.scanner.enums.Terminals;
import ch.fhnw.cpib.compiler.scanner.enums.TypeAttribute;

public abstract class BaseType extends BaseToken {

    private final TypeAttribute value;

    BaseType(Terminals term, TypeAttribute value) {
        super(term);
        this.value = value;
    }

    TypeAttribute getValue() {
        return value;
    }

    public String toString() {
        return "(" + super.toString() + "," + value.toString() + ")";
    }

    public static class Type extends BaseType {

        public Type(TypeAttribute value) {
            super(Terminals.TYPE, value);
            assert (TypeAttribute.INT64 == value || TypeAttribute.BOOL == value);
        }
    }    
}
