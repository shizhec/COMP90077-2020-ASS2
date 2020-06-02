import java.lang.System;
import java.util.ArrayList;

public class Main{
    private static final int M = 1000000;

    public static void main(String[] args) {
        // run construction efficiency experiment
        construction_exp();
        query_exp1();
        query_exp2();
    }

    // Perform Constuction efficiency experiments on two
    // different constucting method of the Original Range tree
    private static void construction_exp() {
        DataPointGenerator dg = new DataPointGenerator();

        for (int i=1; i<=10; i++) {
            System.out.println("==========================Experiment Start==========================");
            // generate the required datapoints set
            int amount = (int)Math.pow(2, i) * 1000;
            long total_time_naive = 0;
            long total_time_smart = 0;
            long start;
            long end;
            for (int j=0; j<10; j++) {
                // generate points set
                ArrayList<DataPoint> points = dg.generate_points_set(amount);

                // Construct RangeTreeOrg in naive way
                RangeTreeOrg rangeTreeOrg_naive = new RangeTreeOrg();
                start = System.currentTimeMillis();
                rangeTreeOrg_naive.construct_naive(points);
                end = System.currentTimeMillis();
                total_time_naive += (end - start);

                // Construct RangeTreeOrg in smart way
                RangeTreeOrg rangeTreeOrg_smart = new RangeTreeOrg();
                start = System.currentTimeMillis();
                rangeTreeOrg_smart.construct_sorted(points);
                end = System.currentTimeMillis();
                total_time_smart += (end - start);
            }
            long average_time_naive = (long)((float)total_time_naive / (float)10);
            long average_time_smart = (long)((float)total_time_smart / (float)10);

            
            System.out.println("Construct Naive Tree average in "+ average_time_naive + " milliseconds with i="+i+", total "+ amount+ " of points");
            System.out.println("Construct Smart Tree average in "+ average_time_smart + " milliseconds with i="+i+", total "+ amount+ " of points");
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