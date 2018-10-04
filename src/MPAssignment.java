import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author David Hoy
 */

public class MPAssignment {  
  static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
  
  static ArrayList<JFrame> _frameList = new ArrayList<JFrame>();

  public static void main(String args[]) {
  	boolean foundImage = false;
    if(args.length == 0) {
      args = new String[1];
      args[0] = ".";
    }
    
    for(String arg : args) {
      File dir = new File(arg);
      File[] dirList = dir.listFiles();

      if(dir.isDirectory()) { 
        if(dirList != null && dirList.length > 0 ) {
          Arrays.sort(dirList);
          for(File file : dirList) {
            try {
              if(processFile(file)) {
              	foundImage = true;	// Check to see if any images have been found at all
              }
            } catch (IOException e) {
              System.out.println("Skipping file due to issue opening: " + file.getName());
            } catch (MPException e) {
              System.out.println(e.toString());
            }
          }
        }
        
        if(!foundImage) {
          System.out.println("No images found in given directory:\n" + args[0]);
        }
        
      } else if(dir.isFile()){
        try {
          processFile(dir);
        } catch (IOException e) {
          System.out.println("Skipping file due to issue opening: " + dir.getName());
        } catch (MPException e) {
          System.out.println(e.toString());
        }
      } else {
        System.out.println("The directory " + dir.toString() + " does not exist");
      }
    }
  }
  
  static boolean processFile(File file) throws IOException, MPException {
    ImageFileObject imgFO = new ImageFileObject(file);
    
    // If the file is not an image we do not want to try and process it further
    if(!imgFO.isImage()) {
    	return false;
    }
    
    OutputObject outputObj = new OutputObject(imgFO.getFilename());
    
	  /* 
	   * [TODO] Create different way to create masks *********
	   */
	  Mat mask = Imgcodecs.imread("./Data/Masks/Mask.png");
    Imgproc.cvtColor(mask, mask, Imgproc.COLOR_BGR2GRAY);
    //******************************************************

    TreeSet<CharAndLoc> classNumList = new TreeSet<CharAndLoc>();
    TreeSet<CharAndLoc> textList = new TreeSet<CharAndLoc>();
    MatchResult symbolTmr = new MatchResult();
    
    ConnectedComponentsBlob[] connBlob = findBlobs(imgFO.getMat(), mask);
    
    int signH = imgFO.getHeight();
    int signW = imgFO.getWidth();
	  
	  for(int i = 0; i < connBlob.length; i++) {	    
	    if(connBlob[i].getWidth() < 5 && connBlob[i].getHeight() < 5) {
	      // It is most likely garbage, discard
	      continue;
	    }
	    
	    /* [Notes]
	     * The following are for all occurrences in the sample data(and hopefully all test data)
	     * Class Number is located 0.70:350 from top of the sign and ends at 0.90:450, 0.38:190 from left start til 0.63:315
	     * Text T 0.38:190, B 0.66:330, L 0.12:60, R 0.89:445
	     * Symbol is located at top 0.04:20, bottom 0.49:245, left 0.21:105, right 0.78:390
	     * 
	     * stretching boxes to try and prevent dropped blobs, shouldn't really need to clip sides
	     */
      
      int classNumTop = (int) (signH * 0.70);
      int classNumBottom = (int) (signH);
      
      int classTextTop = (int) (signH * 0.38);
      int classTextBottom = (int) (signH * 0.67);
      
      int classSymbolTop = (int) (0);
      int classSymbolBottom = (int) (signH * 0.49);
	    
	    Rect classNumLoc = new Rect(new Point(0, classNumTop), new Point(signW, classNumBottom));
      Rect classTextLoc = new Rect(new Point(0, classTextTop), new Point(signW, classTextBottom));
      Rect classSymbolLoc = new Rect(new Point(0, classSymbolTop), new Point(signW, classSymbolBottom));

	    if(classNumLoc.contains(connBlob[i].tl())) {
	      // It is most likely the class identification number
        String foundClassNum = findNumbers(connBlob[i]);
        if(!foundClassNum.equals("Unknown")) {
          if(foundClassNum.equals("dot")) {
            foundClassNum = ".";
          }
          classNumList.add(new CharAndLoc(foundClassNum.charAt(0), connBlob[i].tl(), connBlob[i].br()));
        }
	    } else if(classTextLoc.contains(connBlob[i].tl()) && classTextLoc.contains(connBlob[i].br())) {       
	      // It is most likely the descriptive text
        String foundText = findCharacters(connBlob[i]);
        if(!foundText.equals("Unknown")) {
          if(foundText.equals("dot")) {
            foundText = ".";
          }
          textList.add(new CharAndLoc(foundText.charAt(0), connBlob[i].tl(), connBlob[i].br()));
        }
	    } else if(classSymbolLoc.contains(connBlob[i].tl()) && classSymbolLoc.contains(connBlob[i].br())) {
	      // It is most likely the class symbol
	      MatchResult symbolTmrTmp = new MatchResult();
        
        // If we have a good result then ignore the rest of the templates
        if(symbolTmr.getResult() <= 0.9) {
          symbolTmrTmp = findSymbol(connBlob[i].getMat());
          
          if(symbolTmr.getResult() < symbolTmrTmp.getResult()) {
            symbolTmr = symbolTmrTmp;
            
            outputObj.symbol = symbolTmr.getName();
          }
        }
	    }
	  }
	  
	  /* Get Class number */
	  outputObj.classNum = convertCharAndLocListToString(classNumList);
	  
	  /* Get descriptive text */
	  String foundText = convertCharAndLocListToString(textList);
	  
	  if(foundText == "(none)") {
	    outputObj.otherText = foundText;
	  } else {	    
	    outputObj.otherText = ExpectedTextMatcher.match(foundText);
	  }
	  
	  /* Get Colors */
	  findSignHalfColors(imgFO.getMat(), mask, outputObj);
	  String output = outputObj.toString() + "\n";
	  System.out.println(output);

	  /* Debugging code, ideally you should not be seeing this */
	  // Automatically check if output is correct, if it isn't then pause
	  BufferedReader br = new BufferedReader(new FileReader("SampleData/expectedResults/" + imgFO.getFilename() + ".txt"));
	  
	  String[] outputStrings = output.split("\n");
	  
	  boolean shouldPause = false;
	  int i = 0;
	  String line;
	  while((line = br.readLine()) != null) {
	    if(i==0) {
	      // We dont care about filename, that should be the same anyway
	      i++;
	      continue;
	    }
	    
	    if(!line.equals(outputStrings[i])) {
	      // Not sure if the actual number should be printed or the phrase "A number" so assuming the easiest of the 2
	      if(outputStrings[i].endsWith("A number")) {
	        System.out.println("Assume its okay: \"" + outputStrings[i] + "\" != \"" + line + "\"\n\n");
	      } else if(!outputStrings[i].endsWith("Not Implemented")  && !outputStrings[i].equals(line)) {
	        System.out.println("MISMATCH:    \"" + outputStrings[i] + "\" != \"" + line + "\"  found: " + foundText + "\n\n");

	        shouldPause = true;
	      }
	    }

	    i++;
	  }
	  
	  if(shouldPause) {
	    winShow("",imgFO.getMat());
	    winWait();
	  }
	  
	  return true;
  }
  
  /*
   * [TODO] fix issue: "Dangerous When Wet" sign finds "Dangerousw wet"
   */
  private static String convertCharAndLocListToString(TreeSet<CharAndLoc> charLocList) {
    String returnString;
    
    if(charLocList.isEmpty()) {
      returnString = "(none)";
    } else {
      String tmpString = "";
      
      CharAndLoc prevCharAndLoc = null;
      for (CharAndLoc charAndLoc : charLocList) {
        if(prevCharAndLoc != null) {
          double rightSideOfPrevChar = prevCharAndLoc.getLoc2().x;
          double bottomSideOfPrevChar = prevCharAndLoc.getLoc2().y;
          
          if(charAndLoc.getLoc().y > bottomSideOfPrevChar) {
            // Character does not overlap, solves issue of finding a . inside of chars
            tmpString += " " + charAndLoc.getChar();
          } else if(charAndLoc.getLoc().x >= rightSideOfPrevChar) {
            tmpString += charAndLoc.getChar();
          }
        } else {
          tmpString += charAndLoc.getChar();
        }
        
        prevCharAndLoc = charAndLoc;
      }
      
      returnString = tmpString;
    }
    
    return returnString;
  }

  private static String findCharacters(ConnectedComponentsBlob connBlob) {
    MatchResult mr = new MatchResult("Unknown");
    mr.setResult(1.0);
    double[] hogArray = HOG.createFixedSize(connBlob.getMat(), 30, 30);
    
    File dir = new File("./Data/HOGs/Characters");
    File[] dirList = dir.listFiles();

    if(dir.isDirectory()) { 
      if(dirList != null && dirList.length > 0 ) {
        for(File file : dirList) {
          try {
            if(file.isDirectory()) {
              continue;
            }
            String filename = file.getName();
            
            double[] charHogArray = Utils.loadDoubleArray("./Data/HOGs/Characters/" + filename);
            double distance = HOG.calcEuclideanDist(hogArray, charHogArray);

            if(mr.getResult() > distance) {
              int indexOf = filename.indexOf('_');
              int indexOfDot = filename.lastIndexOf('.');
              
              if(indexOf > -1) {
                filename = filename.substring(0, indexOf);
              } else if(indexOfDot > -1) {
                filename = filename.substring(0, indexOfDot);
              }
              
              mr.setName(filename);
              mr.setResult(distance);
            }
          } catch (MPException e) {
            e.printStackTrace();
          }
        }
      }
    }
    
    return mr.getName();
  }

  /*
   * [TODO] fix character recognition
   */
  private static String findNumbers(ConnectedComponentsBlob connBlob) {
    MatchResult finalTmr = new MatchResult("Unknown");
    Mat mat = new Mat();
    connBlob.getMat().copyTo(mat);
    
    // match must be at least 50% to count
    finalTmr.setResult(0.5);
    
    boolean gotGoodResult = false;
    
    File dir = new File("./Data/NumTemplates");
    File[] dirList = dir.listFiles();

    if(dir.isDirectory()) { 
      if(dirList != null && dirList.length > 0 ) {
        for(File file : dirList) {
          try {
            if(gotGoodResult) {
              break;
            }
            ImageFileObject templ = new ImageFileObject(file, Imgcodecs.IMREAD_GRAYSCALE);
            MatchResult tmr = new MatchResult();
            
            tmr.setResult(0);
            
            Mat templMat;
            templMat = templ.getMat();
            
            double templWidthToHeightRatio = (double)templ.getWidth() / (double)templ.getHeight();
            double matWidthToHeightRatio = (double)mat.width() / (double)mat.height();
            
            double ratioDiff = Math.abs(templWidthToHeightRatio - matWidthToHeightRatio);
            
            if(ratioDiff < 0.05) {
              mat = Filter.resizeToPixel(mat, templ.getWidth(), templ.getHeight());

              tmr = MatInfo.templateMatch(mat, templMat,Imgproc.TM_CCORR_NORMED);
              
              if(finalTmr.getResult() < tmr.getResult()) {
                String name = templ.getName();
                int indexOf = name.indexOf('_');
                
                finalTmr = tmr;
                
                if(indexOf > -1) {
                  name = name.substring(0, indexOf);
                }
                finalTmr.setName(name);
                
                if(finalTmr.getResult() > 0.9) {
                  gotGoodResult = true;
                  break;
                }
              }
            }
          } catch (IOException e) {
            
          }
        }
      }
    }
    
    return finalTmr.getName();
  }

  private static ConnectedComponentsBlob[] findBlobs(Mat mat, Mat mask) {
    Mat blackWhite = new Mat();
    Imgproc.cvtColor(mat, blackWhite, Imgproc.COLOR_BGR2GRAY);

    Mat outBlack = Filter.thresholdInv(blackWhite, 30);
    Mat outWhite = Filter.threshold(blackWhite, 160);
    
    ConnectedComponents connCompBlack = new ConnectedComponents(outBlack, mask);
    ConnectedComponents connCompWhite = new ConnectedComponents(outWhite, mask);
    connCompBlack.generate();
    connCompWhite.generate();
    
    ConnectedComponentsBlob[] connBlobBlack = connCompBlack.getBlobs();
    ConnectedComponentsBlob[] connBlobWhite = connCompWhite.getBlobs();
    
    int combinedLength = connBlobBlack.length + connBlobWhite.length;
    ConnectedComponentsBlob[] connBlob = new ConnectedComponentsBlob[combinedLength];
    
    int ii = 0;
    for(int i = 0; i < connBlobBlack.length; i++) {
      connBlob[i] = connBlobBlack[i];
      ii++;
    }
    for(int i = 0; i < connBlobWhite.length; i++) {
      connBlob[ii] = connBlobWhite[i];
      ii++;
    }
    
    return connBlob;
  }
  
  /*
   * [TODO] remove\mask found blobs from Mat going to findSignHalfColors() to lower chance of
   *        text + symbols being picked up as text
   */
  private static void findSignHalfColors(Mat mat, Mat mask, OutputObject outputObj) {
    int newHeight = mat.rows() / 2;
    Mat topMask = Filter.crop(mask, new Point(0,0), mat.cols(), newHeight);
    Mat bottomMask = Filter.crop(mask, new Point(0, newHeight), mat.cols(), newHeight);
    
    /* placard-7-radioactive.png was identified wrong, saw top as white instead of yellow
     * [TODO] Stop using mask that works around this
     */
    topMask = Imgcodecs.imread("./Data/Masks/MaskTopSmallerRadio.png");
    Imgproc.cvtColor(topMask, topMask, Imgproc.COLOR_BGR2GRAY);
    // **************************************************************
    
    Mat topHalf = Filter.crop(mat, new Point(0,0), mat.cols(), newHeight);
    Mat bottomHalf = Filter.crop(mat, new Point(0, newHeight), mat.cols(), newHeight);
    
    outputObj.topColor = MatInfo.getMainColor(topHalf, topMask);
    outputObj.bottomColor = MatInfo.getMainColor(bottomHalf, bottomMask);
  }
  
  // Very similar to find character but there is a few changes in different spots
  // Which make it very hard to put subcode into functions
  private static MatchResult findSymbol(Mat mat) {
    MatchResult finalTmr = new MatchResult("Unknown");
    
    // match must be at least 50% to count
    finalTmr.setResult(0.5);
    
    boolean gotGoodResult = false;
    
    File dir = new File("./Data/SymbolTemplates");
    File[] dirList = dir.listFiles();

    if(dir.isDirectory()) { 
      if(dirList != null && dirList.length > 0 ) {
        for(File file : dirList) {
            try {
              if(gotGoodResult) {
                break;
              }
              ImageFileObject templ = new ImageFileObject(file, Imgcodecs.IMREAD_GRAYSCALE);
              MatchResult tmr = new MatchResult();
              
              tmr.setResult(0);
              
              Mat templMat;
              templMat = templ.getMat();
              
              double templWidthToHeightRatio = (double)templ.getWidth() / (double)templ.getHeight();
              double matWidthToHeightRatio = (double)mat.width() / (double)mat.height();
              
              double ratioDiff = Math.abs(templWidthToHeightRatio - matWidthToHeightRatio);
              
              if(ratioDiff < 0.05) {
                // Should rescale images up to the same size without ruining ratio
                templMat = Filter.resizeToPixelWidth(templ.getMat(), mat.width());
                
                if(templMat.width() <= mat.width() && templMat.height() <= mat.height()) {
                  // Cant save sobel templates or resize will cause issues
                  tmr = MatInfo.templateMatch(Filter.sobel(mat), Filter.sobel(templMat));

                  if(finalTmr.getResult() < tmr.getResult()) {
                    String name = templ.getName();
                    int indexOf = name.indexOf('_');
                    
                    finalTmr = tmr;
                    
                    if(indexOf > -1) {
                      name = name.substring(0, indexOf);
                    }
                    finalTmr.setName(name);
                    
                    if(finalTmr.getResult() > 0.9) {
                      gotGoodResult = true;
                      break;
                    }
                  }
                }
              }
            } catch (IOException e) {
              
            }
          }
        }
      }
    return finalTmr;
  }
  
  static JFrame winShow(String title , Mat img) {
    return imshow(title, img);
  }
  
  static void winWait() {
    Scanner input = new Scanner(System.in);
    input.nextLine();
    
    closeWins();
  }
  
  static void closeWins() {
    for(JFrame frame : _frameList) {
      frame.dispose();
    }
    
    _frameList.clear();
  }
  
  // [TODO] REMOVE, THIS IS THIRD PARTY CODE USED TO DEBUG AND DISPLAY IMAGES
  public static JFrame imshow(String title, Mat src) {
    BufferedImage bufImage = null;
    try {
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".png", src, matOfByte); 
        byte[] byteArray = matOfByte.toArray();
        InputStream in = new ByteArrayInputStream(byteArray);
        bufImage = ImageIO.read(in);

        JFrame frame = new JFrame(title);
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(new JLabel(new ImageIcon(bufImage)));
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        _frameList.add(frame);
        
        return frame;
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}
}