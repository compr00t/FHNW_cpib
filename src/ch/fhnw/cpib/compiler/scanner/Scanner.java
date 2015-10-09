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
    int tmpHolder;

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
                tmpHolder = c;
            }
            
            if (('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z')) {
                currentState = State.IDENTIFIERSTATE;
                tmpHolder = c;
            }
            
            if (ScannerSymbols.contains((int)c)) {
                currentState = State.SYMBOLSTATE;
                tmpHolder = c;
            }
            
            if ((' ' == c) || ('\t' == c) || ('\n' == c)) {
                currentState = State.INITIALSTATE;
            }
            
            if ('\u0003' == c) {
                IToken token = new Keywords.Sentinel();
                tokenList.add(token);
            }
            break;
            
        case LITERALSTATE:
            
            break;
            
        case IDENTIFIERSTATE:
            
            break;
            
        case SYMBOLSTATE:
            
            break;

        default:
            throw new LexicalError("unknown char at " + charNumber + ": " + c, lineNumber);
        }
    }
}
