import java.util.ArrayList;
import java.util.Stack;

public class RangeTreeFC extends RangeTreeOrg{
    private RangeTreeNode root;
    private static RangeTreeNode nil = new RangeTreeNode(0, null);

    public RangeTreeFC() {
        root = nil;
    }

    public RangeTreeNode construct_FC(ArrayList<DataPoint> points_set) {
        // Sort the points set by x coordinate
        points_set = sort_points_by("x", points_set);

        // Construct base tree recursively
        root = construct(points_set);

        // Sort the points set by y
        points_set =  sort_points_by("y", points_set);

        // Construct FC Point Array
        ArrayList<DataPointFC> fc_points_set_sorted_y = new ArrayList<>();
        for (DataPoint dataPoint : points_set) {
            fc_points_set_sorted_y.add(new DataPointFC(dataPoint));
        }
        construct_fc_Array_sorted(root, fc_points_set_sorted_y);
        

        return root;
    }

    private void construct_fc_Array_sorted(RangeTreeNode root, ArrayList<DataPointFC> root_points_set) {
        // construct left points set and right points set
        ArrayList<DataPointFC> left_points_set = new ArrayList<>();
        ArrayList<DataPointFC> right_points_set = new ArrayList<>();
        if (root_points_set.size() > 0) {
            for (DataPointFC dataPoint : root_points_set) {
                DataPointFC newPoint = new DataPointFC(dataPoint.point);
                if (dataPoint.point.x < root.x) {
                    left_points_set.add(newPoint);
                } else if (dataPoint.point.x > root.x) {
                    right_points_set.add(newPoint);
                } else {
                    // break tie by id
                    if (dataPoint.point.id < root.point.id) {
                        left_points_set.add(newPoint);
                    } else if (dataPoint.point.id > root.point.id) {
                        right_points_set.add(newPoint);
                    }
                }
            }
        }

        // Construct fc array recursively
        if (root.left != null) {
            // construct left tree
            construct_fc_Array_sorted(root.left, left_points_set);
        }

        // if points set contain elements, construct fc array
        if (root_points_set.size()>0) {
            construt_array(root_points_set, left_points_set, right_points_set);
            root.fcData = root_points_set;
        }


        if (root.right != null) {
            // construct right tree
            construct_fc_Array_sorted(root.right, right_points_set);
        }

    }

    private void construt_array(ArrayList<DataPointFC> root_points, 
                                                  ArrayList<DataPointFC> left_points,
                                                  ArrayList<DataPointFC> right_points) {

        // initial data
        if (left_points.size() > 0){
            int current_left_index = 0;
            DataPointFC current_leftPoint;
            int q_left;
    
            for (DataPointFC dataPoint : root_points) {
                int p = dataPoint.point.y;
                while (current_left_index < left_points.size()) {
                    current_leftPoint = left_points.get(current_left_index);
                    q_left = current_leftPoint.point.y;
                    if (p <= q_left) {
                        dataPoint.left_successor = current_leftPoint;
                        break;
                    } else {
                        current_left_index += 1;
                    }
                }
                
            }
        }

        if (right_points.size() > 0) {
            int current_right_index = 0;
            DataPointFC current_rightPoint;
            int q_right;

            for (DataPointFC dataPoint : root_points) {
                int p = dataPoint.point.y;
                while (current_right_index < right_points.size()) {
                    current_rightPoint = right_points.get(current_right_index);
                    q_right = current_rightPoint.point.y;
                    if (p <= q_right) {
                        dataPoint.right_successor = current_rightPoint;
                        break;
                    } else {
                        current_right_index += 1;
                    }
                }
            }
        }
        

        

    }

    public ArrayList<DataPoint> query2d(QueryGenerator.Square range) {
        if (root == nil) {
            System.out.println("Range Tree not yet constructed");
            return null;
        }
        ArrayList<DataPoint> output = new ArrayList<>();
        // find the successor of lower bound
        int a1_succ = find_successor_with_path(range.x_range.lower, root, new Stack<>());
        // find the predecessor of upper bound
        int b1_pred = find_predecessor_with_path(range.x_range.upper, root, new Stack<>());

        // find the Lowest Common Ancester u_split of a and b
        RangeTreeNode u_split = find_LCA(a1_succ, b1_pred, root);

        // if u_split in range query, report u_split
        if (range.in_Range(u_split.point)) {
            output.add(u_split.point);
        }
        // find succ(a2) in Ly(u_split) through binary search
        DataPointFC a2_succ_u_split = binary_search_successor(u_split.fcData, range.y_range.lower, 0, u_split.fcData.size() - 1, new Stack<>());
        if (a2_succ_u_split == null) {
            return null;
        }
        // initialize the current parent node w as u_split
        RangeTreeNode w = u_split;
        DataPointFC a2_succ_w = a2_succ_u_split;
        DataPointFC a2_succ_u;

        // find path from u_split to a1
        ArrayList<RangeTreeNode> La = new ArrayList<>();
        if (have_path(u_split, a1_succ, La)) {
            // For each node in the path La
            for (RangeTreeNode u : La) {
                // other than u_split
                if (!u.equals(u_split)) {
                    // if node in range query, report
                    if (range.in_Range(u.point)) {
                        output.add(u.point);
                    }
                    // jump from w's a2_succ to u's through successor pointers
                    if (u.x < w.x) {
                        if (a2_succ_w.left_successor != null) {
                            a2_succ_u = a2_succ_w.left_successor;
                            a2_succ_w = a2_succ_u;
                            w = u;
                        }
                    } else {
                        if (a2_succ_w.right_successor != null) {
                            a2_succ_u = a2_succ_w.right_successor;
                            a2_succ_w = a2_succ_u;
                            w = u;
                        }
                    }
                    if (u.right != null) {
                        u = u.right;
                    }
                    if (range.x_range.lower <= w.x) {
                        // locate succ(a2)
                        if (u.fcData != null) {
                            ArrayList<DataPointFC> fcdata = u.fcData;
                            if (u.x < w.x) {
                                if (a2_succ_w.left_successor != null) {
                                    a2_succ_u = a2_succ_w.left_successor;
                                    // Scan array to select point
                                    for (DataPointFC data : fcdata) {
                                        if (data.point.y >= a2_succ_u.point.y && data.point.y <= range.y_range.upper) {
                                            output.add(data.point);
                                        }
                                    }
                                }
                            } else {
                                if (a2_succ_w.right_successor != null) {
                                    a2_succ_u = a2_succ_w.right_successor;
                                    // Scan array to select point
                                    for (DataPointFC data : fcdata) {
                                        if (data.point.y >= a2_succ_u.point.y && data.point.y <= range.y_range.upper) {
                                            output.add(data.point);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // initialize the current parent node w as u_split
        w = u_split;
        a2_succ_w = a2_succ_u_split;
        // find path from u_split to b1
        ArrayList<RangeTreeNode> Lb = new ArrayList<>();
        if (have_path(u_split, b1_pred, Lb)) {
            // For each node in the path Lb
            for (RangeTreeNode u : Lb) {
                // other than u_split
                if (!u.equals(u_split)) {
                    // if node in range, report
                    if (range.in_Range(u.point)) {
                        output.add(u.point);
                    }
                    // jump from w's a2_succ to u's through successor pointers
                    if (u.x < w.x) {
                        if (a2_succ_w.left_successor != null) {
                            a2_succ_u = a2_succ_w.left_successor;
                            a2_succ_w = a2_succ_u;
                            w = u;
                        }
                    } else {
                        if (a2_succ_w.right_successor != null) {
                            a2_succ_u = a2_succ_w.right_successor;
                            a2_succ_w = a2_succ_u;
                            w = u;
                        }
                    }
                    if (u.left != null) {
                        u = u.left;
                    }
                    if (range.x_range.upper >= w.x) {
                        // locate succ(a2)
                        if (u.fcData != null) {
                            ArrayList<DataPointFC> fcdata = u.fcData;
                            if (u.x < w.x) {
                                if (a2_succ_w.left_successor != null) {
                                    a2_succ_u = a2_succ_w.left_successor;
                                    // Scan array to select point
                                    for (DataPointFC data : fcdata) {
                                        if (data.point.y >= a2_succ_u.point.y && data.point.y <= range.y_range.upper) {
                                            output.add(data.point);
                                        }
                                    }
                                }
                            } else {
                                if (a2_succ_w.right_successor != null) {
                                    a2_succ_u = a2_succ_w.right_successor;
                                    // Scan array to select point
                                    for (DataPointFC data : fcdata) {
                                        if (data.point.y >= a2_succ_u.point.y && data.point.y <= range.y_range.upper) {
                                            output.add(data.point);
                                        }
                                    }
                                }
                            }
                        }
                        
                    }
                }
            }
        }

        return output;
    }

    private DataPointFC binary_search_successor(ArrayList<DataPointFC> fc_points, int a2, int l, int r, Stack<DataPointFC> stack) {
        if (r >= l) {
            int mid = l + (r - l) / 2;

            DataPointFC fcpoint = fc_points.get(mid);
            if (fcpoint.point.y == a2) {
                return fcpoint;
            }

            if (fcpoint.point.y > a2) {
                stack.push(fcpoint);
                return binary_search_successor(fc_points, a2, l, mid - 1, stack);
            } else {
                return binary_search_successor(fc_points, a2, mid + 1, r, stack);
            }
        }
        if (stack.size() > 0) {
            return stack.pop();
        }

        return null;
    }

}