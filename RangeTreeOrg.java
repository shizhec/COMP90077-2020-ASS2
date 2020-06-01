import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Stack;


public class RangeTreeOrg {
    private RangeTreeNode root;
    private static RangeTreeNode nil = new RangeTreeNode(0, null);

    public RangeTreeOrg() {
        root = nil;
    }

    public RangeTreeNode construct_naive(ArrayList<DataPoint> points_set) {
        // Sort the points set by x coordinate
        ArrayList<DataPoint> points_set_sorted =  sort_points_by("x", points_set);

        // Construct base tree recursively
        root = construct(points_set_sorted);

        // For each node in base tree, construct a WB-BST on y of all points in subtree.
        construct_subtree_naive(root);

        return root;
    }

    public RangeTreeNode construct_sorted(ArrayList<DataPoint> points_set) {
        // Sort the points set by x
        ArrayList<DataPoint> points_set_sorted_x =  sort_points_by("x", points_set);

        // Construct base tree on x recursively
        root = construct(points_set_sorted_x);

        // Sort the points set by y
        ArrayList<DataPoint> points_set_sorted_y =  sort_points_by("y", points_set);

        // Construct subtree on y for each node
        construct_subtree_sorted(root, points_set_sorted_y);

        return root;
    }

    public RangeTreeNode construct(ArrayList<DataPoint> points_set) {
        // if sets is empty, return null
        if (points_set.size() == 0) {
            return null;
        } else {
            // find the median x, create a node storing x
            int x_index = Math.round(points_set.size()/2);
            DataPoint point = points_set.get(x_index);
            RangeTreeNode u = new RangeTreeNode(point.x, point);

            // get Points Set P1 and P2, where P1.elements.x < point.x and P2.elements.x > point.x
            ArrayList<DataPoint> p1 = new ArrayList<>(points_set.subList(0, x_index));
            ArrayList<DataPoint> p2 = new ArrayList<>(points_set.subList(x_index + 1, points_set.size()));

            // Recursively Construct t1 and t2 from p1 and p2
            RangeTreeNode t1 = construct(p1);
            RangeTreeNode t2 = construct(p2);

            // set u.left to t1, u.right to t2
            u.left = t1;
            u.right = t2;

            return u;
        }
    }
    
    // traverse tree inorder
    private void construct_subtree_naive(RangeTreeNode root) {
        if (root.left != null) {
            // recursively traverse left tree
            construct_subtree_naive(root.left);
        }

        // Construt subtree for current node
        ArrayList<DataPoint> P = new ArrayList<>();
        get_subtreePoints(root, P);

        if (P.size() != 0) {
            // sort the point set by y coordinates
            ArrayList<DataPoint> points_set_sorted =  RangeTreeOrg.sort_points_by("y", P);
            WBBST subtree = new WBBST();
            subtree.construct_sorted(points_set_sorted);
            root.subTree = subtree;
        } else {
            root.subTree = null;
        }

        if (root.right != null) {
            // recursively traverse right tree
            construct_subtree_naive(root.right);
        }
    }

    // traverse tree inorder, construct WBBST on y
    private void construct_subtree_sorted(RangeTreeNode root, ArrayList<DataPoint> root_points_set) {
        // construct left points set and right points set
        ArrayList<DataPoint> left_points_set = new ArrayList<>();
        ArrayList<DataPoint> right_points_set = new ArrayList<>();
        if (root_points_set.size() > 0) {
            for (DataPoint dataPoint : root_points_set) {
                if (dataPoint.x < root.x) {
                    left_points_set.add(dataPoint);
                } else if (dataPoint.x > root.x) {
                    right_points_set.add(dataPoint);
                } else {
                    // break tie by id
                    if (dataPoint.id < root.point.id) {
                        left_points_set.add(dataPoint);
                    } else if (dataPoint.id > root.point.id) {
                        right_points_set.add(dataPoint);
                    }
                }
            }
        }
        
        // Construct tree
        if (root.left != null) {
            // recursively traverse left tree
            construct_subtree_sorted(root.left, left_points_set);
        }
        
        // if points set contain elements, construct subtree on sorted y
        if (root_points_set.size() > 0) {
            WBBST subtree = new WBBST();
            subtree.construct_sorted(root_points_set);
            root.subTree = subtree;
        } else {
            root.subTree = null;
        }

        if (root.right != null) {
            // recursively traverse left tree
            construct_subtree_sorted(root.right, right_points_set);
        }
    }

    public ArrayList<DataPoint> query2d(QueryGenerator.Square range) {
        if (root == nil) {
            System.out.println("Range Tree not yet constructed");
            return null;
        }
        ArrayList<DataPoint> output = new ArrayList<>();
        // find the successor of lower bound
        int a = find_successor_with_path(range.x_range.lower, root, new Stack<>());
        // find the predecessor of upper bound
        int b = find_predecessor_with_path(range.x_range.upper, root, new Stack<>());

        // find the Lowest Common Ancester u_split of a and b
        RangeTreeNode u_split = find_LCA(a, b, root);

        // if u_split in range query, report u_split
        if (range.in_Range(u_split.point)) {
            output.add(u_split.point);
        }

        // find path from u_split to a
        ArrayList<RangeTreeNode> La = new ArrayList<>();
        if (have_path(u_split, a, La)) {
            // For each node in the Path La, 
            for (RangeTreeNode node : La) {
                // other than u_split
                if (!node.equals(u_split)) {
                    // if node in the range query, report
                    if (range.in_Range(node.point)) {
                        output.add(node.point);
                    }
                    // if a <= node.x
                    if (a <= node.x && node.right != null) {
                        // find points in y range through secondery tree query reporting.
                        WBBST sec_tree = node.right.subTree;
                        ArrayList<DataPoint> sec_reported = sec_tree.query(range);
                        // if there are points reported, add to the final report
                        if (sec_reported.size() > 0) {
                            output.addAll(sec_reported);
                        }
                    }

                }
            }
        }

        // find path from u_split to b
        ArrayList<RangeTreeNode> Lb = new ArrayList<>();
        if (have_path(u_split, b, Lb)) {
            // For each node in the Path Lb,
            for (RangeTreeNode node : Lb) {
                // other then u_split
                if (!node.equals(u_split)) {
                    // if node in the range query, report
                    if (range.in_Range(node.point)) {
                        output.add(node.point);
                    }
                    // if b >= node.x
                    if (b >= node.x && node.left != null) {
                        // find points in y range through secondery tree query reporting.
                        WBBST sec_tree = node.left.subTree;
                        if (sec_tree != null) {
                            ArrayList<DataPoint> sec_reported = sec_tree.query(range);
                            // if there are points reported, add to the final report
                            if (sec_reported.size() > 0) {
                                output.addAll(sec_reported);
                            }
                        }
                    }

                }
            }
        }
        return output;
    }

    public int find_predecessor_with_path(int x, RangeTreeNode root, Stack<Integer> value) {
        if (root == null) {
            if (value.size() > 0) {
                return value.pop();
            } else {
                return -1;
            }
        } else {
            if (root.x <= x) {
                value.push(root.x);
                return find_predecessor_with_path(x, root.right, value);
            } else {
                return find_predecessor_with_path(x, root.left, value);
            }
        }
    }

    public int find_successor_with_path(int x, RangeTreeNode root, Stack<Integer> value) {
        if (root == null) {
            if (value.size() > 0) {
                return value.pop();
            } else {
                return -1;
            }
        } else {
            if (x <= root.x) {
                value.push(root.x);
                return find_successor_with_path(x, root.left, value);
            } else {
                return find_successor_with_path(x, root.right, value);
            }
        }
    }

    public RangeTreeNode find_LCA(int a, int b, RangeTreeNode root) {
        if (root == null) {
            return null;
        }
        if (root.x > a && root.x > b) {
            return find_LCA(a, b, root.left);
        }
        if (root.x < a && root.x < b) {
            return find_LCA(a, b, root.right);
        }
        return root;
    }

    public boolean have_path(RangeTreeNode node, int x, ArrayList<RangeTreeNode> path) {
        if (node == null) {
            return false;
        }
        path.add(node);
        if (node.x == x) {
            return true;
        }
        if (have_path(node.left, x, path) || have_path(node.right, x, path)) {
            return true;
        }
        path.remove(path.size()-1);
        return false;
    }

    private void get_subtreePoints(RangeTreeNode node, ArrayList<DataPoint> points) {
        if (node.left != null) {
            get_subtreePoints(node.left, points);
        }

        points.add(node.point);

        if (node.right != null) {
            get_subtreePoints(node.right, points);
        }
    }

    private void get_subtreeNode(RangeTreeNode node, ArrayList<RangeTreeNode> nodes) {
        // recursive to left tree
        if (node.left != null) {
            get_subtreeNode(node.left, nodes);
        }
        // add current node
        nodes.add(node);
        // recuresive to right tree
        if (node.right != null) {
            get_subtreeNode(node.right, nodes);
        }
    }

    public static ArrayList<DataPoint> sort_points_by(String keyword, ArrayList<DataPoint> points_set) {
        if (keyword.equals("x")) {
            Comparator<DataPoint> by_X = (DataPoint p1, DataPoint p2) -> p1.x - p2.x;
            Collections.sort(points_set, by_X);
            return points_set;
        } else {
            Comparator<DataPoint> by_Y = (DataPoint p1, DataPoint p2) -> p1.y - p2.y;
            Collections.sort(points_set, by_Y);
            return points_set;
        }
    }
}