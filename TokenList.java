/* AUTHORSHIP STATEMENT
Elizabeth Konstantin Kwek Jin Li (2287563K)
Web Science Level 4 Coursework 2017
This is my own work as defined in the Academic Ethics agreement I have signed.
*/

import java.util.*;

public class TokenList {
  ArrayList<Token> tokens = new ArrayList<>();

  private int midpoint;

  public int getMidpoint() {
    return this.midpoint;
  }

  public Token get(int i) {
    return tokens.get(i);
  }

  public int size() {
    return tokens.size();
  }

  /**
   * Adds a token into the ArrayList. If this token is present, the
   * new token's count will be added into the existing.
   * @param  Token t  Token to add.
   * @return          whether a new token was added.
   */
  public boolean add(Token t) {
    if (this.contains(t)) {
      Token o = tokens.get(indexOf(t));
      o.inc(t.getCount());
      tokens.set(indexOf(t), o);
      return false;
    }
    return tokens.add(t);
  }

  public boolean add(String tStr) {
    Token t = new Token(tStr);
    return add(t);
  }

  /**
   * Checks if a token is relevant (appears more than average).
   * @param  String s Token name to check.
   * @return          Whether this token is relevant.
   */
  public boolean contains(String s) {
    for (int i=0; i<midpoint; i++) {
      if (Helper.strSimilarity(s, tokens.get(i).name) > 0.5)
        return true;
    }

    return false;
  }

  /**
   * Checks if the token list already contains a token with the
   * same name.
   * @param  Token t  Token to check for
   * @return          Whether the token name exists in the list.
   */
  private boolean contains(Token t) {
    for (Token o : tokens)
      if (t.name.equalsIgnoreCase(o.name))
        return true;
    return false;
  }

  private int indexOf(Token t) {
    for (int i=0; i<tokens.size(); i++) {
      if (t.name.equalsIgnoreCase(tokens.get(i).name))
        return i;
    }
    return -1;
  }

  /**
   * @return Average counts of all tokens in the list.
   */
  private int avgCount() {
    int ret = 0;
    for (Token t : tokens)
      ret += t.getCount();

    return ret / this.tokens.size();
  }

  /**
   * Sorts the list by the number of times each token appears in
   * the list. This function also detects the midpoint of the
   * sorted list, to be used in checking relevance of tokens.
   */
  public void clean() {
    Collections.sort(tokens);
    for (int i=0; i<tokens.size(); i++) {
      Token t = tokens.get(i);
      if (t.getCount() > avgCount()) {
        this.midpoint = i;
        break;
      }
    }
  }


  /**
   * Finds the difference between each word on each of the
   * given lists.
   * @param  TokenList other  The TokenList to check against
   * @return                  The total percentage similarity
   *                          between the 2 TokenLists.
   */
  public double difference(TokenList other) {
    ArrayList<Double> matches = new ArrayList<>();

    for (int i=0; i<midpoint; i++) {
      Token t = tokens.get(i);
      for (int i2=0; i2<other.getMidpoint(); i2++) {
        Token t2 = other.get(i2);
        matches.add(t.distance(t2));
      }
    }

    // int sMax = 10, oMax = 10;
    // if (tokens.size() < sMax)
    //   sMax = tokens.size() / 2;
    // if (other.size() < oMax)
    //   oMax = other.size() / 2;
    //
    // for (int i=0; i<sMax; i++) {
    //   Token t = tokens.get(i);
    //   for (int i2=0; i2<oMax; i2++) {
    //     Token t2 = other.get(i2);
    //     matches.add(t.distance(t2));
    //   }
    // }

    // Calculate average
    double ret = 0.0;
    for (double d : matches)
      ret += d;
    return ret / matches.size();
  }





  @Override
  public String toString() {
    String ret = "[";
    for (Token s : tokens)
      ret += s + ", ";
    return ret.substring(0, ret.length() - 2) + "]";
  }
}
