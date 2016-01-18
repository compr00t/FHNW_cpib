package ch.fhnw.cpib.compiler.context;

import ch.fhnw.cpib.compiler.context.Routine.RoutineTypes;

public class Function extends Routine {
    public Function(final String ident) {
        super(ident, RoutineTypes.FUNCTION);
    }
}
