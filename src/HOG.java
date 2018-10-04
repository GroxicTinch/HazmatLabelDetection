import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/*
 * Adapted from "Mallick, S. 2016. Histogram of Oriented Gradients. https://www.learnopencv.com/histogram-of-oriented-gradients/"
 */
public class HOG {
  private static int _cellSize = 8;
  private static int _cellWidth;
  private static int _cellHeight;
  
  public static double calcEuclideanDist(double[] hog1, double[] hog2) {
    double total = 0;
    
    if(hog1.length != hog2.length) {
      return -1;
    }
    
    for(int i = 0; i < hog1.length; i++) {
      total += Math.pow((hog1[i] - hog2[i]), 2);
    }
    
    return total;
  }
  
  // Resizes the image to a certain size before calculating HOG so try and lower chance of
  // Odd fonts messing it up
  public static double[] createFixedSize(Mat mat, int newWidth, int newHeight) {
    double[] array;
    double whRatio = (double)mat.width() / (double)mat.height();
    mat = Filter.resizeToPixel(mat, newWidth, newHeight);
    array = create(mat);
    
    array[array.length-1] = whRatio;
    return array;
  }
  
  public static double[] create(Mat mat) {
    double[][][] cellBin;
    Mat gradXMat = new Mat();
    Mat gradYMat = new Mat();
    
    Mat mag = new Mat();
    Mat angle = new Mat();
    
    _cellWidth = (int) Math.floor(mat.cols() / _cellSize);
    _cellHeight = (int) Math.floor(mat.rows() / _cellSize);

    Imgproc.Sobel(mat, gradXMat, CvType.CV_32F, 1, 0);
    Imgproc.Sobel(mat, gradYMat, CvType.CV_32F, 0, 1);
    
    Core.cartToPolar(gradXMat, gradYMat, mag, angle, true);

    cellBin = convertToAngleBin(mag, angle);
    
    return generateDescriptor(cellBin);
  }

  private static double[][][] binNormalize(double norm, int cellRow, int cellCol, double[][][] cellBin) {
    for(int i = 0; i < cellBin[cellRow][cellCol].length; i++) {
      if(norm != 0) {
        cellBin[cellRow][cellCol][i] /= norm;
      }
    }
    
    return cellBin;
  }
  
  private static double[][][] blockNormalize(int cellRow, int cellCol, double[][][] cellBin) {
    double norm = 0;
    
    // We want the current cell, and the cells 1 to the left, 1 above and the cell up to the left.
    for(int i = 0; i <= 1; i++) {
      for(int ii = 0; ii <= 1; ii++) {
        norm += sumOfBinsSqred(cellBin[cellRow-i][cellCol-ii]);
      }
    }
    
    norm = Math.sqrt(norm);
    
    // The top left of the 2x2 is the only cell that wont be looked at for normalization again
    // so it is safe to normalize it
    
    cellBin = binNormalize(norm, cellRow-1, cellCol-1, cellBin);
        
    // If we are working on the far right then the top right wont be looked at for normalization again
    if(cellCol == _cellWidth-1) {
      cellBin = binNormalize(norm, cellRow-1, cellCol, cellBin);
    }
    
    // If we are working on the very bottom then the left wont be looked at for normalization again
    if(cellRow == _cellHeight-1) {
      cellBin = binNormalize(norm, cellRow, cellCol-1, cellBin);
    }
    
    // If we are on the last cell we need to normalize it
    if(cellCol == _cellWidth-1 && cellRow == _cellHeight-1) {
      cellBin = binNormalize(norm, cellRow, cellCol, cellBin);
    }
    
    return cellBin;
  }
  
  private static double[][][] convertToAngleBin(Mat mag, Mat angle) {
    // Bins should be 9, bin 0 = angle 0 to 19, bin 1 angle 20 to 39 etc
    double[][][] cellBin = new double[_cellHeight][_cellWidth][9];
    
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
            
            cellBin[cellRow][cellCol][binNum] += currMag * (((20 - diffToBin) * 5) / 100);
            cellBin[cellRow][cellCol][(binNum + 1) % 9] += currMag * ((diffToBin * 5) / 100);
          }
        }
        
        // Once we have filled in the cellBin for this block, check to see if it is not the top or leftmost row
        // If it is neither then we do a block normalization
        if(cellRow != 0 && cellCol != 0) {
          cellBin = blockNormalize(cellRow, cellCol, cellBin);
        }
      }
    }
    
    return cellBin;
  }
  
  private static double[] generateDescriptor(double[][][] cellBin) {
    if(cellBin.length == 0 || cellBin[0].length == 0) {
      return new double[0];
    }
    int binCount = cellBin[0][0].length;
    
    int currIndex = 0;
    
    // Last will be empty unless manually written to, using for an extra ratio distance
    double[] descriptor = new double[_cellWidth * _cellHeight * binCount + 2];
    
    for( int cellRow = 0; cellRow < _cellHeight; cellRow++){
      for( int cellCol = 0; cellCol < _cellWidth; cellCol++){
        double[] currCellBins = cellBin[cellRow][cellCol];
        
        for( int bin = 0; bin < binCount; bin++) {
          currIndex++;
          //int newCol = (cellCol * binCount) + bin;
          descriptor[currIndex] = currCellBins[bin];
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
