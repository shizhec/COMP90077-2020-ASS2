public class DataPoint {
    public int id;
    public int x, y;
    

    public DataPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "id: "+id+" ("+x+", "+y+")";
    }
    
}