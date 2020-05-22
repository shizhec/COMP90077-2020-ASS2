import java.util.ArrayList;

public class WBBST {
    public WBBSTNode root;
    private static WBBSTNode nil = new WBBSTNode(0, null);

    public WBBST() {
        root = nil;
    }

    // Assume the points set is sorted
    public void construct_sorted(ArrayList<DataPoint> points_set) {
        // construct WBBST recursively
        root = construct(points_set);
    }

    private WBBSTNode construct(ArrayList<DataPoint> points_set) {
        if (points_set.size() == 0) {
            return null;
        } else {
            // find the median x, create a node storing x
            int x_index = Math.round(points_set.size()/2);
            DataPoint point = points_set.get(x_index);
            WBBSTNode u = new WBBSTNode(point.y, point);

            // get Points Set P1 and P2, where P1.elements.x < point.x and P2.elements.x > point.x
            ArrayList<DataPoint> p1 = new ArrayList<>(points_set.subList(0, x_index));
            ArrayList<DataPoint> p2 = new ArrayList<>(points_set.subList(x_index + 1, points_set.size()));

            // Recursively Construct t1 and t2 from p1 and p2
            WBBSTNode t1 = construct(p1);
            WBBSTNode t2 = construct(p2);

            u.left = t1;
            u.right = t2;

            return u;
        }
    }
}