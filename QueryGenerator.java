

public class QueryGenerator {
    private static final int M = 1000000;

    public Square generate_a_query(int s) {
        DataPointGenerator dpg = new DataPointGenerator();
        DataPoint q = dpg.generate_a_point(1, M-s);

        return new Square(new IntRange(q.x, q.x + s), new IntRange(q.y, q.y + s));
    }

    public class Square {
        public IntRange x_range;
        public IntRange y_range;

        private Square(IntRange x_range, IntRange y_range) {
            this.x_range = x_range;
            this.y_range = y_range;
        }
    }

    public class IntRange {
        public int lower;
        public int upper;

        private IntRange(int lower, int upper) {
            this.lower = lower;
            this.upper = upper;
        }
    }

    public void printSquare(Square sq) {
        System.out.println("x range: " + sq.x_range.lower + "-" + sq.x_range.upper);
        System.out.println("y range: " + sq.y_range.lower + "-" + sq.y_range.upper);
    }
}