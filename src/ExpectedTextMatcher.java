import java.util.ArrayList;

public class ExpectedTextMatcher {
  private static ArrayList<String> _expectedStrings = new ArrayList<String>();
  
  private static boolean _isSetup = false;
  
  /*
   * Takes in one string which is the needle, and searches the _expectedStrings for the closest match
   * using the Levenshtein algorithm
   */
  public static String match(String needle) {
    if(!_isSetup) {
      setUp();
    }
    
    int minDistance = -1;
    String matchingString = "";
    
    Levenshtein lev = new Levenshtein(needle);
    
    for(String expected : _expectedStrings) {
      int distance = lev.findDistance(expected);
      
      if(distance < minDistance || minDistance == -1) {
        minDistance = distance;
        matchingString = expected;
      }
    }
    
    return matchingString;
  }
  
  public static void setUp() {
    try {
      _expectedStrings = Utils.loadStringList("./Data/ExpectedTexts", "txt");
    } catch (MPException e) {
      System.out.println("./Data/ExpectedTexts.txt was not found\n"
                       + "as a result all strings will be matched to (none)");
    }
    
    _isSetup = true;
  }
  
  public static ArrayList<String> getExpectedStrings() {
    if(!_isSetup) {
      setUp();
    }
    
    return _expectedStrings;
  }
}
