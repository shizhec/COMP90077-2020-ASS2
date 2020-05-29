import java.lang.System;
import java.util.ArrayList;

public class Main{
    public static void main(String[] args) {
        ArrayList<DataPoint> testPoints = Test.generate_test_Points(10);
        QueryGenerator qg = new QueryGenerator();
        RangeTreeOrg rangeTreeOrg = new RangeTreeOrg();
        // RangeTreeNode root = rangeTreeOrg.construct_naive(testPoints);
        RangeTreeNode root = rangeTreeOrg.construct_sorted(testPoints);

        QueryGenerator.Square range = qg.generate_test_square();
        
        ArrayList<DataPoint> points = rangeTreeOrg.query2d(range);
        for (DataPoint dataPoint : points) {
            System.out.println(dataPoint);
        }
    }
}