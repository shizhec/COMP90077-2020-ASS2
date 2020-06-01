import java.util.ArrayList;

public class Test {
    public static ArrayList<DataPoint> generate_test_Points(int size) {
        ArrayList<DataPoint> test = new ArrayList<>();
        size += 1;
        for (int i=1; i < size; i++) {
            DataPoint point = new DataPoint(i, i);
            point.id = i;
            test.add(point);
            i += 1;
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

    public static void print_FCarray(ArrayList<DataPointFC> points) {
        for (DataPointFC dataPointFC : points) {
            System.out.println(dataPointFC.point);
            if (dataPointFC.left_successor != null) {
                System.out.println("left pointer to: " + dataPointFC.left_successor.point);
            }
            if (dataPointFC.right_successor != null) {
                System.out.println("right pointer to: " + dataPointFC.right_successor.point);
            }
        }
    }

    
}