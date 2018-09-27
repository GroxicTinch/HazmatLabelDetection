import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

// https://www.learnopencv.com/histogram-of-oriented-gradients/
public class HOG {
  private static int _cellSize = 8;
  private static int _cellWidth;
  private static int _cellHeight;
  
  // I know global variables are frowned upon but going a few functions deep
  // passing it along every time will use a lot of memory
  private static double[][][] _cellBin;
  
  public static Mat create(Mat img) {
    Mat gradXMat = new Mat();
    Mat gradYMat = new Mat();
    
    Mat grayMat = new Mat();
    Mat colorMat = Filter.gaussian(img, 12, 3); // Not interested in any details but average colour
    
    Mat mag = new Mat();
    Mat angle = new Mat();
    
    _cellWidth = (int) Math.floor(img.cols() / _cellSize);
    _cellHeight = (int) Math.floor(img.rows() / _cellSize);

    // Create gradients
    Imgproc.cvtColor(img, grayMat, Imgproc.COLOR_BGR2GRAY);
    
    // [FIXME] Need to fix my function
    /*gradXMat = Filter.gradientX(grayMat);
    gradYMat = Filter.gradientY(grayMat);*/

    Imgproc.Sobel(img, gradXMat, CvType.CV_32F, 1, 0);
    Imgproc.Sobel(img, gradYMat, CvType.CV_32F, 0, 1);
    
    Core.cartToPolar(gradXMat, gradYMat, mag, angle, true);

    _cellBin = convertToAngleBin(mag, angle /*, _cellBin */);
    
    return generateDescriptor(/* _cellBin */);
  }

  private static void binNormalize(double norm, int cellRow, int cellCol/*, _cellBin */) {
    for(int i = 0; i < _cellBin[cellRow][cellCol].length; i++) {
      if(norm != 0) {
        _cellBin[cellRow][cellCol][i] /= norm;
      }
    }
  }
  
  private static void blockNormalize(int cellRow, int cellCol/*, _cellBin */) {
    double norm = 0;
    
    // We want the current cell, and the cells 1 to the left, 1 above and the cell up to the left.
    for(int i = 0; i <= 1; i++) {
      for(int ii = 0; ii <= 1; ii++) {
        norm += sumOfBinsSqred(_cellBin[cellRow-i][cellCol-ii]);
      }
    }
    
    norm = Math.sqrt(norm);
    
    // The top left of the 2x2 is the only cell that wont be looked at for normalization again
    // so it is safe to normalize it
    
    binNormalize(norm, cellRow-1, cellCol-1);
        
    // If we are working on the far right then the top right wont be looked at for normalization again
    if(cellCol == _cellWidth-1) {
      binNormalize(norm, cellRow-1, cellCol);
    }
    
    // If we are working on the very bottom then the left wont be looked at for normalization again
    if(cellRow == _cellHeight-1) {
      binNormalize(norm, cellRow, cellCol-1);
    }
    
    // If we are on the last cell we need to normalize it
    if(cellCol == _cellWidth-1 && cellRow == _cellHeight-1) {
      binNormalize(norm, cellRow, cellCol);
    }
  }
  
  private static double[][][] convertToAngleBin(Mat mag, Mat angle/*, _cellBin */) {
    // Bins should be 9, bin 0 = angle 0 to 19, bin 1 angle 20 to 39 etc
    _cellBin = new double[_cellHeight][_cellWidth][9];
    
    // Loop through each cell in the image
    for( int cellRow = 0; cellRow < _cellHeight; cellRow++){
      for( int cellCol = 0; cellCol < _cellWidth; cellCol++){
        int cellPixelRowStart = cellRow * (_cellSize);
        int cellPixelColStart = cellCol * (_cellSize);
        
        int cellPixelRowEnd = cellPixelRowStart + 8;
        int cellPixelColEnd = cellPixelColStart + 8;
        
        // Loop through each pixel in the cell
        for( int row = cellPixelRowStart; row < cellPixelRowEnd; row++){
          for( int col = cellPixelColStart; col < cellPixelColEnd; col++){
            double currAngle = angle.get(row, col)[0];
            double currMag = mag.get(row, col)[0];
            
            double diffToBin = (int) (currAngle % 20);
            
            int binNum = (int) (currAngle - diffToBin) / 20;
            binNum %= 9; // We want 180(which would end up bin 9) to be in bin 0
            
            _cellBin[cellRow][cellCol][binNum] += currMag * (((20 - diffToBin) * 5) / 100);
            _cellBin[cellRow][cellCol][(binNum + 1) % 9] += currMag * ((diffToBin * 5) / 100);
          }
        }
        
        // Once we have filled in the cellBin for this block, check to see if it is not the top or leftmost row
        // If it is neither then we do a block normalization
        if(cellRow != 0 && cellCol != 0) {
          blockNormalize(cellRow, cellCol/*, _cellBin */);
        }
      }
    }
    
    return _cellBin;
  }
  
  private static Mat generateDescriptor(/* cellBin */) {
    int binCount = _cellBin[0][0].length;
    
    int currIndex = 0;
    Mat descriptor = Mat.zeros(1, _cellWidth * _cellHeight * binCount, CvType.CV_32F);
    
    for( int cellRow = 0; cellRow < _cellHeight; cellRow++){
      for( int cellCol = 0; cellCol < _cellWidth; cellCol++){
        double[] currCellBins = _cellBin[cellRow][cellCol];
        
        for( int bin = 0; bin < binCount; bin++) {
          currIndex++;
          //int newCol = (cellCol * binCount) + bin;
          descriptor.put(0, currIndex, currCellBins[bin]);
        }
      }
    }
    
    return descriptor;
  }
  
  private static double sumOfBinsSqred(double[] cellBinBinsOnly) {
    double sum = 0;
    
    for(int i = 0; i < cellBinBinsOnly.length; i++) {
      sum += Math.pow(cellBinBinsOnly[i], 2);
    }
    
    return sum;
  }
}
