import java.io.*;
import java.nio.charset.StandardCharsets;

public class Detect {

	public static final int WINDOW = 1;
	public static final int INSIG_BUFFER = 11;
	public static final double MATCH_THRESHOLD = 0.48;
	public static final double NAME_RATIO = 0.8;
	public static final double TOKEN_RATIO = 0.2;

	public static final int TOKEN_THRESHOLD = 1;

	public static void main(String[] args) {
		if (args.length < 2) {
		  System.out.println("Usage: java Detect [1 | 7] [output file name]");
		  System.exit(0);
	    }

		int d = Integer.parseInt(args[0]);
    String file = d + (d == 1 ? "day" : "days") + "/clusters.sortedby.time.csv";

    Detective det = new Detective(d);


    try {
    	BufferedReader br = new BufferedReader(new FileReader(file));
			String line = "";
			System.err.println("Reading " + file + "...");

			while ((line = br.readLine()) != null)
				det.add(line.split(",", 7));

			System.err.println(det.size() + " clusters created.");
			System.err.println(Detective.timestamps.size()
					+ " " + WINDOW + "-hour timestamps starting with " + Detective.startTime);

			System.err.println("Removing insignificant clusters...");
			det.removeInsignificant();

			System.err.println("Merging events...");
			det.mergeEvents();

			System.err.println("Running burst detection...");
			det.burstDetection();

			System.err.println("Removing noise...");
			det.removeNoise();


			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(args[1] + ".csv"), StandardCharsets.UTF_8);
			writer.write(det.compile());
			writer.close();
			System.err.println(det.size() + " clusters written to " + args[1] + ".");

    } catch (Exception e) {
    	e.printStackTrace();
    }

	}

}
