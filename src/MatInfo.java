import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.imgproc.Imgproc;

/*
 * Used for functions that get information about the Mat without altering it
 */
public class MatInfo { 
  public static String getMainColor(Mat img) {
    Mat mask = new Mat();
    return getMainColor(img, mask);
  }
  
  /*
   * Adapted from "Hoy, D. 2018. Practical 2: Image processing. https://lms.curtin.edu.au/bbcswebdav/pid-6105312-dt-content-rid-31739546_1/courses/2018_2_COMP3007_V1_L1_A1_INT_642633/02_image_processing.pdf"
   */
  public static String getMainColor(Mat img, Mat mask) {
    int saturationThreshold = 51; // approx 20%
    int brightnessThreshold = 76; // approx 30%
    
    // Hue goes from 0-180, if we allow 5 for each group it should give us an accurate enough color guess
    // 180 / 5 = 36
    int binsH = 36;
    int binsSV = 255;
    
    String mainColor = "";
    Mat hsvImg = new Mat();
    Mat histH = new Mat();
    Mat histS = new Mat();
    Mat histV = new Mat();
    
    MatOfInt histSizeH = new MatOfInt(binsH+1);
    MatOfInt histSizeSV = new MatOfInt(binsSV+1);
    
    MatOfFloat rangesH = new MatOfFloat(0,180);
    MatOfFloat rangesSV = new MatOfFloat(0,256);
    
    int maxBinH = -1;
    double maxH = 0;
    int maxBinS = -1;
    double maxS = 0;
    int maxBinV = -1;
    double maxV = 0;
    
    // Was not able to find a way to convert straight from Gray to HSV(because H wouldn't exist)
    if(img.channels() == 1) {
      Imgproc.cvtColor(img, hsvImg, Imgproc.COLOR_GRAY2BGR);
      Imgproc.cvtColor(hsvImg, hsvImg, Imgproc.COLOR_BGR2HSV);
    } else {
      Imgproc.cvtColor(img, hsvImg, Imgproc.COLOR_BGR2HSV);
    }
    
    // We need to split the mat into the different colour channels BGR 
    List<Mat> hsv_planes = new ArrayList<Mat>();
    Core.split(hsvImg, hsv_planes);

    // Place the Hue, Saturation and Value into bins
    Imgproc.calcHist(hsv_planes, new MatOfInt(0), mask, histH, histSizeH, rangesH);
    Imgproc.calcHist(hsv_planes, new MatOfInt(1), mask, histS, histSizeSV, rangesSV);
    Imgproc.calcHist(hsv_planes, new MatOfInt(2), mask, histV, histSizeSV, rangesSV);
    
    // Find the most used Hue range
    for(int i = 0; i < binsH; i++) {
      double countH = histH.get(i, 0)[0];
      
      if(countH > maxH) {
        maxBinH = i;
        maxH = countH;
      }
    }
    
    // Find the most used Saturation and Value, can be done together due to same bin sizes
    for(int i = 0; i <= 255; i++) {
      double countS = histS.get(i, 0)[0];
      double countV = histV.get(i, 0)[0];
      
      if(countS > maxS) {
        maxBinS = i;
        maxS = countS;
      }
      if(countV > maxV) {
        maxBinV = i;
        maxV = countV;
      }
    }
    
    if(maxBinV > brightnessThreshold) {
      // It is probably not black
      
      if(maxBinS > saturationThreshold) {
        // It is probably a color
        
        if(maxBinH <= 1 || maxBinH >= 32) {
          // H between 0-10 + 175-180
          mainColor = "Red";
        } else if(maxBinH >= 2 && maxBinH <= 3) {
          // H between 10-25
          mainColor = "Orange" ;
        } else if(maxBinH >= 4 && maxBinH <= 6) {
          // H between 25-35
          mainColor = "Yellow";
        } else if(maxBinH >= 7 && maxBinH <= 16) {
          // H between 35-85
          mainColor = "Green";
        } else if(maxBinH >= 17 && maxBinH <= 27) {
          // H between 85-140
          mainColor = "Blue";
        } else if(maxBinH >= 28 && maxBinH <= 31) {
          // H between 140-160
          mainColor = "Pink"; // This shouldn't happen in the assignment but thought I would add it anyway
        }
      } else {
        // It is probably white
        mainColor = "White";
      }
    } else {
      // It is probably black
      mainColor = "Black";
    }
    
    return mainColor;
  }
  
  public static MatchResult templateMatch(Mat mat, Mat templ) {
    // Imgproc.TM_CCOEFF
    // Imgproc.TM_CCOEFF_NORMED
    // Imgproc.TM_CCORR
    // Imgproc.TM_CCORR_NORMED
    // Imgproc.TM_SQDIFF
    // Imgproc.TM_SQDIFF_NORMED
    return templateMatch(mat, templ, Imgproc.TM_SQDIFF_NORMED);
  }
  
  public static MatchResult templateMatch(Mat mat, Mat templ, int matchMethod) {
    MatchResult tmr = new MatchResult();
    Mat result = new Mat();
    Double percent;
    
    Imgproc.matchTemplate(mat, templ, result, matchMethod);
    MinMaxLocResult mmlr = Core.minMaxLoc(result);
    
    if(matchMethod == Imgproc.TM_SQDIFF || matchMethod == Imgproc.TM_SQDIFF_NORMED) {
      percent = 1-mmlr.minVal;
    } else {
      percent = mmlr.maxVal;
    }
    
    tmr.setResult(percent);

    return tmr;
  }
}
