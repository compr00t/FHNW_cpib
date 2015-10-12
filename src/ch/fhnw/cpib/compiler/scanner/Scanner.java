package ch.fhnw.cpib.compiler.scanner;

import java.io.BufferedReader; 
import java.io.IOException;

import ch.fhnw.cpib.compiler.scanner.exception.LexicalError;
import ch.fhnw.cpib.compiler.scanner.enums.*;
import ch.fhnw.cpib.compiler.scanner.token.*;

public class Scanner {
    
    private enum State { INITIALSTATE, LITERALSTATE, IDENTIFIERSTATE, SYMBOLSTATE };
    
    State currentState;
    private ITokenList tokenList;
    String tmpHolder;

    public ITokenList scan(BufferedReader inBuffer) throws LexicalError, IOException {
        
        currentState = State.INITIALSTATE;
        tokenList = new TokenList();
        String currentLine = "";
        int lineNumber = 0;
        int charNumber = 0;
        
        while ((currentLine = inBuffer.readLine()) != null) {
            lineNumber++;
            
            for (int i = 0; i < currentLine.length(); i++) {
                charNumber++;   
                scanChar(currentLine.charAt(i), lineNumber, charNumber);
            }
            
        }
        
        return null;
    }

    private void scanChar(char c, int lineNumber, int charNumber) throws LexicalError {
        
        switch (currentState) {
        case INITIALSTATE:
            
            if ('0' <= c && c <= '9') {
                currentState = State.LITERALSTATE;
                tmpHolder += c;
            }
            
            if (('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z')) {
                currentState = State.IDENTIFIERSTATE;
                tmpHolder += c;
            }
            
            if (ScannerSymbols.contains((int)c)) { 
                currentState = State.SYMBOLSTATE;
                tmpHolder += c;
            }
            
            if ((' ' == c) || ('\t' == c) || ('\n' == c)) {
                currentState = State.INITIALSTATE;
            }
            
            if ('\u0003' == c) {
                IToken token = new Keywords.Sentinel();
                tokenList.add(token);
            }else{
                throw new LexicalError("unknown char at " + charNumber + ": " + c, lineNumber);
            }
            break;
            
        case LITERALSTATE:
            if (('0' <= c && c <= '9') ) {
                tmpHolder += c;
            }
            if ((' ' == c) || ('\t' == c) || ('\n' == c) || (ScannerSymbols.contains((int)c))) {
                IToken token = new Literal(Integer.parseInt(tmpHolder));
                tokenList.add(token);
                tmpHolder = null;
                currentState = State.INITIALSTATE;
            }
            if (ScannerSymbols.contains((int)c)||'\u0003'==c) { 
                IToken token = new Literal(Integer.parseInt(tmpHolder));
                tokenList.add(token);
                tmpHolder = null;
                currentState = State.INITIALSTATE;
                this.scanChar(c, lineNumber, charNumber);
            }else{
                throw new LexicalError("unknown char at " + charNumber + ": " + c, lineNumber);
            }
            
            break;
            
        case IDENTIFIERSTATE:
            if (('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z') || ('0' <= c && c <= '9') ) {
                tmpHolder += c;
            }
            if ((' ' == c) || ('\t' == c) || ('\n' == c)) {
                IToken token = new Ident(tmpHolder);
                tokenList.add(token);
                tmpHolder = null;
                currentState = State.INITIALSTATE; 
            }
            if (ScannerSymbols.contains((int)c)||'\u0003'==c) { 
                IToken token = new Ident(tmpHolder);
                tokenList.add(token);
                tmpHolder = null;
                currentState = State.INITIALSTATE;
                this.scanChar(c, lineNumber, charNumber);
            }else{
                throw new LexicalError("unknown char at " + charNumber + ": " + c, lineNumber);
            }
            
            break;
            
        case SYMBOLSTATE:
            if(ScannerSymbols.contains((int)c)){
                
            }
            if (('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z') || ('0' <= c && c <= '9') || (' ' == c) || ('\t' == c) || ('\n' == c)) {
                if(tmpHolder.length()>=1){
                    IToken token = findSymbol(tmpHolder);
                    if(token!=null){
                        tokenList.add(token);
                        tmpHolder = null;
                        currentState = State.INITIALSTATE;
                        this.scanChar(c, lineNumber, charNumber);
                    }else{
                        throw new LexicalError("Illegal length of a Symbol at "+ charNumber + ": " + c, lineNumber);
                    }                 
                    
                }
                else{
                    throw new LexicalError("Illegal length of a Symbol at "+ charNumber + ": " + c, lineNumber);
                }
                tmpHolder=null;
            }
            if ('\u0003'==c) { 
                
            }else{
                throw new LexicalError("unknown char at " + charNumber + ": " + c, lineNumber);
            }
            
            break;

        default:
            throw new LexicalError("unknown char at " + charNumber + ": " + c, lineNumber);
        }
    }
    
    private BaseToken findSymbol(String s){
        if(s.length() == 1){
            switch(s){
            case("("):
                return new Symbol.LParen();
            case(")"):
                return new Symbol.RParen();
            case("{"):
                return null;
            case("}"):
                return null;
            case(","):
                return new Symbol.Comma();
            case(":"):
                return new Symbol.Colon();
            case(";"):
                return new Symbol.Semicolon();
            case("="):
                break;
            case("*"):
                break;
            case("+"):
                break;
            case("-"):
                break;
            case("/"):
                break;
            case("<"):
                break;
            case(">"):
                break;
            case("."):
                break;
            default:
                return null;
            }
           
        }
        else if(s.length() == 2){
            switch(s){
            case("<="):
                return null;
            case(">="):
                return null;
            case(":="):
                return null;
            case("&&"):
                return null;
            case("&?"):
                return null;
            case("||"):
                return null;
            case("|?"):
                return null;
            default:
                return null;
            }        
        }
        return null;
    }
}
