package ch.fhnw.cpib.compiler.scanner;

import java.io.BufferedReader;
import java.io.IOException;

import ch.fhnw.cpib.compiler.scanner.exception.LexicalError;
import ch.fhnw.cpib.compiler.scanner.enums.*;
import ch.fhnw.cpib.compiler.scanner.token.*;

public class Scanner {

    private enum State {
        INITIALSTATE, LITERALSTATE, IDENTIFIERSTATE, SYMBOLSTATE
    };

    State currentState;
    private ITokenList tokenList;
    String tmpHolder = "";

    public ITokenList scan(BufferedReader inBuffer) throws LexicalError, IOException {

        currentState = State.INITIALSTATE;
        tokenList = new TokenList();
        String currentLine = "";
        int lineNumber = 0;
        int charNumber = 0;

        while ((currentLine = inBuffer.readLine()) != null) {
            lineNumber++;
            charNumber = 0;
            
            for (int i = 0; i < currentLine.length(); i++) {
                charNumber++;
                scanChar(currentLine.charAt(i), lineNumber, charNumber);
            }

        }

        return tokenList;
    }

    private void scanChar(char c, int lineNumber, int charNumber) throws LexicalError {

        switch (currentState) {
        case INITIALSTATE:
            
            if ('0' <= c && c <= '9') {
                currentState = State.LITERALSTATE;
                tmpHolder = "" + c;
            }else if (('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z')) {
                currentState = State.IDENTIFIERSTATE;
                tmpHolder = "" + c;
            }else if (ScannerSymbols.contains((int) c)) {
                currentState = State.SYMBOLSTATE;
                tmpHolder = "" + c;
            }else if ((' ' == c) || ('\t' == c) || ('\n' == c)) {
                currentState = State.INITIALSTATE;
            }else if ('\u0003' == c) {
                IToken token = new Keywords.Sentinel();
                tokenList.add(token);
            } else {
                throw new LexicalError("unknown char at " + charNumber + ": " + c, lineNumber);
            }
            break;

        case LITERALSTATE:
            if (('0' <= c && c <= '9')) {
                tmpHolder += c;
            }else if ((' ' == c) || ('\t' == c) || ('\n' == c) || (ScannerSymbols.contains((int) c))) {
                IToken token = new Literal(Integer.parseInt(tmpHolder));
                tokenList.add(token);
                tmpHolder = "";
                currentState = State.INITIALSTATE;
            }else if (ScannerSymbols.contains((int) c) || '\u0003' == c) {
                IToken token = new Literal(Integer.parseInt(tmpHolder));
                tokenList.add(token);
                tmpHolder = "";
                currentState = State.INITIALSTATE;
                this.scanChar(c, lineNumber, charNumber);
            } else {
                throw new LexicalError("unknown char at " + charNumber + ": " + c, lineNumber);
            }

            break;

        case IDENTIFIERSTATE:
            if (('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z') || ('0' <= c && c <= '9')) {
                tmpHolder += c;
            }else if ((' ' == c) || ('\t' == c) || ('\n' == c)) {
                IToken token = scanKeyword(tmpHolder);
                if(token==null){
                    tokenList.add(new Ident(tmpHolder));
                }else{
                    tokenList.add(token);
                }
                tmpHolder = "";
                currentState = State.INITIALSTATE;
            }else if (ScannerSymbols.contains((int) c) || '\u0003' == c) {
                IToken token = scanKeyword(tmpHolder);
                if(token==null){
                    tokenList.add(new Ident(tmpHolder));
                }else{
                    tokenList.add(token);
                }
                tmpHolder = "";
                currentState = State.INITIALSTATE;
                this.scanChar(c, lineNumber, charNumber);
            } else {
                throw new LexicalError("unknown char at " + charNumber + ": " + c, lineNumber);
            }

            break;

        case SYMBOLSTATE:
            if (ScannerSymbols.contains((int) c)) {
                tmpHolder+=c;
            } else if (('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z') || ('0' <= c && c <= '9') || (' ' == c) || ('\t' == c)
                    || ('\n' == c)) {
                if (tmpHolder.length() >= 1) {
                    if(tmpHolder.equals("()")){
                        IToken tokenA = findSymbol("(");
                        tokenList.add(tokenA);
                        IToken tokenB = findSymbol(")");
                        tokenList.add(tokenB);
                        tmpHolder = "";
                        currentState = State.INITIALSTATE;
                        this.scanChar(c, lineNumber, charNumber);
                    }else if(tmpHolder.equals(");")){
                            IToken tokenA = findSymbol(")");
                            tokenList.add(tokenA);
                            IToken tokenB = findSymbol(";");
                            tokenList.add(tokenB);
                            tmpHolder = "";
                            currentState = State.INITIALSTATE;
                            this.scanChar(c, lineNumber, charNumber);
                    }else{
                        IToken token = findSymbol(tmpHolder);
                        if (token != null) {
                            tokenList.add(token);
                            tmpHolder = "";
                            currentState = State.INITIALSTATE;
                            this.scanChar(c, lineNumber, charNumber);
                        } else {
                            throw new LexicalError("Illegal length of a Symbol at " + charNumber + ": " + c, lineNumber);
                        }  
                    }             
                } else {
                    throw new LexicalError("Illegal length of a Symbol at " + charNumber + ": " + c, lineNumber);
                }
                tmpHolder = "";
            } else if ('\u0003' == c) {

            } else {
                throw new LexicalError("unknown char at " + charNumber + ": " + c, lineNumber);
            }

            break;

        default:
            throw new LexicalError("unknown char at " + charNumber + ": " + c, lineNumber);
        }
    }

    private IToken scanKeyword(String tmpHolder) {
            
        switch (tmpHolder.toUpperCase()) {
        
        case ("DEBUGIN"):
            return new Keywords.DebugIn();
        case ("DEBUGOUT"):
            return new Keywords.DebugOut();
        case ("PROGRAM"):
            return new Keywords.Program();
        case ("INT64"):
            return new BaseType.Type(TypeAttribute.INT64);
        case ("BOOL"):
            return new BaseType.Type(TypeAttribute.BOOL);
        case ("CALL"):
            return new Keywords.Call();
        case ("NOT"):
            return new Keywords.NotOpr();
        case ("IF"):
            return new Keywords.If();
        case ("ELSE"):
            return new Keywords.Else();
        case ("ENDIF"):
            return new Keywords.EndIf();
        case ("FLOWMODE"):
            return null;
        case ("CHANGEMODE"):
            return null;
        case ("MECHMODE"):
            return null;
        case ("FUN"):
            return new Keywords.Fun();
        case ("GLOBAL"):
            return new Keywords.Global();
        case ("LOCAL"):
            return new Keywords.Local();
        case ("INIT"):
            return new Keywords.Init();
        case ("PROC"):
            return new Keywords.Proc();
        case ("RETURNS"):
            return new Keywords.Returns();
        case ("SKIP"):
            return new Keywords.Skip();
        case ("THEN"):
            return new Keywords.Then();
        case ("WHILE"):
            return new Keywords.While();
        case ("DO"):
            return new Keywords.Do();
        case ("ENDWHILE"):
            return new Keywords.EndWhile();
        case ("ENDPROGRAM"):
            return new Keywords.EndProgram();
        case ("ENDFUN"):
            return new Keywords.EndFun();
        case ("ENDPROC"):
            return new Keywords.EndProc();
        case ("SENTINEL"):
            return new Keywords.Sentinel();
        default:
            return null;
        }
    }

    private IToken findSymbol(String s) {
        if (s.length() == 1) {
            switch (s) {
            
            case ("("):
                return new Symbol.LParen();
            case (")"):
                return new Symbol.RParen();
            case ("{"):
                return null;
            case ("}"):
                return null;
            case (","):
                return new Symbol.Comma();
            case (":"):
                return new Symbol.Colon();
            case (";"):
                return new Symbol.Semicolon();
            case ("="):
                return new Operator.RelOpr(OperatorAttribute.EQ);
            case ("*"):
                return new Operator.MultOpr(OperatorAttribute.TIMES);
            case ("+"):
                return new Operator.AddOpr(OperatorAttribute.PLUS);
            case ("-"):
                return new Operator.AddOpr(OperatorAttribute.MINUS);
            case ("/"):
                return new Operator.MultOpr(OperatorAttribute.DIV);
            case ("<"):
                return new Operator.RelOpr(OperatorAttribute.LT);
            case (">"):
                return new Operator.RelOpr(OperatorAttribute.GT);
            case ("."):
                return new Operator.DotOpr();
            default:
                return null;
            }

        } else if (s.length() == 2) {
            switch (s) {
            
            case ("<="):
                return new Operator.RelOpr(OperatorAttribute.LE);
            case (">="):
                return new Operator.RelOpr(OperatorAttribute.GE);
            case (":="):
                return new Keywords.Becomes();
            case ("&&"):
                return null;
            case ("&?"):
                return new Operator.BoolOpr(OperatorAttribute.CAND);
            case ("||"):
                return null;
            case ("|?"):
                return new Operator.BoolOpr(OperatorAttribute.COR);
            case ("()"):
                
            default:
                return null;
            }
        }
        return null;
    }
}
