import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

public class FastaFileWriter extends PrintWriter {

	// Number of bases per line
	private int basesPerLine;

	private CharArrayWriter buffer;
	
	/**
	 * 
	 * @param out
	 * @param columns number of bases per line in this FASTA file
	 */
	public FastaFileWriter(Writer out, int basesPerLine) {
		super(out);
		this.basesPerLine = basesPerLine;
		buffer = new CharArrayWriter(basesPerLine);
	}

	public void printHeaderLine(String hdr) {

		if (buffer.size()>0) {
			char out[] = buffer.toCharArray();
			println(out);
			buffer.flush();
			buffer.reset();
		}
		
		println(hdr);
	}

	/**
	 * This method will flush the internal buffer to the file
	 * if thme buffer reaches basesPerLine.
	 */
	private void printToFile() {
		if (buffer.size()>=basesPerLine) {
			char out[] = buffer.toCharArray();
			println(new String(out, 0, basesPerLine));
			buffer.flush();
			buffer.reset();
			buffer.write(out, basesPerLine, out.length-basesPerLine);
		}

	}
	
	public void printBases(String bases) throws IOException {
		buffer.write(bases);
		printToFile();
	}
	
	public void printBase(char base) {
		buffer.write(base);
		printToFile();
	}
		
	public void close() {
		if (buffer.size()>0) {
			char out[] = buffer.toCharArray();
			println(out);
			buffer.flush();
			buffer.reset();
		}
		super.close();
	}

}
