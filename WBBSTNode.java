

public class WBBSTNode {
    WBBSTNode left, right;
    int y;
    DataPoint point;
    int size;

    public WBBSTNode(int y, DataPoint point) {
        this(y, point, null, null);
    }

    public WBBSTNode(int y, DataPoint point, WBBSTNode left, WBBSTNode right) {
        this.y = y;
        this.point = point;
        this.right = right;
        this.left = left;
    }
}