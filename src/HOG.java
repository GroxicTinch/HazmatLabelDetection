import org.opencv.core.Mat;

public class HOG {
  private static int cellSize = 9;
  
  public static Mat create(Mat img) {
    Mat gradientMat = new Mat();
    Mat colorMat = Filter.gaussian(img, 20);
    
    int cellWidth = (int) Math.floor(img.cols() / cellSize);
    int cellHeight = (int) Math.floor(img.rows() / cellSize);
    
    
    
    return colorMat;
  }
}
