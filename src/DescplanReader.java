package src;


import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.opencv_core.CvPoint;
import org.bytedeco.javacv.*;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.*;

public class DescplanReader extends Shape{
    static int thresh = 50;
    static IplImage img = null;
    static IplImage img0 = null;
    static CvMemStorage storage = null;
    static String wndname = "Desc plan reader";
    static CanvasFrame canvas = null;
    
    public static List<Deck> decks = new ArrayList<Deck>();
    
    //Area of a square/rectangle to find
    //  74 x 24 = 1776 | 22 x 50 = 1100 (25 x 76 = 1900 | 23 x 46 = 1058)
    private static int areaMin = 200;
    private static int areaMax = 8000;//1900 + 2000;
    
    //Image get/save path
    private static String image_path = "samples/";
    
    //Image From path
    private static String imageName = "acc_qs_deck06-010516.jpg";
    
    private static String loadImageFromPath = image_path + imageName;
    
    private static String fileNameWithoutExtention = "acc_qs_deck06-010516";
    
    private static String loadImageForCropping = image_path + fileNameWithoutExtention + "_crop.jpg";
  
    private static String first_digit = "6";
    
    private static String csvPath = "Result\\Targetlist.csv";
    //Functions
    static OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();

    static CvSeq findRectangleAndSquares(IplImage img, CvMemStorage storage) {
        int i, c, l, N = 33;
        CvSize sz = cvSize(img.width() & -2, img.height() & -2);
        IplImage timg = cvCloneImage(img);
        IplImage gray = cvCreateImage(sz, 8, 1);
        IplImage pyr = cvCreateImage(cvSize(sz.width()/2, sz.height()/2), 8, 3);
        IplImage tgray = null;

        CvSeq squares = cvCreateSeq(0, Loader.sizeof(CvSeq.class), Loader.sizeof(CvPoint.class), storage);

        cvSetImageROI(timg, cvRect(0, 0, sz.width(), sz.height()));

        cvPyrDown(timg, pyr, 7);
        cvPyrUp(pyr, timg, 7);
        tgray = cvCreateImage(sz, 8, 1);

        for (c = 0; c < 3; c++) {
            cvSetImageCOI(timg, c+1);
            cvCopy(timg, tgray);

            for (l = 0; l < N; l++) {
                if (l == 0) {
                    cvCanny(tgray, gray, 0, thresh, 7);

                    cvDilate(gray, gray, null, 1);
                } else {
                    cvThreshold(tgray, gray, (l+1)*255/N, 255, CV_THRESH_BINARY);
                }


                CvSeq contours = new CvSeq();
                //CV_CHAIN_APPROX_NONE
                cvFindContours(gray, storage, contours, Loader.sizeof(CvContour.class), CV_RETR_LIST, CV_CHAIN_APPROX_NONE, cvPoint(0,0));
                
                while (contours != null && !contours.isNull()) {
                	//line massivity
                    CvSeq result = cvApproxPoly(contours, Loader.sizeof(CvContour.class), storage, CV_POLY_APPROX_DP, cvContourPerimeter(contours)*0.02, 0);
                    //Area
                    if(result.total() == 4 && (Math.abs(cvContourArea(result, CV_WHOLE_SEQ, 0)) >= areaMin && Math.abs(cvContourArea(result, CV_WHOLE_SEQ, 0)) <= areaMax) && cvCheckContourConvexity(result) != 0) {
                    	double s = 0.0, t = 0.0;

                    	for( i = 0; i < 5; i++ ) {
                           
                            if( i >= 2 ) {
      
                                t = Math.abs(angle(new CvPoint(cvGetSeqElem(result, i)),
                                        new CvPoint(cvGetSeqElem(result, i-2)),
                                        new CvPoint(cvGetSeqElem(result, i-1))));
                                s = s > t ? s : t;
                            }
                        }
                    	//degrees 0.43 90 deg
                    	//0.3 95 deg
                    	
                        if (s < 0.43)
                            for( i = 0; i < 4; i++ ) {
                            	
                                cvSeqPush(squares, cvGetSeqElem(result, i));
                            }
                    }
                    contours = contours.h_next();
                }
            }
        }

        cvReleaseImage(gray);
        cvReleaseImage(pyr);
        cvReleaseImage(tgray);
        cvReleaseImage(timg);

        return squares;
    }

    static void drawSquares(IplImage img, CvSeq squares) {
    	System.out.println("drawSquares()");

        IplImage cpy = cvCloneImage(img);
        int i = 0;

        CvSlice slice = new CvSlice(squares);

         for(i = 0; i < squares.total(); i += 4) {
        	 //get x and y of square 
			Pointer line = cvGetSeqElem(squares, i);
			CvPoint pt  = new CvPoint(line).position(0);            
			  
			CvPoint rect = new CvPoint(4);
			IntPointer count = new IntPointer(1).put(4);
			
			cvCvtSeqToArray(squares, rect, slice.start_index(i).end_index(i + 4));
			
			cvPolyLine(cpy, rect.position(0), count, 1, 1, CV_RGB(0,255,0), 1, CV_AA, 0);
	 		getDeckCoordinates(rect.position(0));

         }
 		cvSaveImage(image_path + "hdship_CPY_reworked.jpg", cpy);

        canvas.showImage(converter.convert(cpy));
        cvReleaseImage(cpy);
    }
    
    
    
	private static void getDeckCoordinates(CvPoint position) {
		String pattern = "([0-9]+, [0-9]+)";
			
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(position.toString());
		List<String> intList = new ArrayList<String>();
		while(m.find()) {
			String match = m.group();
			intList.add(match);
		}
		for (int i = 0; i < intList.size(); i+=4) {
			String[] number1 = intList.get(i).split(",\\s");
			String[] number2 = intList.get(i + 1).split(",\\s");
			String[] number3 = intList.get(i + 2).split(",\\s");
			String[] number4 = intList.get(i + 3).split(",\\s");

			decks.add(new Deck(new Point(Integer.parseInt(number1[0]), Integer.parseInt(number1[1])), new Point(Integer.parseInt(number2[0]), Integer.parseInt(number2[1])), new Point(Integer.parseInt(number3[0]), Integer.parseInt(number3[1])), new Point(Integer.parseInt(number4[0]), Integer.parseInt(number4[1]))));
	  }
	}

	public static void main(String [] args)
	{	
		System.out.println("Proccessed!");

		IplImage originalImage = cvLoadImage(loadImageFromPath);
		IplImage resultImage = IplImage.create(originalImage.width(), originalImage.height(), IPL_DEPTH_8U, 1);
		cvCvtColor(originalImage, resultImage, CV_BGR2GRAY);
		cvAdaptiveThreshold(resultImage, resultImage, 255, CV_ADAPTIVE_THRESH_GAUSSIAN_C, CV_THRESH_BINARY_INV, 7, 7);
		cvSaveImage(image_path + "/hdship_reworked.jpg", resultImage);
		
		
		
		IplImage whiteImage = IplImage.create(originalImage.width(), originalImage.height(), IPL_DEPTH_8U, 1);
		  cvCvtColor(originalImage, whiteImage, CV_RGB2GRAY);
		  cvAdaptiveThreshold(whiteImage, whiteImage, 255, CV_ADAPTIVE_THRESH_GAUSSIAN_C, CV_THRESH_BINARY, 17, 15);
		  
		
		cvSaveImage(image_path + "/" + fileNameWithoutExtention + "_crop.jpg", whiteImage);
  
	    String names[] = new String[]{ image_path + "hdship_reworked.jpg" };
		
		int i;

        storage = cvCreateMemStorage(0);

        for(i = 0; i < names.length; i++) {

            String filePathAndName = names[i];
            filePathAndName = filePathAndName == null || filePathAndName.isEmpty() ? names[i] : filePathAndName;
            img0 = cvLoadImage(filePathAndName, 1);
            if (img0 == null) {
                System.err.println("Couldn't load " + names[i]);
                continue;
            }
            img = cvCloneImage(img0);

            canvas = new CanvasFrame(wndname, 1);
            canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
            drawSquares(img, findRectangleAndSquares(img, storage));
            
            //Do Crop
            doCrop();

            KeyEvent key = null;
            try {
                key = canvas.waitKey(0);
            } catch(InterruptedException ie) {
            }

            cvReleaseImage(img);
            cvReleaseImage(img0);
            cvClearMemStorage(storage);

            if (key.getKeyCode() == 27) {
                break;
            }
        }
        if (canvas != null) {
            canvas.dispose();
        }
	}
	
	private static void doCrop(){
		System.out.println("Screenshots count = " + decks.size());
		
		for (int i = 0; i < decks.size(); i++) {
			int rect_x = decks.get(i).getP1().x;
			int rect_y = decks.get(i).getP1().y;
			int width = decks.get(i).getP2().x - rect_x;
			
			if (width < 0) {
				width *= -1;
			}else if(width == 0){
				width = 1;
			}

			int height = decks.get(i).getP4().y - rect_y;
			if (height < 0) {
				height *= -1;
			}else if(height == 0) {
				height = 1;
			}
			
			//Crop Image
			Rectangle rect = new Rectangle(rect_x, rect_y, width, height);
			ImageCrop.cropImage(loadImageForCropping, rect, "C:\\Users\\Darko\\Desktop\\cropedImages\\" + "croped");
			
			if (decks.size() - 1 == i) {
				for (int j = 0; j < decks.size(); j++) {
					MyTesseract.doOCR("C:\\Users\\Darko\\Desktop\\cropedImages\\", "croped_" + j + ".tif", first_digit);
				}
			}
		}
		GenerateOutout.run(decks, MyTesseract.numbers, csvPath);
		System.out.println("Process done!");
	}

}


