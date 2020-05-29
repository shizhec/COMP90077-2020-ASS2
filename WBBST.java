import java.util.ArrayList;
import java.util.Stack;

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
            // find the median y, create a node storing y
            int y_index = Math.round(points_set.size()/2);
            DataPoint point = points_set.get(y_index);
            WBBSTNode u = new WBBSTNode(point.y, point);

            // get Points Set P1 and P2, where P1.elements.x < point.x and P2.elements.x > point.x
            ArrayList<DataPoint> p1 = new ArrayList<>(points_set.subList(0, y_index));
            ArrayList<DataPoint> p2 = new ArrayList<>(points_set.subList(y_index + 1, points_set.size()));

            // Recursively Construct t1 and t2 from p1 and p2
            WBBSTNode t1 = construct(p1);
            WBBSTNode t2 = construct(p2);

            u.left = t1;
            u.right = t2;

            return u;
        }
    }

    public ArrayList<DataPoint> query(QueryGenerator.Square range) {
        // None checking
        if (root == nil) {
            return null;
        }
        // run 1 dimensional query
        ArrayList<DataPoint> output = new ArrayList<>();
        // find successor
        int a = find_successor_with_path(range.y_range.lower, root, new Stack<>());
        // find predecessor
        int b = find_predecessor_with_path(range.y_range.upper, root, new Stack<>());
        // find Lowest Common Ancester
        WBBSTNode u_split = find_LCA(a, b, root);

        // if u_split in range, report it
        if (range.in_Range(u_split.point)) {
            output.add(u_split.point);
        }

        // find path from u_split to a
        ArrayList<WBBSTNode> La = new ArrayList<>();
        if (have_path(u_split, a, La)) {
            // for each node in La, report qualified points
            for (WBBSTNode wbbstNode : La) {
                // other then u_split
                if (!wbbstNode.equals(u_split)) {
                    // if node in range, report
                    if (range.in_yRange(u_split.y)) {
                        output.add(wbbstNode.point);
                    }
                    if (a <= wbbstNode.y && wbbstNode.right != null) {
                        report_subtree_point(wbbstNode.right, output);
                    }
                }
            }
        }
        // find path from u_split to b
        ArrayList<WBBSTNode> Lb = new ArrayList<>();
        if (have_path(u_split, b, Lb)) {
            // for each node in Lb, report qualified points
            for (WBBSTNode wbbstNode : Lb) {
                // other then u_split
                if (!wbbstNode.equals(u_split)) {
                    // if node in range, report
                    if (range.in_yRange(u_split.y)) {
                        output.add(wbbstNode.point);
                    }
                    if (b >= wbbstNode.y && wbbstNode.left != null) {
                        report_subtree_point(wbbstNode.left, output);
                    }
                }
            }
        }
        return output;
    }

    private int find_successor_with_path(int y, WBBSTNode root, Stack<Integer> value) {
        if (root == null) {
            if (value.size() > 0) {
                return value.pop();
            } else {
                return -1;
            }
        } else {
            if (y <= root.y) {
                value.push(root.y);
                return find_successor_with_path(y, root.left, value);
            } else {
                return find_successor_with_path(y, root.right, value);
            }
        }
    }

    private int find_predecessor_with_path(int y, WBBSTNode root, Stack<Integer> value) {
        if (root == null) {
            if (value.size() > 0) {
                return value.pop();
            } else {
                return -1;
            }
        } else {
            if (root.y <= y) {
                value.push(root.y);
                return find_predecessor_with_path(y, root.right, value);
            } else {
                return find_predecessor_with_path(y, root.left, value);
            }
        }
    }

    private WBBSTNode find_LCA(int a, int b, WBBSTNode root) {
        if (root == null) {
            return null;
        }
        if (root.y > a && root.y > b) {
            return find_LCA(a, b, root.left);
        }
        if (root.y < a && root.y < b) {
            return find_LCA(a, b, root.right);
        }
        return root;
    }

    private boolean have_path(WBBSTNode node, int y, ArrayList<WBBSTNode> path) {
        if (node == null) {
            return false;
        }
        path.add(node);
        if (node.y == y) {
            return true;
        }
        if (have_path(node.left, y, path) || have_path(node.right, y, path)) {
            return true;
        }
        path.remove(path.size()-1);
        return false;
    }

    private void report_subtree_point(WBBSTNode root, ArrayList<DataPoint> points) {
        if (root.left != null) {
            report_subtree_point(root.left, points);
        }
        // report point
        points.add(root.point);

        if (root.right != null) {
            report_subtree_point(root.right, points);
        }
    }
}