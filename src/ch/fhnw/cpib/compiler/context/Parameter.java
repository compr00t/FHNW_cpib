package ch.fhnw.cpib.compiler.context;

import ch.fhnw.cpib.compiler.parser.AbsTree.TypedIdent;
import ch.fhnw.cpib.compiler.scanner.token.Mode;

public class Parameter {
    private final Mode mechMode;
    private final Mode changeMode;
    private final TypedIdent type;

    public Parameter(final Mode mechMode, final Mode changeMode, final TypedIdent type) {
        this.mechMode = mechMode;
        this.changeMode = changeMode;
        this.type = type;
    }

    public Mode getMechMode() {
        return mechMode;
    }

    public Mode getChangeMode() {
        return changeMode;
    }

    public TypedIdent getType() {
        return type;
    }
}
