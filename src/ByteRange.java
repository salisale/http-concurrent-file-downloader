
public class ByteRange {
	private long i, f;
	public ByteRange(long i, long f) {
		this.i = i;
		this.f = f;
	}
	public long getStartingByte() {return i;}
	public long getEndingByte() {return f;}
}
