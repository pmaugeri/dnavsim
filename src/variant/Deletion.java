package variant;

public class Deletion extends Variant {

	private static double frequency;
	
	public Deletion (int minLength, int maxLength) {
		super();
		type = TYPE.DELETION;
		length = 0;
		while ((length = (int)(Math.random() * (double)maxLength) + 1) < minLength);
	}

	public int getLength() {
		return length;
	}

	@Override
	public void setFrequency(double freq) {
		Deletion.frequency = freq;
	}

	@Override
	public double getFrequency() {
		return Deletion.frequency;
	}

}
