package variant;

public class Snp extends Variant {
	
	private static double frequency;
	
	public Snp(char ref) {
		super();
		type = TYPE.SNP;
		length = 0;
		char alt;		
		do {
			double r = Math.random();
			if (r < 0.25)
				alt = 'A';
			else
			if (r < 0.50)
				alt = 'C';
			else
			if (r < 0.75)
				alt = 'G';
			else
				alt = 'T';
		}
		while (ref == alt);
		refBases = Character.toString(ref);
		altBases = Character.toString(alt);
	}

	@Override
	public void setFrequency(double freq) {
		Snp.frequency = freq;
	}

	@Override
	public double getFrequency() {
		return Snp.frequency;
	}

}
