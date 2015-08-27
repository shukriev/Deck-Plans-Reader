package src;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ImageCrop
{
	public static int cropCount = 0;
	
	public static void cropImage(String src, Rectangle rect, String name) {
	     try {
	    	 File imageFile = new File(src);
			 BufferedImage in = ImageIO.read(imageFile);
			 BufferedImage newImage = in;
			 BufferedImage dest = newImage.getSubimage(rect.x, rect.y, rect.width, rect.height);
			 DescplanReader.decks.get(cropCount).setImageNumber(name + "_" + cropCount + ".tif");;
			 File outputfile = new File(name + "_" + cropCount + ".tif");
		      
			 if(dest.getHeight() > dest.getWidth()){
				 dest = rotate(dest);
			 }
			 
	    	 ImageIO.write(dest, "tif", outputfile);
			 cropCount++;
		} catch (Exception e) {
			e.printStackTrace();
		}

	 }
	public static BufferedImage rotate(BufferedImage image) {        
		BufferedImage buffer = image;
		AffineTransform tx = new AffineTransform();
		tx.rotate(1.57079633, buffer.getWidth(), buffer.getHeight());

		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
		return buffer = op.filter(buffer, null);
		}
}