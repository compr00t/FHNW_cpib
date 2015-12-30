package ch.fhnw.cpib.compiler.context;

import ch.fhnw.cpib.compiler.scanner.token.Mode;

public final class GlobImp {
    private final Mode changeMode;
    private final String ident;

    public Mode getChangeMode() {
        return changeMode;
    }

    public String getIdent() {
        return ident;
    }

    public GlobImp(final Mode changeMode, final String ident) {
        this.changeMode = changeMode;
        this.ident = ident;
    }
}
