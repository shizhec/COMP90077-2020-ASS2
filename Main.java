import java.lang.System;
import java.util.ArrayList;

public class Main{
    public static void main(String[] args) {
        ArrayList<DataPoint> testPoints = Test.generate_test_Points(10);

        RangeTreeOrg rangeTreeOrg = new RangeTreeOrg();
        // RangeTreeNode root = rangeTreeOrg.construct_naive(testPoints);
        RangeTreeNode root = rangeTreeOrg.construct_sorted(testPoints);

        Test.print_tree(root);
    }
}