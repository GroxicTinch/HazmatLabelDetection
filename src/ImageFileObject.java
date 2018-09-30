import java.io.File;
import java.io.IOException;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class ImageFileObject extends ImageObject {
  private String _name;
  private String _filename;
  private String _fullPath;
  private String _fileExt;

  private boolean _isImage;

  // Constructors
  public ImageFileObject(File file) throws IOException {
    super(Imgcodecs.imread(file.getCanonicalPath()));
    
    int dotIndex;

    _filename = file.getName();
    _fullPath = file.getCanonicalPath();
   
    dotIndex = _filename.lastIndexOf(".");
    
    if (dotIndex >= -1) {
      _name = _filename.substring(0,dotIndex);
      _fileExt = _filename.substring(dotIndex+1);

      switch(_fileExt) {
        //case "bmp":
        //case "jp2":
        //case "jpe":
        //case "jpeg":
        case "jpg":
        //case "pbm":
        //case "pgm":
        case "png":
        //case "ppm":
        //case "ras":
        //case "sr":
        //case "tiff":
        //case "tif":
          _isImage = true;
          break;
        default:
          _isImage = false;
          break;
      }
    } else {
      _name = _filename;
      _fileExt = "";
      _isImage = false;
    }
  }
  
  public ImageFileObject(Mat img, String name, String filename, String fullPath, String fileExt, boolean isImage) {
    super(img);
    
    _name = name;
    _filename = filename;
    _fullPath = fullPath;
    _fileExt = fileExt;
    _isImage = isImage;
  }

  // Getters
  public String getName() { return _name;}
  public String getFilename() { return _filename;}
  public String getFullPath() { return _fullPath;}
  public String getFileExt() { return _fileExt;}

  public boolean isImage() { return _isImage; }

  // Methods

  // toString
  public String toString() {
    return "\nFilename: " + _filename
          + "\nwidth: " + this.getMat().width()
          + "\nheight: " + this.getMat().height();
  }
  
  public ImageFileObject copy() {
    Mat copyMat = new Mat();
    getMat().copyTo(copyMat);
    
    ImageFileObject copy = new ImageFileObject(copyMat, _name, _filename, _fullPath, _fileExt, _isImage);
    copy.setBounds(this.getBoundsCopy());
    return copy;
  }
}