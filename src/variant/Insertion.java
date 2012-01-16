package variant;


public class Insertion extends Variant {

	/**
	 * Constructor.
	 * Generate a insertion which length is greater than minLength and
	 * smaller than maxLength (both inclusives).
	 * 
	 * @param ref the reference base where the insertion will occur
	 * @param minLength
	 * @param maxLength
	 */
	public Insertion(char ref, int minLength, int maxLength) {
		refBases = Character.toString(ref);
		altBases = "";
		length = 0;
		while ((length = (int)(Math.random() * (double)maxLength) + 1) < minLength);
		for (int i=0; i<length; i++) {
			double r = Math.random();
			if (r < 0.25)
				altBases += "A";
			else
			if (r < 0.50)
				altBases += "C";
			else
			if (r < 0.75)
				altBases += "G";
			else
				altBases += "T";				
		}
	}

}
