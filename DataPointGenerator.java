import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class DataPointGenerator {
    private static final int M = 1000000;

    public DataPoint generate_a_point(int coord_min, int coord_max) {
        int x_rand = ThreadLocalRandom.current().nextInt(coord_min, coord_max + 1);
        int y_rand = ThreadLocalRandom.current().nextInt(coord_min, coord_max + 1);

        return new DataPoint(x_rand, y_rand);
    }

    public ArrayList<DataPoint> generate_points_set(int n) {
        ArrayList<DataPoint> points_set= new ArrayList<>();

        for (int i=1; i<=n; i++) {
            DataPoint point = generate_a_point(1, M);
            point.id = i;
            points_set.add(point);
        }

        return points_set;
    }
}