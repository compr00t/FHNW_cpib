package ch.fhnw.cpib.compiler.scanner.token;

import ch.fhnw.cpib.compiler.scanner.enums.BoolVal;
import ch.fhnw.cpib.compiler.scanner.enums.Terminals;
import ch.fhnw.cpib.compiler.scanner.enums.TypeAttribute;

public class Literal extends BaseToken {

    private final int value;
    private BoolVal bool;

    public Literal(int value) {
        super(Terminals.LITERAL);
        this.value = value;
    }

    public Literal(BoolVal bool) {
        super(Terminals.LITERAL);
        this.value = bool.getIntVal();
        this.bool = bool;
    }

    public boolean getBoolVal(){
        return (value != 0);
    }

    public int getIntVal(){
        return value;
    }
    
    public int getLiteral() {
        return value;
    }
    
    public Type getType() {
        if (bool != null) {
            return new Type(TypeAttribute.BOOL);
        } else {
            return new Type(TypeAttribute.INT64);
        }
    }

    public String toString() {
        if (bool != null) {
            return "(LITERAL, " + bool.toString() + ")";
        } else {
            return "(LITERAL, IntVal " + value + ")";
        }
    }

    public String toString(final String indent) {
        return indent 
                + "<Literal mode=\"" 
                + getTerminal().toString() 
                + "\" value=\""
                + (bool != null ? bool.toString() : value) 
                + "\"/>\n";
    }
    
    public boolean isInteger(){
        return (bool == null);
    }
    
    public boolean isBoolean(){
        return (bool != null);
    }
}
