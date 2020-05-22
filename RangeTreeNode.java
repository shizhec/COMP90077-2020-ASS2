
public class RangeTreeNode {
    RangeTreeNode left, right;
    int x;
    DataPoint point;
    public WBBST subTree;

    public RangeTreeNode(int x, DataPoint point) {
        this(x, point, null, null);
    }

    public RangeTreeNode(int x, DataPoint point, RangeTreeNode left, RangeTreeNode right) {
        this.x = x;
        this.point = point;
        this.left = left;
        this.right = right;
    }
}