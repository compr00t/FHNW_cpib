package ch.fhnw.cpib.compiler.context;

import ch.fhnw.cpib.compiler.parser.AbsTree.TypedIdent;


@SuppressWarnings("rawtypes")
public abstract class Symbol {
	private String ident;
	private TypedIdent type;
	
	public final String getIdent() {
		return ident;
	}
	
	public final TypedIdent getType() {
		return type;
	}
	
	protected Symbol(final String ident, final TypedIdent type) {
		this.ident = ident;
		this.type = type;
	}
}