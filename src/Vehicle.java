import java.util.ArrayList;
import java.util.List;

public class Vehicle {
  public int id;
  public int capacity ;
  public ArrayList<Point> Points = new ArrayList<>();
  public Vehicle(int id){
      this.id=id;
  }
  public  void addPoint(Point p){
      this.Points.add(p);
  }
    public int getWeight() {
        int weight = 0;
        for (Point p : Points) {
            weight += p.weight;
        }
        return weight;
    }
}
