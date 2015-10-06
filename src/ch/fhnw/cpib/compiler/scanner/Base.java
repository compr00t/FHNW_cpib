package ch.fhnw.cpib.compiler.scanner;

class Base {
    private final Terminals terminal;

    Base(Terminals t) {
        terminal = t;
    }

    Terminals getTerminal() {
        return terminal;
    }

    public String toString() {
        return terminal.toString();
    }
}