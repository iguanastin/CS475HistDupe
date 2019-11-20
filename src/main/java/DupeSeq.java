import java.io.IOException;
import java.sql.*;
import java.util.*;

public class DupeSeq {


    public static void main(String[] args) throws SQLException {
        Connection db = DriverManager.getConnection("jdbc:h2:" + args[0], "sa", "");
        Statement s = db.createStatement();

        Map<Integer, float[][]> hists = new HashMap<>();

        long t = System.nanoTime();
        System.out.println("Starting query...");
        ResultSet rs = s.executeQuery("SELECT TOP " + Constants.N + " id,hist_r,hist_g,hist_b,hist_a FROM media WHERE hist_r IS NOT NULL;");
        System.out.printf("Query: %.6fs\n", (System.nanoTime() - t) / 1000000000.0);

        t = System.nanoTime();
        while (rs.next()) {
            try {
                float[][] hist = new float[4][];

                hist[0] = Utils.inputStreamAsArray(rs.getBinaryStream("hist_r"));
                hist[1] = Utils.inputStreamAsArray(rs.getBinaryStream("hist_g"));
                hist[2] = Utils.inputStreamAsArray(rs.getBinaryStream("hist_b"));
                hist[3] = Utils.inputStreamAsArray(rs.getBinaryStream("hist_a"));

                hists.put(rs.getInt("id"), hist);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.printf("Read: %.6fs\n", (System.nanoTime() - t) / 1000000000.0);
        long size = hists.size();
        System.out.println("n: " + size);
        System.out.println("n*n: " + (size * size));
        System.out.println();

        System.out.println("Closing database...");
        rs.close();
        s.execute("SHUTDOWN DEFRAG;");
        s.close();
        db.close();
        System.out.println();
        System.out.println("Starting duplicate find...");

        t = System.nanoTime();
        List<SimilarPair> pairs = new ArrayList<>(10000);
        List<Integer> ids = new ArrayList<>(hists.keySet());
        for (Integer i1 : ids) {
            if (i1 % 1000 == 0) {
                System.out.printf("ID: %d\t\tTime: %.6fs\t\tPairs: %d\n", i1, (System.nanoTime() - t) / 1000000000.0, pairs.size());
            }

            for (Integer i2 : ids) {
                if (i1.equals(i2)) continue;

                float similarity = Utils.getSimilarity(hists.get(i1), hists.get(i2));
                if (similarity > Constants.CONFIDENCE) {
                    SimilarPair pair = new SimilarPair(i1, i2, similarity);
                    pairs.add(pair);
                }
            }
        }

        System.out.printf("Pair find: %.6fs\n", (System.nanoTime() - t) / 1000000000.0);

        LinkedHashSet<SimilarPair> set = new LinkedHashSet<>(pairs);
        pairs.clear();
        pairs.addAll(set);

        System.out.println("Pair count: " + pairs.size());

        if (pairs.size() >= 3) {
            System.out.println();
            System.out.println("First three pairs: ");
            for (int i = 0; i < 3; i++) {
                System.out.println("\t" + pairs.get(i));
            }
            System.out.println();
            System.out.println("Last three pairs: ");
            for (int i = pairs.size() - 1; i > pairs.size() - 4; i--) {
                System.out.println("\t" + pairs.get(i));
            }
        }
    }

}
