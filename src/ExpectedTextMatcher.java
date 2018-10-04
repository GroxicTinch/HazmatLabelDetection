
public class ExpectedTextMatcher {
  private static final String[] _expectedStrings = {
                                  "(none)",
                                  "CORROSIVE",
                                  "DANGEROUS WHEN WET",
                                  "EXPLOSIVE",
                                  "FLAMMABLE",
                                  "FLAMMABLE GAS",
                                  "FLAMMABLE LIQUID",
                                  "FUEL OIL",
                                  "GASOLINE",
                                  "INHALATION HAZARD",
                                  "NON-FLAMMABLE GAS",
                                  "ORGANIC PEROXIDE",
                                  "OXIDIZER",
                                  "OXYGEN",
                                  "POISON",
                                  "RATIOACTIVE",
                                  "RATIOACTIVE I",
                                  "RATIOACTIVE II",
                                  "RATIOACTIVE III",
                                  "SPONTANEOUSLY COMBUSTIBLE",
                                  "TOXIC"
                                  };
  
  public static String match(String needle) {
    int minDistance = -1;
    String matchingString = "";
    
    Levenshtein lev = new Levenshtein(needle);
    
    
    for(int i = 0; i < _expectedStrings.length; i++) {
      int distance = lev.findDistance(_expectedStrings[i]);
      
      if(distance < minDistance || minDistance == -1) {
        minDistance = distance;
        matchingString = _expectedStrings[i];
      }
    }
    
    return matchingString;
  }
}
