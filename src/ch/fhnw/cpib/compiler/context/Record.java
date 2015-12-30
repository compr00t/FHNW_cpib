package ch.fhnw.cpib.compiler.context;

import java.util.ArrayList;

import ch.fhnw.cpib.compiler.parser.AbsTree.DeclarationRecordField;;

public class Record {
	
	@SuppressWarnings("unused")
	private Scope scope;
	private ArrayList<DeclarationRecordField> recordfields;
	private String ident;
	
	public Record(final String ident, final ArrayList<DeclarationRecordField> recordFields) {
		this.ident = ident;
		this.recordfields = recordFields;
		this.scope = new Scope();
	}

	public ArrayList<DeclarationRecordField> getRecordFields() {
		return this.recordfields;
	}
	
	public String getIdent() {
		return this.ident;
	}
}
