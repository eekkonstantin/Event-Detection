/* AUTHORSHIP STATEMENT
Elizabeth Konstantin Kwek Jin Li (2287563K)
Web Science Level 4 Coursework 2017
This is my own work as defined in the Academic Ethics agreement I have signed.
*/

import java.util.*;
import java.lang.Math;

public class Cluster {
  private int id;
  private String name;
  private TokenList tokens = new TokenList();

  private HashMap<Long, ArrayList<String[]>> hourly = new HashMap<>();

  private int burst;

  public Cluster(String[] tweetLine) {
    this.id = Integer.parseInt(tweetLine[0]);
    this.name = tweetLine[1];

    // Initialize map
    for (long end : Detective.timestamps)
      hourly.put(end, new ArrayList<>());
  }

  public int getID() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public TokenList getTokens() {
    return this.tokens;
  }

  /**
   * Adds the given tweet into this cluster, replacing the cluster name
   * and id with those of the current cluster.
   * @param String[] tweet  Tweet data gathered from a single line of a
   *                        given CSV.
   */
  public void add(String[] tweet) {
    // ensure cluster id and name correct
    tweet[0] = "" + id;
    tweet[1] = name;

    // add tokens
    String[] toks = tweet[5].split(" ");
    for (String s : toks)
      tokens.add(s);

    // add tweet
    for (long end : Detective.timestamps) {
      if (Long.parseLong(tweet[3]) < end) {
        hourly.get(end).add(tweet);
        break;
      }
    }
  }

  /**
   * Compiles all tweets in the HashMap into a single arraylist for easy
   * iteration or counting.
   * @return ArrayList of all tweets in this cluster.
   */
  public ArrayList<String[]> getTweets() {
    ArrayList<String[]> ret = new ArrayList<>();
    for (long end : Detective.timestamps)
      ret.addAll(hourly.get(end));

    // System.err.println(this + " has " + ret.size() + " tweets.");
    return ret;
  }





  //////////////////////////// EVENT MERGING ////////////////////////////
  public void cleanTokens() {
    tokens.clean();
  }

  /**
   * Adds the tweets and tokens from the other cluster, then cleans the
   * token list.
   * @param Cluster other Cluster to absorb
   */
  public void absorb(Cluster other) {
    System.err.println(this + " absorbing " + other + ": " + compare(other));
    for (String[] tweet : other.getTweets()) {
      boolean alreadyIn = false;
      for (String[] own : getTweets()) {
        if (own[2].equalsIgnoreCase(tweet[2])) {
          alreadyIn = true;
          break;
        }
      }
      if (!alreadyIn)
        add(tweet);
    }

    cleanTokens();
  }

  /**
   * Compares each word in the cluster name.
   * @param  String other Name of the other cluster
   * @return              The percentage similarity between the 2 clusters'
   *                      names.
   */
  private double compareNames(String other) {
    String[] first = name.split(" ");
    String[] second = other.split(" ");

    ArrayList<Double> matches = new ArrayList<>();
    for (String a : first) {
      for (String b : second)
        matches.add(Helper.strSimilarity(a, b));
    }

    double ret = 0.0;
    for (double d : matches)
      ret += d;
    return ret / matches.size();
  }

  /**
   * Returns the similarity between 2 clusters' names and tokens, adjusted
   * for the ratio specified in {@code Detect}.
   * @param  Cluster other  Cluster to compare against
   * @return                The percentage similarity between the 2 clusters.
   */
  public double compare(Cluster other) {
    // return ( Helper.strSimilarity(this.getName(), other.getName()) * Detect.NAME_RATIO)
    return (compareNames(other.getName()) * Detect.NAME_RATIO)
          + (tokens.difference(other.getTokens()) * Detect.TOKEN_RATIO);
  }




  ////////////////////////// BURST DETECTION //////////////////////////
  /**
   * Determines if this cluster has a burst using an adapted version of
   * the Three Sigma rule.
   * At the same time, this method removes all tweets from windows with
   * less tweets than the calculated burst value.
   * @return Whether the cluster has at least one burst.
   */
  public boolean hasBurst() {
    calculateBurstValue();

    boolean hasBurst = false;

    for (long end : hourly.keySet()) {
      ArrayList<String[]> entries = hourly.get(end);
      if (entries.size() >= burst)
        hasBurst = true;
      else // replace
        hourly.put(end, new ArrayList<>());
    }

    return hasBurst;
  }

  private void calculateBurstValue() {
    int windows = hourly.keySet().size();
    int mean = getTweets().size() / windows;

    // Standard Deviation
    double std = 0;
    for (ArrayList<String[]> entries : hourly.values())
      std += Math.pow((entries.size() - mean), 2);
    std = Math.sqrt(std / windows);

    // Three Sigma (adapted)
    this.burst = (int) Math.ceil(mean + (1.5 * std));
  }




  ////////////////////////// NOISE REMOVAL //////////////////////////
  /**
   * Removes tweets that have less than {@code Detect.TOKEN_THRESHOLD}
   * tokens that have a > 50% similarity to any of the top tokens in
   * the cluster.
   */
  public void removeNoise() {
    for (long stamp : hourly.keySet()) {
      ArrayList<String[]> tweets = hourly.get(stamp);

      int i = 0;
      while (i < tweets.size()) {
        String[] tweet = tweets.get(i);

        int count = 0;
        for (String token : tweet[5].split(" ")) {
          if (tokens.contains(token)) {
            count++;
            if (count >= Detect.TOKEN_THRESHOLD)
              break;
          }
        }

        if (count < Detect.TOKEN_THRESHOLD)
          tweets.remove(i);
        else
          i++;
      }

      hourly.put(stamp, tweets);
    }
  }





  //////////////////////////// COMPILATION ////////////////////////////
	public String compile() {
		String out = "";
		for (String[] t : getTweets())
			out += Helper.join(t) + "\r\n";
		return out;
	}

  @Override
  public String toString() {
    return "Cluster " + id + ": " + name
            + "(TOTAL " + getTweets().size() + " BURST " + burst + ")";
  }
}
