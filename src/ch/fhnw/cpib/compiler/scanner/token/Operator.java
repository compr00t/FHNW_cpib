package ch.fhnw.cpib.compiler.scanner.token;

import ch.fhnw.cpib.compiler.scanner.enums.OperatorAttribute;
import ch.fhnw.cpib.compiler.scanner.enums.Terminals;

public class Operator extends BaseToken {

    private final OperatorAttribute value;

    Operator(Terminals term, OperatorAttribute value) {
        super(term);
        this.value = value;
    }

    public OperatorAttribute getValue() {
        return value;
    }

    public String toString() {
        return "(" + super.toString() + "," + value.toString() + ")";
    }
    
    public String toString(final String indent) {
        return indent
                + "<Operator name=\""
                + getTerminal().toString()
                + "\" attribute=\""
                + value.toString()
                + "\"/>\n";
    }

    public static class BoolOpr extends Operator {

        public BoolOpr(OperatorAttribute value) {
            super(Terminals.BOOLOPR, value);
            assert (OperatorAttribute.AND == value || OperatorAttribute.OR == value || OperatorAttribute.CAND == value || OperatorAttribute.COR == value);
        }
    }

    public static class AddOpr extends Operator {

        public AddOpr(OperatorAttribute value) {
            super(Terminals.ADDOPR, value);
            assert (OperatorAttribute.MINUS == value || OperatorAttribute.PLUS == value);
        }
    }

    public static class MultOpr extends Operator {

        public MultOpr(OperatorAttribute value) {
            super(Terminals.MULTOPR, value);
            assert (OperatorAttribute.TIMES == value || OperatorAttribute.DIV == value || OperatorAttribute.MOD == value);
        }
    }

    public static class RelOpr extends Operator {

        public RelOpr(OperatorAttribute value) {
            super(Terminals.RELOPR, value);
            assert (OperatorAttribute.EQ == value || OperatorAttribute.NE == value
                    || OperatorAttribute.LT == value || OperatorAttribute.GT == value
                    || OperatorAttribute.LE == value || OperatorAttribute.GE == value);
        }
    }

    public static class DotOpr extends Operator {

        public DotOpr() {
            super(Terminals.DOTOPR, OperatorAttribute.DOT);
        }
    }
}
