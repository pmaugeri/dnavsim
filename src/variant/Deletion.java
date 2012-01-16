package variant;


public class Deletion extends Variant {

	public Deletion (int minLength, int maxLength) {
		length = 0;
		while ((length = (int)(Math.random() * (double)maxLength) + 1) < minLength);
	}

	public int getLength() {
		return length;
	}

}
