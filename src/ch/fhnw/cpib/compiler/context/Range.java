package ch.fhnw.cpib.compiler.context;

public final class Range {

	private final int start;
	private final int end;
	private final int size;
	private final int offset;
	private int address;
	
	public Range(int start, int end) {
		this.start = start;
		this.end = end;
		this.size = (end - start)+1;
		this.offset = 0 + start;
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
	
	public int getOffset() {
        return offset;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }
	
}
