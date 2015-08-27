package src;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sourceforge.tess4j.Tesseract1;
import net.sourceforge.tess4j.TesseractException;

public class MyTesseract
{
	public static HashMap<String, String> numbers = new HashMap<String, String>();
//	private static List<String> numbers = new ArrayList<String>();

    public static void doOCR(String imagePath, String imageName, String startNumber) {
       System.setProperty("jna.library.path", "32".equals(System.getProperty("sun.arch.data.model")) ? "lib/win32-x86" : "lib/win32-x86-64");
       File imageFile = new File(imagePath + imageName);

        //Tesseract instance = Tesseract.getInstance(); // JNA Interface Mapping
        Tesseract1 instance = new Tesseract1(); // JNA Direct Mapping
        instance.setTessVariable("tessedit_char_whitelist", "0123456789");
        try {
        	
        	String result = instance.doOCR(imageFile);

        	String[] NumbersInFile = result.split("[\n \\s]+");
        	int digit = 4;
            for (String number : NumbersInFile) {
            	if (number.length() == digit) {
					if (number.startsWith(startNumber)) {
//						if (!numbers.contains(number)) {
							System.out.println(number);
							numbers.put(number, imageName);	
//						}
					}
				}else if (number.length() > digit){
					for (int i = number.length(); i > 0; i--) {
						String number_without_front = number.substring(i, number.length());
						String number_without_back = number.substring(0, number.length()-i);
						
						if (number_without_front.length() == digit && number_without_front.startsWith(startNumber)) {
//							if (!numbers.contains(number_without_front)) {
								System.out.println(number_without_front);
								numbers.put(number_without_front, imageName);	
//							}
						}
						
						if (number_without_back.length() == digit && number_without_back.startsWith(startNumber)) {
//							if (!numbers.contains(number_without_back)) {
								System.out.println(number_without_back);
								numbers.put(number_without_back, imageName);	
//							}
						}
					}
				}
            }
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
    }
}
