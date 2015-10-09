package ch.fhnw.cpib.compiler.scanner;

import ch.fhnw.cpib.compiler.scanner.enums.*;

public interface IToken {
    
    public Terminals getTerminal();
    public String toString();    
}
