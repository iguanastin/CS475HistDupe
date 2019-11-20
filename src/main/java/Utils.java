import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class Utils {

    private Utils() {
        // Don't allow constructing
    }


    public static double getSimilarity(double[][] h1, double[][] h2) {
        double dr = 0, dg = 0, db = 0, da = 0;

        for (int i = 0; i < Constants.BIN_SIZE; i++) {
            dr += Math.abs(h1[0][i] - h2[0][i]);
            dg += Math.abs(h1[1][i] - h2[1][i]);
            db += Math.abs(h1[2][i] - h2[2][i]);
            da += Math.abs(h1[3][i] - h2[3][i]);
        }

        return 1 - (da + dr + dg + db) / 8;
    }

    public static double[] inputStreamAsArray(InputStream in) throws IOException {
        byte[] b = new byte[Constants.BIN_SIZE * 8];
        if (in.read(b) != Constants.BIN_SIZE * 8) return null;

        ByteBuffer bb = ByteBuffer.wrap(b);
        double[] result = new double[Constants.BIN_SIZE];

        for (int i = 0; i < Constants.BIN_SIZE; i++) {
            result[i] = bb.getDouble();
        }

        return result;
    }

}
