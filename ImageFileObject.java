import java.io.File;
import java.io.IOException;

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
        case "bmp":
        case "jp2":
        case "jpe":
        case "jpeg":
        case "jpg":
        case "pbm":
        case "pgm":
        case "png":
        case "ppm":
        case "ras":
        case "sr":
        case "tiff":
        case "tif":
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

  // Getters
  public String getName() { return _name;}
  public String getFilename() { return _filename;}
  public String getFullPath() { return _fullPath;}
  public String getFileExt() { return _fileExt;}

  public boolean isImage() { return _isImage; }

  // Methods
  public void saveAs(String name) throws MPException {
    saveAs(name, _fileExt);
  }

  // toString
  public String toString() {
    return "\nFilename: " + _filename
          + "\nwidth: " + this.getImg().width()
          + "\nheight: " + this.getImg().height();
  }
}