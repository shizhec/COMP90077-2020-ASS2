import java.util.ArrayList;

public class Test {
    public static ArrayList<DataPoint> generate_test_Points(int size) {
        ArrayList<DataPoint> test = new ArrayList<>();
        size += 1;
        for (int i=1; i < size; i++) {
            DataPoint point = new DataPoint(i, size - i);
            point.id = i;
            test.add(point);
        }
        return test;
    }

    public static void print_tree(RangeTreeNode root) {
        if (root.left != null) {
            print_tree(root.left);
        }

        System.out.println(root.point);

        if (root.right != null) {
            print_tree(root.right);
        }

        // print_WBBSTree(root.subTree.root);


        
    }

    public static void print_WBBSTree(WBBSTNode root) {
        if (root.left != null) {
            print_WBBSTree(root.left);
        }

        System.out.println(root.point);

        if (root.right != null) {
            print_WBBSTree(root.right);
        }
    }
}