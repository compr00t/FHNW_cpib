package ch.fhnw.cpib.compiler.scanner.token;

import ch.fhnw.cpib.compiler.scanner.enums.Terminals;

public abstract class Keywords extends BaseToken {

    public Keywords(Terminals t) {
        super(t);
    }

    @Override
    public String toString() {
        return getTerminal().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o.getClass() == this.getClass()) {
            return super.equals((BaseToken) o);
        }
        return true;
    }

    public static class Sentinel extends Keywords {

        public Sentinel() {
            super(Terminals.SENTINEL);
        }
    }

    public static class Becomes extends Keywords {

        public Becomes() {
            super(Terminals.BECOMES);
        }
    }
    
    public static class NotOpr extends Keywords {

        public NotOpr() {
            super(Terminals.NOTOPR);
        }
    }

    public static class DebugIn extends Keywords {

        public DebugIn() {
            super(Terminals.DEBUGIN);
        }
    }

    public static class DebugOut extends Keywords {

        public DebugOut() {
            super(Terminals.DEBUGOUT);
        }
    }
    
    public static class Program extends Keywords {

        public Program() {
            super(Terminals.PROGRAM);
        }
    }
    
    public static class Call extends Keywords {

        public Call() {
            super(Terminals.CALL);
        }
    }
    
    public static class If extends Keywords {

        public If() {
            super(Terminals.IF);
        }
    }
    
    public static class Else extends Keywords {

        public Else() {
            super(Terminals.ELSE);
        }
    }
    
    public static class EndIf extends Keywords {

        public EndIf() {
            super(Terminals.ENDIF);
        }
    }
    
    public static class Changemode extends Keywords {

        public Changemode() {
            super(Terminals.CHANGEMODE);
        }
    }
    
    public static class Mechmode extends Keywords {

        public Mechmode() {
            super(Terminals.MECHMODE);
        }
    }
    
    public static class Fun extends Keywords {

        public Fun() {
            super(Terminals.FUN);
        }
    }
    
    public static class Global extends Keywords {

        public Global() {
            super(Terminals.GLOBAL);
        }
    }
    
    public static class Local extends Keywords {

        public Local() {
            super(Terminals.LOCAL);
        }
    }
    
    public static class Init extends Keywords {

        public Init() {
            super(Terminals.INIT);
        }
    }
    
    public static class Proc extends Keywords {

        public Proc() {
            super(Terminals.PROC);
        }
    }
    
    public static class Returns extends Keywords {

        public Returns() {
            super(Terminals.RETURNS);
        }
    }
    
    public static class Skip extends Keywords {

        public Skip() {
            super(Terminals.SKIP);
        }
    }
    
    public static class Then extends Keywords {

        public Then() {
            super(Terminals.THEN);
        }
    }
    
    public static class While extends Keywords {

        public While() {
            super(Terminals.WHILE);
        }
    }
    
    public static class Do extends Keywords {

        public Do() {
            super(Terminals.DO);
        }
    }
    
    public static class EndWhile extends Keywords {

        public EndWhile() {
            super(Terminals.ENDWHILE);
        }
    }
    
    public static class EndProgram extends Keywords {

        public EndProgram() {
            super(Terminals.ENDPROGRAM);
        }
    }
    
    public static class EndFun extends Keywords {

        public EndFun() {
            super(Terminals.ENDFUN);
        }
    }
    
    public static class EndProc extends Keywords {

        public EndProc() {
            super(Terminals.ENDPROC);
        }
    }
}
