import java.lang.System;
import java.util.ArrayList;

public class Main{
    private static final int M = 1000000;

    public static void main(String[] args) {
        // run construction efficiency experiment
        // construction_exp();
        // query_exp1();
        query_exp2();

        // ArrayList<DataPoint> test = Test.generate_test_Points(30);

        // RangeTreeOrg rangeTreeOrg_naive = new RangeTreeOrg();
        // rangeTreeOrg_naive.construct_naive(test);

        // RangeTreeOrg rangeTreeOrg_smart = new RangeTreeOrg();
        // rangeTreeOrg_smart.construct_sorted(test);

        // RangeTreeFC rangeTreeFC = new RangeTreeFC();
        // rangeTreeFC.construct_FC(test);

        // QueryGenerator qg = new QueryGenerator();
        // QueryGenerator.Square range = qg.generate_test_square();
        // // ArrayList<DataPoint> points = rangeTreeOrg_naive.query2d(range);
        // // ArrayList<DataPoint> points = rangeTreeOrg_smart.query2d(range);
        // ArrayList<DataPoint> points = rangeTreeFC.query2d(range);
        // for (DataPoint dataPoint : points) {
        //     System.out.println(dataPoint);
        // }

        // QueryGenerator.Square query = qg.generate_a_query((int)(100000));
        // ArrayList<DataPoint> org = rangeTreeOrg.query2d(query);
        // ArrayList<DataPoint> fc = rangeTreeFC.query2d(query);

        // for (DataPoint dataPoint : org) {
        //     if (!fc.contains(dataPoint)) {
        //         System.out.println("un_match: "+dataPoint);
        //     }
        // }
        // System.out.println(org.size());
        // System.out.println(fc.size());
    }

    // Perform Constuction efficiency experiments on two
    // different constucting method of the Original Range tree
    private static void construction_exp() {
        // define Percentages
        Double[] percentages = {0.01, 0.05, 0.1, 0.2, 0.5, 0.8, 1.0};
        DataPointGenerator dg = new DataPointGenerator();

        for (Double percent : percentages) {
            System.out.println("==========================Experiment Start==========================");
            // generate the required datapoints set
            int amount = (int)(percent * M);
            ArrayList<DataPoint> points = dg.generate_points_set(amount);

            // Construct RangeTreeOrg in naive way
            RangeTreeOrg rangeTreeOrg_naive = new RangeTreeOrg();
            long start = System.currentTimeMillis();
            rangeTreeOrg_naive.construct_naive(points);
            long end = System.currentTimeMillis();
            System.out.println("Construct Naive Tree in "+ (end-start) + " milliseconds with "+percent+" percent of points");

            // Construct RangeTreeOrg in smart way
            RangeTreeOrg rangeTreeOrg_smart = new RangeTreeOrg();
            start = System.currentTimeMillis();
            rangeTreeOrg_smart.construct_sorted(points);
            end = System.currentTimeMillis();
            System.out.println("Construct Smart Tree in "+ (end-start) + " milliseconds with "+percent+" percent of points");
        }
    }

    // Perform Query efficiency experiments on RangeTreeFC and RangeTreeOrg
    // fixed n, verying s
    private static void query_exp1() {
        // generate data points set
        DataPointGenerator dg = new DataPointGenerator();
        ArrayList<DataPoint> points = dg.generate_points_set(M);
        
        // Construct FC tree and Org tree
        RangeTreeOrg rangeTreeOrg = new RangeTreeOrg();
        rangeTreeOrg.construct_sorted(points);
        RangeTreeFC rangeTreeFC = new RangeTreeFC();
        rangeTreeFC.construct_FC(points);

        QueryGenerator qg = new QueryGenerator();

        // define percentages of s
        Double[] s_percent = {0.01, 0.02, 0.05, 0.1, 0.2};
        // Start Experiments
        for (Double percent : s_percent) {
            System.out.println("==========================Experiment Start==========================");
            long total_timestep_org = 0;
            long total_timestep_fc = 0;
            // generate 100 querys
            for (int i = 0; i < 100; i++) {
                // generate a query
                QueryGenerator.Square query = qg.generate_a_query((int)(percent*M));

                // query org
                long start = System.nanoTime();
                rangeTreeOrg.query2d(query);
                long end = System.nanoTime();
                total_timestep_org += (end - start);

                start = System.nanoTime();
                rangeTreeFC.query2d(query);
                end = System.nanoTime();
                total_timestep_fc += (end - start);
            }
            long final_time_org = (long)((float)total_timestep_org / (float)100);
            long final_time_fc = (long)((float)total_timestep_fc / (float)100);
            // display average time
            System.out.println("Original Range Tree average query time: "+final_time_org+ " with "+ percent + " percent");
            System.out.println("FC Range Tree average query time:       "+final_time_fc+ " with "+ percent + " percent");
        }
    }

    // fixed s, very n
    private static void query_exp2() {
        // generate workload
        QueryGenerator qg = new QueryGenerator();
        ArrayList<QueryGenerator.Square> workload = new ArrayList<>();
        for (int i=0; i < 100; i++) {
            QueryGenerator.Square query = qg.generate_a_query((int)(0.05*M));
            workload.add(query);
        }
        // generate 10 points set, perform experiments
        DataPointGenerator dg = new DataPointGenerator();
        for (int i=1; i<=10; i++) {
            System.out.println("==========================Experiment Start==========================");
            int n = (int)Math.pow(2, i) * 1000;
            ArrayList<DataPoint> points_set = dg.generate_points_set(n);

            // Construct rangetreeOrg and rangetreeFC on the same point set
            RangeTreeOrg rangeTreeOrg = new RangeTreeOrg();
            rangeTreeOrg.construct_sorted(points_set);

            RangeTreeFC rangeTreeFC = new RangeTreeFC();
            rangeTreeFC.construct_FC(points_set);

            long total_timestep_org = 0;
            long total_timestep_fc = 0;
            for (QueryGenerator.Square query : workload) {
                long start = System.nanoTime();
                rangeTreeOrg.query2d(query);
                long end = System.nanoTime();
                total_timestep_org += (end - start);

                start = System.nanoTime();
                rangeTreeFC.query2d(query);
                end = System.nanoTime();
                total_timestep_fc += (end - start);
            }
            long final_time_org = (long)((float)total_timestep_org / (float)100);
            long final_time_fc = (long)((float)total_timestep_fc / (float)100);
            // display average time
            System.out.println("Original Range Tree average query time: "+final_time_org+ " with n="+ n + " points");
            System.out.println("FC Range Tree average query time:       "+final_time_fc+ " with n="+ n + " points");
        }
    }
}