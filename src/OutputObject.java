
public class OutputObject {
  // Not using _ naming convention because they are public
  String topColor;
  String bottomColor;
  String classNum;
  String otherText;
  String symbol;
  
  public OutputObject() {
    topColor = "Not Implemented";
    bottomColor = "Not Implemented";
    classNum = "Not Implemented";
    otherText = "Not Implemented";
    symbol = "Not Implemented";
  }
  
  @Override
  public String toString() {
    return "\nTop: " + topColor
         + "\nBottom: " + bottomColor
         + "\nClass: " + classNum
         + "\nText: " + otherText
         + "\nSymbol: " + symbol;
  }
}
