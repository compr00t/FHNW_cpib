package ch.fhnw.cpib.compiler.context;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import ch.fhnw.cpib.compiler.parser.AbsTree.TypedIdent;

@SuppressWarnings("rawtypes")
public final class StoreTable {
	
	private Map<String, Object> storeMap;
	
	public StoreTable() {
		storeMap = new TreeMap<String, Object>();
	}
	
	public boolean addStore(final String key, final Object s) {
		if (s instanceof ArrayList<?> || s instanceof Store) {
			if (!storeMap.containsKey(key)) {
				storeMap.put(key, s);
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public TypedIdent getType(final String ident) {
		if (storeMap.containsKey(ident)) {
			/*if (storeMap.get(ident) instanceof ArrayList<?>) {
				throw new InvalidParameterException("This should not be happening");
			} else {*/
				return ((Store)storeMap.get(ident)).getType();
			//}
		} else {
			return null;
		}
	}
	
	public boolean containsIdent(final String ident) {
		return(storeMap.containsKey(ident));
	}
	
	public Object getStore(final String ident) {
		return storeMap.get(ident);
	}
	
	public Map<String, Object> getTable() {
		return storeMap;
	}
	
	public StoreTable clone() {
		StoreTable newTable = new StoreTable();
		Set<String> keys = storeMap.keySet();
		for (String key : keys) {
			Store store = (Store)storeMap.get(key);
			newTable.addStore(key, store.clone());
		}
		return newTable;
	}
}
