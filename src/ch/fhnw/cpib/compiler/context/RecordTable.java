package ch.fhnw.cpib.compiler.context;

import java.util.Map;
import java.util.TreeMap;

public final class RecordTable {
private Map<String, Record> recordMap;

	public RecordTable() {
		recordMap = new TreeMap<String, Record>();
	}
	
	public boolean addRecord(final Record record) {
		if (!recordMap.containsKey(record.getIdent())) {
			recordMap.put(record.getIdent(), record);
			return true;
		} else {
			return false;
		}
	}
	
	public Record getRecord(final String ident) {
		return recordMap.get(ident);
	}
	
	public boolean containsRecord(final String ident) {
		return recordMap.containsKey(ident);
	}
	
	public Map<String, Record> getTable() {
		return recordMap;
	}
}
