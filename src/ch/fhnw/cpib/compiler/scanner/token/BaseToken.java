package ch.fhnw.cpib.compiler.scanner.token;

import ch.fhnw.cpib.compiler.scanner.IToken;
import ch.fhnw.cpib.compiler.scanner.enums.Terminals;

public abstract class BaseToken implements IToken {
    private final Terminals terminal;

    BaseToken(Terminals t) {
        terminal = t;
    }

    public Terminals getTerminal() {
        return terminal;
    }

    public String toString() {
        return terminal.toString();
    }
}