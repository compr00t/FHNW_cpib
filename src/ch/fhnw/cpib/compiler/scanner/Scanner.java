package ch.fhnw.cpib.compiler.scanner;

import java.io.BufferedReader;
import java.io.IOException;

import ch.fhnw.cpib.compiler.exception.LexicalError;
import ch.fhnw.cpib.compiler.scanner.enums.*;
import ch.fhnw.cpib.compiler.scanner.token.*;

public class Scanner {

    /*
     * Implementation vom Scanner:
     * Stati als ENUM erstellt und in Switch-Case-Funktion implementiert.
     * 
     * Abweichungen:
     * - keine
     *  
     */
    
    private enum State {
        INITIALSTATE, LITERALSTATE, IDENTIFIERSTATE, SYMBOLSTATE
    };

    State currentState;
    private ITokenList tokenList;
    String tmpHolder = "";
    char oldHolder;

    public ITokenList scan(BufferedReader inBuffer) throws LexicalError, IOException {

        currentState = State.INITIALSTATE;
        tokenList = new TokenList();
        String currentLine = "";
        int lineNumber = 0;
        int charNumber = 0;

        while ((currentLine = inBuffer.readLine()) != null) {
            currentLine += '\n';
            lineNumber++;
            charNumber = 0;

            for (int i = 0; i < currentLine.length(); i++) {
                charNumber++;
                if (currentLine.charAt(i) == '/' && i < currentLine.length() && currentLine.charAt(i + 1) == '/') {
                    break;
                } else {
                    scanChar(currentLine.charAt(i), lineNumber, charNumber);
                }
            }
        }

        charNumber = 0;
        lineNumber++;
        for (char c : "sentinel\n".toCharArray()) {
            charNumber++;
            scanChar(c, lineNumber, charNumber);
        }

        return tokenList;
    }

    private void scanChar(char c, int lineNumber, int charNumber) throws LexicalError {

        switch (currentState) {
        case INITIALSTATE:

            if ('0' <= c && c <= '9') {
                currentState = State.LITERALSTATE;
                tmpHolder = "" + c;
            } else if (('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z')) {
                currentState = State.IDENTIFIERSTATE;
                tmpHolder = "" + c;
            } else if (ScannerSymbols.contains((int) c)) {
                currentState = State.SYMBOLSTATE;
                tmpHolder = "" + c;
                oldHolder = c;
            } else if ((' ' == c) || ('\t' == c) || ('\n' == c) || ('\r' == c)) {
                currentState = State.INITIALSTATE;
            } else if ('\u0003' == c) {
                IToken token = new Keywords.Sentinel();
                token.setLine(lineNumber);
                tokenList.add(token);
            } else {
                throw new LexicalError("unknown char", c, lineNumber, charNumber);
            }
            break;

        case LITERALSTATE:
            if (('0' <= c && c <= '9')) {
                tmpHolder += c;
            } else if ((' ' == c) || ('\t' == c) || ('\n' == c) || ('\r' == c)) {
                IToken token = new Literal(Integer.parseInt(tmpHolder));
                token.setLine(lineNumber);
                tokenList.add(token);
                tmpHolder = "";
                currentState = State.INITIALSTATE;
            } else if (ScannerSymbols.contains((int) c) || '\u0003' == c || ('A' <= c && c <= 'Z')
                    || ('a' <= c && c <= 'z')) {
                IToken token = new Literal(Integer.parseInt(tmpHolder));
                token.setLine(lineNumber);
                tokenList.add(token);
                tmpHolder = "";
                currentState = State.INITIALSTATE;
                this.scanChar(c, lineNumber, charNumber);
            } else {
                throw new LexicalError("unknown char", c, lineNumber, charNumber);
            }

            break;

        case IDENTIFIERSTATE:
            if (('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z') || ('0' <= c && c <= '9') || (c == '_') || (c == '`')) {
                tmpHolder += c;
            } else if ((' ' == c) || ('\t' == c) || ('\n' == c) || ('\r' == c)) {
                IToken token = scanKeyword(tmpHolder);
                token.setLine(lineNumber);
                tokenList.add(token);
                tmpHolder = "";
                currentState = State.INITIALSTATE;
            } else if (ScannerSymbols.contains((int) c) || '\u0003' == c) {
                IToken token = scanKeyword(tmpHolder);
                token.setLine(lineNumber);
                tokenList.add(token);
                tmpHolder = "";
                currentState = State.INITIALSTATE;
                this.scanChar(c, lineNumber, charNumber);
            } else {
                throw new LexicalError("unknown char", c, lineNumber, charNumber);
            }

            break;

        case SYMBOLSTATE:
            if (ScannerSymbols.contains((int) c)) {
                tmpHolder += c;
                oldHolder = c;
            } else if ('0' <= c && c <= '9' && (oldHolder == '-' || oldHolder == '+') ) {
                currentState = State.LITERALSTATE;
                if(tmpHolder.length()!=1){
                    parseSymbol(tmpHolder.substring(0, tmpHolder.length()-1), c, lineNumber, charNumber);
                    tmpHolder = "";                
                    tmpHolder += oldHolder;
                }
                
                this.scanChar(c, lineNumber, charNumber);
                
            } else if (('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z') || ('0' <= c && c <= '9') || (' ' == c)
                    || ('\t' == c) || ('\u0003' == c) || ('\n' == c) || ('\r' == c)) {
                
                parseSymbol(tmpHolder, c, lineNumber, charNumber); 
                tmpHolder = "";
                currentState = State.INITIALSTATE;
                this.scanChar(c, lineNumber, charNumber);
                
            }
            break;

        default:
            throw new LexicalError("unknown char", c, lineNumber, charNumber);
        }
    }
    
    private void parseSymbol(String symbols, char c, int lineNumber, int charNumber) throws LexicalError{
        if (symbols.length() >= 2) {

            IToken token;

            if (symbols.charAt(0) == '<' || symbols.charAt(0) == '>' || symbols.charAt(0) == '&'
                    || symbols.charAt(0) == '|' || symbols.charAt(0) == ':') {
                token = findSymbol(symbols);
                if (token != null) {
                	token.setLine(lineNumber);
                    tokenList.add(token);
                } else {
                    for (char ch : symbols.toCharArray()) {
                        token = findSymbol("" + ch);
                        if (token != null) {
                        	token.setLine(lineNumber);
                            tokenList.add(findSymbol("" + ch));
                        } else {
                            throw new LexicalError("unknown char", c, lineNumber, charNumber);
                        }
                    }
                }
            } else {
                for (char ch : symbols.toCharArray()) {
                    token = findSymbol("" + ch);
                    if (token != null) {
                    	token.setLine(lineNumber);
                        tokenList.add(findSymbol("" + ch));
                    } else {
                        throw new LexicalError("unknown char", c, lineNumber, charNumber);
                    }
                }
            }
         

        } else if (symbols.length() == 1) {
            IToken token = findSymbol(symbols);
            if (token != null) {
            	token.setLine(lineNumber);
                tokenList.add(token);
            } else {
                throw new LexicalError("unknown char", c, lineNumber, charNumber);
            }

        } else {
            throw new LexicalError("unknown char", c, lineNumber, charNumber);
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
            return new Type(TypeAttribute.INT64);
        case ("BOOL"):
            return new Type(TypeAttribute.BOOL);
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
        case ("CONST"):
            return new Mode.ChangeMode(ModeAttributes.CONST);
        case ("VAR"):
            return new Mode.ChangeMode(ModeAttributes.VAR);
        case ("REF"):
            return new Mode.MechMode(ModeAttributes.REF);
        case ("COPY"):
            return new Mode.MechMode(ModeAttributes.COPY);
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
        case ("MOD"):
        	return new Operator.MultOpr(OperatorAttribute.MOD);
        case ("DIV"):
        	return new Operator.MultOpr(OperatorAttribute.DIV);
        default:
            return new Ident(tmpHolder);
        }
    }

    private IToken findSymbol(String s) {
        if (s.length() == 1) {
            switch (s) {
            case ("("):
                return new Symbol.LParen();
            case (")"):
                return new Symbol.RParen();
            case ("]"):
                return new Symbol.RSBracket();
            case ("["):
                return new Symbol.LSBracket();
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
            default:
                return null;
            }
        }
        return null;
    }
}
