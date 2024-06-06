import java.util.ArrayList;

public class Vehicle {
    public int id;
    public int capacity;
    public ArrayList<Point> Points;

    public Vehicle(int id) {
        this.id = id;
        this.capacity = 0;
        this.Points = new ArrayList<>();
    }

    public int getWeight() {
        int weight = 0;
        for (Point p : Points) {
            weight += p.weight;
        }
        return weight;
    }

    public void addPoint(Point point) {
        this.Points.add(point);
    }

    public void clearPoints() {
        this.Points.clear();
    }
}
