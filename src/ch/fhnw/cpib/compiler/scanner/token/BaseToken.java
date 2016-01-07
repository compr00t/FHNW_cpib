package ch.fhnw.cpib.compiler.scanner.token;

import ch.fhnw.cpib.compiler.scanner.IToken;
import ch.fhnw.cpib.compiler.scanner.enums.*;

public abstract class BaseToken implements IToken {
    
    private final Terminals terminal;
    private int line;

    BaseToken(Terminals terminal) {
        this.terminal = terminal;
    }

    public Terminals getTerminal() {
        return this.terminal;
    }

    public String toString() {
        return terminal.toString();
    }
    
    public int getLine() {
    	return line;
    }
    
    public void setLine(int line) {
    	this.line = line;
    }
}