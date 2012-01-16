package variant;

/**
 * Helper class to create and handle variants
 * 
 * 
 * @author pmaugeri
 */
public class Variant {

	protected String refBases;
	protected String altBases;
	
	public enum type {
		INSERTION, DELETION
	}
	
	/**
	 * Negative when variant is a deletion,
	 * Positive when variant is insertion
	 * Null when SNP
	 */
	protected int length = 0;
	
	protected Variant() {
		Math.random(); // init
	}
	
	public Variant.type getRandomType() {
		if (Math.random() < 0.5) 
			return Variant.type.INSERTION;
		else
			return Variant.type.DELETION;
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

}
