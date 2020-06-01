public class DataPointFC {
    public DataPoint point;
    public DataPointFC left_successor, right_successor;

    public DataPointFC(DataPoint point) {
        this.point = point;
        this.left_successor = null;
        this.right_successor = null;
    }

    @Override
    public String toString() {
        return point.toString();
    }
}