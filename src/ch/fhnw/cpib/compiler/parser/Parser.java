package ch.fhnw.cpib.compiler.parser;

import ch.fhnw.cpib.compiler.exception.GrammarError;
import ch.fhnw.cpib.compiler.scanner.IToken;
import ch.fhnw.cpib.compiler.scanner.ITokenList;
import ch.fhnw.cpib.compiler.scanner.enums.Terminals;

public class Parser {
	
	private final ITokenList tokenList;
	private IToken token;
	private Terminals terminal;
	
	public Parser(final ITokenList tokenList){
		
		this.tokenList = tokenList;
		this.tokenList.reset();
		
		// precondition: token list contains at least the SENTINEL 
		token = tokenList.nextToken();
		
		// establish class invariant
		terminal = token.getTerminal();
	}
	
	private IToken consume(Terminals expectedTerminal) throws GrammarError {
		return null;
	}
}