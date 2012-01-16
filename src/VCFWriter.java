import java.io.PrintWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;


public class VCFWriter extends PrintWriter {

	public static final String SAMPLE_NAME = "DNAVSim";
	
	public VCFWriter(Writer out) {
		super(out);
		
		// Print VCF header
		println("##fileformat=VCFv4.1");
		Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		println("##fileDate=" + format.format(now));
		println("##source=" + DNAVSim.PROGRAM_NAME + " v" + DNAVSim.PROGRAM_VERSION);
		println("#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\t" + SAMPLE_NAME);
	}

	public void printVariant(String chr, String pos, String ref, String alt) {
		print(chr);		// CHROM
		print("\t");
		print(pos);		// POS
		print("\t");
		print(".");		// ID
		print("\t");
		print(ref);		// REF
		print("\t");
		print(alt);		// ALT
		print("\t");
		print(".");		// QUAL
		print("\t");
		print("PASS");		// FILTER
		print("\t");
		
		// Calculate line and column positions in FASTA file considering line of 60 columns
		int p = Integer.parseInt(pos);
		int line = (p / 60) + 2;
		int col = p % 60;
		print("LINE:" + line + ", COL: " + col);		// INFO
		print("\t");
		print(".");		// FORMAT
		print("\t");
		print(".");		// SAMPLE NAME
		println();
	}
	
	public void close() {
		super.close();
	}
	
}
