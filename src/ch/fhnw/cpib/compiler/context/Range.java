package ch.fhnw.cpib.compiler.context;

public final class Range {

	private final int start;
	private final int end;
	private final int size;
	
	public Range(int start, int end) {
		this.start = start;
		this.end = end;
		this.size = (end - start);
	}
	
	public int getStart() {
		return start;
	}
	
	public int getEnd() {
		return end;
	}
	
	public int getSize() {
		return size;
	}
	
}
