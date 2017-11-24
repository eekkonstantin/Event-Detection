/* AUTHORSHIP STATEMENT
Elizabeth Konstantin Kwek Jin Li (2287563K)
Web Science Level 4 Coursework 2017
This is my own work as defined in the Academic Ethics agreement I have signed.
*/

import org.apache.commons.lang3.StringUtils;

public class Helper {
  public static long hToMs(int h) {
    return (long) (h * 3600000);
  }

  public static double strSimilarity(String s1, String s2) {
    String longer = s1, shorter = s2;
    if (s1.length() < s2.length()) {
      longer = s2;
      shorter = s1;
    }
    int longerLength = longer.length();
    if (longerLength == 0)
      return 1.0; // both strings are zero length
    return (longerLength - StringUtils.getLevenshteinDistance(longer, shorter)) / (double) longerLength;
  }

  public static String join(Object[] array) {
    return StringUtils.join(array, ',');
  }

  // public static void main(String[] args) {
  //   System.out.println(Helper.strSimilarity(args[0], args[1]));
  // }
}
