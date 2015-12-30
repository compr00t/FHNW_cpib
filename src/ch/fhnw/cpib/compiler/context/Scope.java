package ch.fhnw.cpib.compiler.context;



import ch.fhnw.cpib.compiler.parser.AbsTree.TypedIdent;

@SuppressWarnings("rawtypes")
public final class Scope {
	private final StoreTable storeTable;
	
	public Scope() {
		this(new StoreTable());
	}
	
	public Scope(final StoreTable storeTable) {
		this.storeTable = storeTable;
	}
	
	public StoreTable getStoreTable() {
		return storeTable;
	}
	
	public TypedIdent getType(final String ident) {
		return storeTable.getType(ident);
	}
	
	public boolean addStore(final StoreTable stores) {
		for (String k : stores.getTable().keySet()) {
			if (!storeTable.addStore(k, stores.getTable().get(k))) {
				return false;
			}
		}
		return true;
	}
}
