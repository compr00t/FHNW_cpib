package ch.fhnw.cpib.compiler.scanner.token;

import ch.fhnw.cpib.compiler.scanner.enums.ModeAttributes;
import ch.fhnw.cpib.compiler.scanner.enums.Terminals;

public class Mode extends BaseToken{

    private final ModeAttributes value;

    Mode(Terminals term, ModeAttributes value) {
        super(term);
        this.value = value;
    }

    ModeAttributes getValue() {
        return value;
    }

    public String toString() {
        return "(" + super.toString() + "," + value.toString() + ")";
    }

    public static class ChangeMode extends Mode {

        public ChangeMode(ModeAttributes value) {
            super(Terminals.CHANGEMODE, value);
            assert (ModeAttributes.CONST == value || ModeAttributes.VAR == value);
        }
    }

    public static class MechMode extends Mode {

        public MechMode(ModeAttributes value) {
            super(Terminals.MECHMODE, value);
            assert (ModeAttributes.REF == value || ModeAttributes.COPY == value);
        }
    }    
}
