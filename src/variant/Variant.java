package variant;

/**
 * Helper class to create and handle variants
 * 
 * 
 * @author pmaugeri
 */
public abstract class Variant {

	protected String refBases;
	protected String altBases;
	
	public enum TYPE {
		SNP, INSERTION, DELETION, NONE
	}
	
	protected TYPE type;
	
	/**
	 * Negative when variant is a deletion,
	 * Positive when variant is insertion
	 * Null when SNP
	 */
	protected int length = 0;
	
	protected Variant() {
		Math.random(); // init
		this.type = TYPE.NONE;
		
	}
	
	public Variant.TYPE getRandomType() {
		if (Math.random() < 0.5) 
			return Variant.TYPE.INSERTION;
		else
			return Variant.TYPE.DELETION;
	}

	public int getLength() {
		return length;
	}

	/** 
	 * @return the reference bases (from the original genome)
	 */
	public String getReference() {
		return refBases;
	}
	
	/**
	 * @return the alternate bases (variant modification)
	 */
	public String getAlternate() {
		return altBases;
	}

	public static void setVariantTypeFreq(TYPE type, double freq) {
		
	}

	public TYPE getType() {
		return type;
	}
	
	public abstract void setFrequency(double freq);
	public abstract double getFrequency();
	
}
