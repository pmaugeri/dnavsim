import java.io.CharArrayWriter;
import java.io.IOException;
import java.nio.CharBuffer;



public class Test {

	
	
	
	public static void main(String[] args) {
	
	
		CharBuffer b = CharBuffer.allocate(1024);
		
		CharArrayWriter caw = new CharArrayWriter(60);
		System.out.println("size: " + caw.size());
		
		try {
			caw.write("Hello");
			System.out.println("size: " + caw.size());
			caw.write(' ');
			System.out.println("size: " + caw.size());
			caw.write("world!");		
			System.out.println("size: " + caw.size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (caw.size()>5) {
			char out[] = caw.toCharArray();
			System.out.println(new String(out, 0, 5));
			caw.flush();
			caw.reset();
			System.out.println("size: " + caw.size());
			caw.write(out, 5, out.length-5);
			System.out.println("size: " + caw.size());
			out = caw.toCharArray();
			System.out.println(new String(out, 0, 5));
		}
		
		
		
	}
	
}
