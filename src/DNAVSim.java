import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import variant.Deletion;
import variant.Insertion;
import variant.Snp;


public class DNAVSim {

	public Logger log;
	
	public static String PROGRAM_NAME = "DNA Variant Simulator";
	public static String PROGRAM_VERSION = "0.1";
	public static String CONFIG_FILENAME = "dnavsim.cfg";

	public static String FASTA_FILE_COLUMNS_KEY = "out.fasta.columns";
	public static String SNP_FREQUENCY_KEY = "snp.frequency";
	public static String INDEL_FREQUENCY_KEY = "indel.frequency";
	public static String INDEL_INSERTION_FREQUENCY_KEY = "indel.insertion.frequency";
	public static String INDEL_LENGTH_MIN_KEY = "indel.length.min";
	public static String INDEL_LENGTH_MAX_KEY = "indel.length.max";
	public static String CONFIG_KEYS[] = { SNP_FREQUENCY_KEY,
			INDEL_FREQUENCY_KEY, INDEL_INSERTION_FREQUENCY_KEY,
			INDEL_LENGTH_MIN_KEY, INDEL_LENGTH_MAX_KEY, FASTA_FILE_COLUMNS_KEY };

	/**
	 * Configuration parameters loaded from configuration file
	 */
	private int FASTA_FILE_COLUMNS;
	private double SNP_FREQ;
	private double INDEL_FREQ;
	private double INSERTION_FREQ;
	private int INDEL_MIN_LENGTH;
	private int INDEL_MAX_LENGTH;
	
	// The configuration file 
	private Properties cfg;
	
	
	/**
	 * Parse a FASTA header line to find out the chromosome name
	 * corresponding to the subsequent sequence
	 * 
	 * @param line the FASTA header line (should start by ">"
	 * @return the chromosome name (e.g. 1, 2, 3, ..., X, or Y)
	 */
	private String parseFastaHeaderLine(String line) {
		
		int index = -1;
		String result = null;
		if ((index = line.indexOf("chr")) != -1) {
			result = line.substring(index+3);
		}
		else 
		if ((index = line.indexOf("chrom")) != -1) {
			result = line.substring(index+5);
		}
		else 
			result = line.substring(1).trim();
		
		// Remove optional loci 
		if (result.indexOf(':') != -1)
			result = result.substring(0, result.indexOf(':'));
		
		return result;
	}

	/**
	 * load the configuration file and do a sanity check of the parameters
	 */
	private void loadConfiguration() {

		log.info("Loading properties from 'dnavsim.cfg' file...");
		try {
			cfg = new Properties();
			cfg.load(new FileInputStream(CONFIG_FILENAME));
		} catch (FileNotFoundException e1) {
			log.severe("An error occured (File not found exception) while loading configuration file '" + CONFIG_FILENAME + "'! Exiting...");
			System.exit(1);
		} catch (IOException e1) {
			log.severe("An IO error occured while loading configuration file '" + CONFIG_FILENAME + "'! Exiting...");
			System.exit(1);
		}
		for (int i=0; i<CONFIG_KEYS.length; i++) {
			if (cfg.getProperty(CONFIG_KEYS[i]) == null) {
				log.severe("The configuration file has not the key '" + CONFIG_KEYS[i] + "' set! Please edit the file '" + CONFIG_FILENAME + "' and add this configuration parameter. Exiting...");
				System.exit(1);				
			}
		}
		// SNP frequency parameter
		SNP_FREQ = Double.parseDouble(cfg.getProperty(SNP_FREQUENCY_KEY)); 
		if (SNP_FREQ < 0 || SNP_FREQ > 1) {
			log.severe("The configuration parameter "
					+ SNP_FREQUENCY_KEY
					+ " should be in the range [0.0-1.0]! Please correct. Exiting...");
			System.exit(1);
		}

		// INDEL frequency parameter
		INDEL_FREQ = Double.parseDouble(cfg.getProperty(INDEL_FREQUENCY_KEY)); 
		if (INDEL_FREQ < 0 || INDEL_FREQ > 1) {
			log.severe("The configuration parameter "
					+ INDEL_FREQUENCY_KEY
					+ " should be in the range [0.0-1.0]! Please correct. Exiting...");
			System.exit(1);
		}

		// Indel min/max length
		INDEL_MIN_LENGTH = Integer.parseInt(cfg.getProperty(INDEL_LENGTH_MIN_KEY));
		INDEL_MAX_LENGTH = Integer.parseInt(cfg.getProperty(INDEL_LENGTH_MAX_KEY));
		if (INDEL_MIN_LENGTH > INDEL_MAX_LENGTH) {
			log.severe("The configuration parameter " + INDEL_LENGTH_MIN_KEY + " can't be greater than " + INDEL_LENGTH_MAX_KEY+"! Please correct. Exiting...");
			System.exit(1);
		}
		if (INDEL_MIN_LENGTH <= 0 || INDEL_MAX_LENGTH <= 0) {
			log.severe("The configuration parameters " + INDEL_MIN_LENGTH + " and " + INDEL_MAX_LENGTH + " can't be zero or negative! Please correct. Exiting...");
			System.exit(1);
		}
		
		// FASTA file columns [1-120]
		FASTA_FILE_COLUMNS = Integer.parseInt(cfg.getProperty(FASTA_FILE_COLUMNS_KEY));
		if (FASTA_FILE_COLUMNS < 1 || FASTA_FILE_COLUMNS > 120) {
			log.severe("The configuration parameter "
					+ FASTA_FILE_COLUMNS_KEY
					+ " should be in the range [1-120]! Please correct. Exiting...");
			System.exit(1);
		}
		
		// Insertion frequency [0.0-1.0]
		INSERTION_FREQ = Double.parseDouble(cfg.getProperty(INDEL_INSERTION_FREQUENCY_KEY)); 
		if (INSERTION_FREQ < 0 || INSERTION_FREQ > 1) {
			log.severe("The configuration parameter "
					+ INDEL_INSERTION_FREQUENCY_KEY
					+ " should be in the range [0.0-1.0]! Please correct. Exiting...");
			System.exit(1);
		}
				
	}
	
	
	/**
	 * Constructor.
	 * Initialize.
	 * 
	 * @param inFasta the input FASTA file (reference genome)
	 * @param outFasta the output FASTA file (reference genome with simulated variations)
	 */
	public DNAVSim(String inFasta, String outFasta, String vcfFile) {
		
		// Initialize logger
		log = Logger.getLogger("DNAVSim");
		log.setLevel(Level.FINER);
		
		log.info("Logger DNAVSim initialized");

		log.info("Reading reference genome from file '" + inFasta + "'...");
		log.info("Writinng modified genome to '" + outFasta + "'...");
		log.info("Writing variants to VCF file '" + vcfFile + "'...");
	
		loadConfiguration();
		
		
		BufferedReader in = null;
		FastaFileWriter out = null;
		VCFWriter vcf = null;
		try {
			in = new BufferedReader(new FileReader(inFasta));
			out = new FastaFileWriter(new BufferedWriter(new FileWriter(
					outFasta)), Integer.parseInt(cfg
					.getProperty(FASTA_FILE_COLUMNS_KEY)));
			vcf = new VCFWriter(new BufferedWriter(new FileWriter(vcfFile)));
			
		} catch (FileNotFoundException e) {
			log.severe("File not found: " + e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			log.severe("IO Exception when opening I/O files: " + e.getMessage());
			System.exit(1);
		}
		log.info("FASTA files successfully opened");
		
		log.info("Processing FASTA file '" + inFasta + "'...");
		
		String line = null;
		
		// important variables: chr and pos gives the current locus of the AA
		// in a chromosome (absolute locus is therefore defined by <chr>:<pos>)
		// being read in inFasta file (original genome file)
		String chr = "";
		int pos = 0; 
		
		// Initialize random numbers generator
		Math.random();
		
		try {
			
			while ((line = in.readLine()) != null) {
		
				// Unchanged copy directly to output when line starts by ";" or "#"
				if (line.startsWith(";") || line.startsWith(">")) { 
					out.printHeaderLine(line);
					chr = parseFastaHeaderLine(line);
					pos = 0;
					log.info("Starting chromosome '" + chr + "'...");
				}
				else {
					
					char lineChar[] = new char[line.length()];
					line.getChars(0, line.length(), lineChar, 0);
					int lineLength = line.length();
					
					// Respectively the REF and ALT column value to be written in VCF file
					char ref;
					String alt = "";
					
					for (int i=0; i<lineLength; i++) {
						
						pos++;
						
						ref = lineChar[i];

						if (ref != 'N') {

							double r = Math.random();

							/**
							 * create INDEL
							 */
							if (r < INDEL_FREQ) {
								
								/**
								 * Insertion
								 */
								if (Math.random() < INSERTION_FREQ) {
									Insertion ins = new Insertion(ref, INDEL_MIN_LENGTH, INDEL_MAX_LENGTH);
									alt =  ref + ins.getAlternate();
									out.printBases(alt);
									vcf.printVariant(chr, Integer.toString(pos), Character.toString(ref), alt);
								}
								
								/**
								 * Deletion
								 */
								else {
									Deletion del = new Deletion(INDEL_MIN_LENGTH, INDEL_MAX_LENGTH);
									alt = Character.toString(ref);
									String refBases = Character.toString(ref);
									
									// Skip 'length' nucleotide(s) from input 
									for (int j=0; j<del.getLength(); j++) {
										i++;
										
										// End of line, read next one
										if (i==line.length()) {
											line = in.readLine();
											lineChar = new char[line.length()];
											line.getChars(0, line.length(), lineChar, 0);
											lineLength = line.length();
											if (line == null) { // Reduce deletion length as we reached EOF
												break;
											}
											if (line.startsWith(";") || line.startsWith(">")) { 
												out.println(line);
												chr = parseFastaHeaderLine(line);
												pos = 1;
												log.info("Starting chromosome '" + chr + "'...");
												break;
											}
											else {
												i = 0;
											}
										}
										refBases += lineChar[i];
									}
									
									vcf.printVariant(chr, Integer.toString(pos), refBases, alt);
									out.printBase(ref);
									pos += del.getLength();
								}
								
							}
							else
								
							/**
							 * create SNP
							 */
							if (r < SNP_FREQ) {
								Snp s = new Snp(lineChar[i]);
								alt = s.getAlternate();
								vcf.printVariant(chr, Integer.toString(pos), Character.toString(ref), alt);
								out.printBases(alt);
							}

							/**
							 * No variation
							 */
							else {
								out.printBase(ref);
							}

						}
						
						/**
						 * No variation
						 */
						else {
							out.printBase(ref);
						}
						
					}
					
				}
			}
			
		} catch (IOException e) {
			log.severe("IO Exception when reading genome FASTA file!\n" + e.getMessage());
			System.exit(1);
		}
		
		
		// Closing FASTA files
		try {
			in.close();
			out.close();
			vcf.close();
		} catch (IOException e) {
			log.severe("IO Error when closing FASTA file!\n" + e.getMessage());
		}

		log.info("Done. Exiting...");

	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		
		if (args.length < 3) {
			System.out.println("Usage: java DNAVSim [genome FASTA file name] [output file (FASTA file)] [output VCF file]");
			System.exit(1);
		}
		DNAVSim sim = new DNAVSim(args[0], args[1], args[2]);
		
	}

}
