import java.io.File;
import java.io.IOException;

import org.opencv.imgcodecs.Imgcodecs;

public class ImageFileObject extends ImageObject {
  String _filename;
  String _fullPath;
  String _fileExt;

  boolean _isImage;

  // Constructors
  public ImageFileObject(File file) throws IOException {
    super(Imgcodecs.imread(file.getCanonicalPath()));
    
    int dotIndex;

    _filename = file.getName();
    _fullPath = file.getCanonicalPath();
   
    dotIndex = _filename.lastIndexOf(".");
    if (dotIndex >= -1) {
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
      _fileExt = "";
      _isImage = false;
    }
  }

  // Getters
  public String getFilename() { return _filename;}
  public String getFullPath() { return _fullPath;}
  public String getFileExt() { return _fileExt;}

  public boolean isImage() { return _isImage; }

  // Methods
  public void saveAs(String name) {
    saveAs(name, _fileExt);
  }

  public void saveAs(String name, String ext) {
    String newName = name;

    if(ext != "") {
      newName += "." + ext;
    }

    Imgcodecs.imwrite(newName, _img);
  }

  // toString
  public String toString() {
    return "Filename: " + _filename
          + "\nwidth: " + _img.width()
          + "\nheight: " + _img.height() + "\n";
  }
}