public class SimilarPair {

    public int id1, id2;
    public double similarity;


    public SimilarPair(int id1, int id2, double similarity) {
        this.id1 = id1;
        this.id2 = id2;
        this.similarity = similarity;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SimilarPair && ((SimilarPair) obj).id1 == id1 && ((SimilarPair) obj).id2 == id2;
    }

    @Override
    public String toString() {
        return String.format("%d-%d: %.2f%%", id1, id2, similarity * 100);
    }

}
