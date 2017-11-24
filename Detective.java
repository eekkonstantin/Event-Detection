/* AUTHORSHIP STATEMENT
Elizabeth Konstantin Kwek Jin Li (2287563K)
Web Science Level 4 Coursework 2017
This is my own work as defined in the Academic Ethics agreement I have signed.
*/

import java.util.*;

public class Detective {
	private ArrayList<Cluster> clusters = new ArrayList<>();

	public static int hours;
	public static long startTime;
	public static ArrayList<Long> timestamps = new ArrayList<>();

	public Detective(int d) {
		this.hours = d * 24;
	}

	public int size() {
		return clusters.size();
	}

	public boolean add(String[] tweetLine) {
		if (clusters.isEmpty()) // first tweet. log timing.
			setStartTime(Long.parseLong(tweetLine[3]));

		Cluster newC = new Cluster(tweetLine);
		for (Cluster c : clusters) {
			if (c.getID() == newC.getID()) {// cluster exists.
				c.add(tweetLine);
				return false;
			}
		}

		newC.add(tweetLine);
		clusters.add(newC);
		return true;
	}

	private void setStartTime(long stamp) {
		this.startTime = stamp;

		for (int h=0; h<hours; h+=Detect.WINDOW)
			timestamps.add( startTime + Helper.hToMs(h + Detect.WINDOW) );
	}

	private ArrayList<Cluster> copy() {
		return (ArrayList<Cluster>) clusters.clone();
	}

	public void removeCluster(Cluster rem, boolean notify) {
		if (notify)
    	System.err.println("Removing " + rem);

		clusters.remove(clusters.indexOf(rem));
  }





	/////////////////////////// NOISE FILTERING ///////////////////////////
	/**
	 * Removes clusters with > {@code Detect.INSIG_BUFFER} tweets.
	 */
	public void removeInsignificant() {
		ArrayList<Cluster> clCopy = copy();
		for (Cluster c : clCopy) {
			if (c.getTweets().size() < Detect.INSIG_BUFFER)
				removeCluster(c, false);
			else
				c.cleanTokens();
		}

	  System.err.println(clusters.size() + " clusters remaining with >=" + Detect.INSIG_BUFFER + " tweets.");
	}

	/**
	 * Removes redundant tweets from clusters. If a cluster has no tweets
	 * left after this operation, the cluster is removed.
	 */
	public void removeNoise() {
		int i = 0;
		while (i < clusters.size()) {
			Cluster c = clusters.get(i);
			c.removeNoise();
			if (c.getTweets().size() == 0)
				clusters.remove(i);
			else
				i++;
		}
		System.err.println(clusters.size() + " clusters remaining after noise removal with " + Detect.TOKEN_THRESHOLD + " threshold.");
	}





	//////////////////////////// EVENT MERGING ////////////////////////////
	/**
	 * Merges clusters that appear to be relevant based on the ratios and
	 * threshold specified in {@code Detect}.
	 */
	public void mergeEvents() {
		for (int i=0; i<clusters.size()-1; i++) {
			Cluster c1 = clusters.get(i);
			for (int i2=i+1; i2<clusters.size(); i2++) {
				Cluster c2 = clusters.get(i2);
				if (c1.compare(c2) > Detect.MATCH_THRESHOLD) {
					c1.absorb(c2);
					clusters.remove(i2);
				}
			}
		}

		System.err.println(clusters.size() + " clusters remaining after event merging with a name to token ratio of " + Detect.NAME_RATIO + ":" + Detect.TOKEN_RATIO + " at a " + Detect.MATCH_THRESHOLD + " threshold.");
	}





	////////////////////////// BURST DETECTION //////////////////////////
	/**
	 * Removes clusters without any bursts.
	 */
	public void burstDetection() {
		ArrayList<Cluster> clCopy = copy();
		int i = 0;
		while (i < clusters.size()) {
			Cluster c = clusters.get(i);
			if (!c.hasBurst()) // remove
				clusters.remove(i);
			else // not removed. increment.
				i++;
		}

		System.err.println(clusters.size() + " clusters remaining after burst detection in " + Detect.WINDOW + "-hourly windows.");
	}








	//////////////////////////// COMPILATION ////////////////////////////
	public String compile() {
		String out = "";
		for (Cluster c : clusters)
			out += c.compile();
		return out;
	}
}
