import java.util.Collection;
import java.util.HashMap;
import java.util.TreeSet;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;

/*
 * Used to generate blobs using connected components
 */
public class ConnectedComponents {
  private HashMap<Integer, ConnectedComponentsBlob> _connList;
  private Mat _mat;
  private Mat _blobMat;
  private TreeSet<Integer> _lowestLabelQueue;
  
  public ConnectedComponents(Mat mat) {
    _mat = mat;
  }
  
  public ConnectedComponents(Mat mat, Mat mask) {
    _mat = Mat.zeros(mat.size(), CvType.CV_8U);
    mat.copyTo(_mat, mask);
  }
  
  private void addPixel(Point pos, int label) {
    ConnectedComponentsBlob posSet = _connList.getOrDefault(label, new ConnectedComponentsBlob(_mat));
    
    posSet.add(pos);
    
    // Add pixel to the blobMat
    _blobMat.put((int)pos.y, (int)pos.x, label);
    
    _connList.put(label, posSet);
  }
  
  public ConnectedComponentsBlob[] getBlobs() {
    Collection<ConnectedComponentsBlob> values = _connList.values();
    
    while (values.remove(null)) {
      // Do nothing but remove all the null values
    }
    
    return values.toArray(new ConnectedComponentsBlob[values.size()]);
  }

  private int merge(int currLabel, int foundLabel) {
    int smallestLabel;
    int largerLabel;
    
    ConnectedComponentsBlob currPosSet = _connList.getOrDefault(currLabel, new ConnectedComponentsBlob(_mat));
    ConnectedComponentsBlob foundPosSet = _connList.getOrDefault(foundLabel, new ConnectedComponentsBlob(_mat));
    
    if(foundLabel < currLabel) {
      smallestLabel = foundLabel;
      largerLabel = currLabel;
    } else {
      smallestLabel = currLabel;
      largerLabel = foundLabel;
    }
    
    // See which list of points is the largest then add the smaller list to it.
    if(foundPosSet.size() >= currPosSet.size()) {
      for(Point p : currPosSet.getPixels()) {
        foundPosSet.add(p);
        _blobMat.put((int)p.y, (int)p.x, smallestLabel);
      }
      _connList.put(smallestLabel, foundPosSet);
    } else {
      for(Point p : foundPosSet.getPixels()) {
        currPosSet.add(p);
        _blobMat.put((int)p.y, (int)p.x, smallestLabel);
      }
      _connList.put(smallestLabel, currPosSet);
    }
    
    _lowestLabelQueue.add(largerLabel);
    _connList.remove(largerLabel);
    
    return smallestLabel;
  }
  
  
  /**
   * Uses 1 as the threshold, meaning if the pixel is not black
   */
  public Mat generate() {
    return generate(1);
  }
  
  /**
   * @param threshold at what value should a pixel be considered
   */
  public Mat generate(int threshold) {
    int latestLabel = 1;
    
    _connList = new HashMap<Integer, ConnectedComponentsBlob>();
    _lowestLabelQueue = new TreeSet<Integer>();
    
    _blobMat = Mat.zeros(_mat.size(), CvType.CV_16U);
    
    for(int row = 0; row < _mat.rows(); row++) {
      for(int col = 0; col < _mat.cols(); col++) {
        boolean connectionFound = false;
        
        int currLabel;
        int foundLabel = 0;
        
        if(_lowestLabelQueue.isEmpty()) {
          currLabel = latestLabel;
        } else {
          currLabel = _lowestLabelQueue.pollFirst();
        }
        
        // Threshold so that if watershed is implemented then it can be used(Spoiler; it wasn't)
        if(_mat.get(row, col)[0] >= threshold) {
          // For each 8-way connectivity pixel
          // Top row
          if(row > 0) {
            int leftMostCol = (col == 0) ? 0 : -1;  // if on first column then don't check col-1 for connected
            int rightMostCol = (col == _mat.cols()-1) ? 0 : 1;
            
            for(int conCol = leftMostCol; conCol <= rightMostCol; conCol++) {
              foundLabel = (int) _blobMat.get(row-1, col + conCol)[0];
              
              if(foundLabel > 0 && foundLabel != currLabel) {
                connectionFound = true;
                
                // Combine lists using the lower label number, This pixel is also connected to another blob, so use this label for now
                currLabel = merge(currLabel, foundLabel);   
              }
            }
          }
          
          // Left of pixel
          if(col > 0) {
            foundLabel = (int) _blobMat.get(row, col-1)[0];
            
            if(foundLabel > 0 && foundLabel != currLabel) {
              connectionFound = true;
              
              // Combine lists using the lower label number, This pixel is also connected to another blob, so use this label for now
              currLabel = merge(currLabel, foundLabel);  
            }
          }
          
          addPixel(new Point(col, row), currLabel);
          
          if(!connectionFound) {
            latestLabel++;
          }
        }
      }
    }
    
    return _blobMat;
  }
}
