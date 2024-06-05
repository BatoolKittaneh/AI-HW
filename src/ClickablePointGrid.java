import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Stack;

public class ClickablePointGrid extends JFrame {
    private JPanel gridPanel;
    private ArrayList<Point> points;
    private Stack<Point> convexHull;
    private Vehicle[] v = new Vehicle[10];
    private boolean drawLines = false;
    private int capacityValue;
    private int sum = 0;
    private Point depot;
    public ClickablePointGrid() {
        setTitle("Clickable Point Grid");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
        setSize(400, 400);
        setVisible(true);
    }

    private void initComponents() {
        points = new ArrayList<>();
        convexHull = new Stack<>();
        depot = new Point(50, 50,0); // Depot coordinates
        gridPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGrid(g);
                drawPoints(g);
                if (drawLines) {
                    drawConnectingLines(g);
                }
                drawDepot(g);
            }

            private void drawDepot(Graphics g) {
                g.setColor(Color.RED); // Set color for depot point
                g.fillOval(depot.x - 5, depot.y - 5, 10, 10); // Draw a circle for depot point
                g.drawString("Depot", depot.x + 10, depot.y - 5); // Label the depot point
            }

            private void drawGrid(Graphics g) {
                int gridSize = 10;
                g.setColor(Color.LIGHT_GRAY);
                for (int x = 0; x < getWidth(); x += gridSize) {
                    for (int y = 0; y < getHeight(); y += gridSize) {
                        g.drawRect(x, y, gridSize, gridSize);
                    }
                }
            }

            private void drawPoints(Graphics g) {
                for (int i = 0; i < points.size(); i++) {
                    Point p = points.get(i);
                    g.setColor(new Color(0, 0, 0));
                    g.fillOval(p.x, p.y, 10, 10);
                    g.drawString("(" + p.x + ", " + p.y + ")", p.x + 15, p.y - 5);
                }
            }

            private void drawConnectingLines(Graphics g) {
                if (points.size() < 2) return;


                for (int i=0;i<10;i++) {
                    if (v[i] == null || v[i].Points.isEmpty()) break;

                    Color vehicleColor = getRandomColor();
                    g.setColor(vehicleColor);
                    Point firstPoint = v[i].Points.get(0);
                    g.drawLine(depot.x, depot.y, firstPoint.x + 5, firstPoint.y + 5);

                    // Draw lines between subsequent points of the vehicle
                    Point prev = firstPoint;
                    for (Point p : v[i].Points) {
                        g.drawLine(prev.x + 5, prev.y + 5, p.x + 5, p.y + 5);
                        prev = p;
                    }

                    // Draw line from the last point of the vehicle back to depot
                    g.drawLine(prev.x + 5, prev.y + 5, depot.x, depot.y);
                }
            }
            private Color getRandomColor() {
                // Generate random values for red, green, and blue components
                int red = (int) (Math.random() * 256);
                int green = (int) (Math.random() * 256);
                int blue = (int) (Math.random() * 256);

                // Create and return the color
                return new Color(red, green, blue);
            }

        };

        JTextField capacityField = new JTextField(10); // Adjust the size as needed
        capacityField.setToolTipText("Enter capacity value");

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Capacity:"));
        inputPanel.add(capacityField);
        JButton findPathButton = new JButton("Find the Path");
        findPathButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawLines = true; // Set drawLines flag to true
                gridPanel.repaint();
                String capacityText = capacityField.getText();
                if (capacityText.isEmpty()) {
                    System.out.println("Capacity value is empty.");
                    return;
                }

                try {
                    capacityValue = Integer.parseInt(capacityText);
                    for (int i = 0; i < 10; i++) {
                        v[i] = new Vehicle(i);
                        v[i].capacity = capacityValue;
                    }
                    int currentVehicleIndex = 0; // Index to track the current vehicle
                    for (Point p : points) {
                        // If adding the point doesn't exceed the current vehicle's capacity, add it to that vehicle
                        if (v[currentVehicleIndex].getWeight() + p.weight <= v[currentVehicleIndex].capacity) {
                            v[currentVehicleIndex].addPoint(p);
                        } else {
                            // If adding the point exceeds the capacity, move to the next vehicle
                            currentVehicleIndex++;
                            // If there are no more vehicles, exit the loop
                            if (currentVehicleIndex >= v.length) {
                                break;
                            }
                            // Add the point to the next vehicle
                            v[currentVehicleIndex].addPoint(p);
                        }
                    }

                } catch (NumberFormatException ex) {
                    System.out.println("Invalid capacity value: " + capacityText);
                    ex.printStackTrace();
                }

            }
        });

        JButton optimizePathButton = new JButton("Optimize the Path");
        optimizePathButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                optimizePath();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(findPathButton);
        buttonPanel.add(optimizePathButton);

        gridPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                int weight = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter weight for this point:", "Point Weight", JOptionPane.PLAIN_MESSAGE));

                Point point = new Point(x, y, weight);
                points.add(point);
                gridPanel.repaint();
            }
        });

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(inputPanel, BorderLayout.NORTH);
        getContentPane().add(gridPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    private void optimizePath() {
        if (points.size() < 2) {
            System.out.println("Not enough points to optimize the path.");
            return;
        }

        boolean[] visited = new boolean[points.size()];
        Point currentPoint = points.get(0);
        visited[0] = true;
        ArrayList<Point> path = new ArrayList<>();
        path.add(currentPoint);

        double totalDistance = 0;

        for (int i = 1; i < points.size(); i++) {
            double shortestDistance = Double.MAX_VALUE;
            Point nextPoint = null;
            int nextIndex = -1;

            // Find the closest unvisited point
            for (int j = 0; j < points.size(); j++) {
                if (!visited[j]) {
                    double distance = calculateDistance(currentPoint, points.get(j));
                    if (distance < shortestDistance) {
                        shortestDistance = distance;
                        nextPoint = points.get(j);
                        nextIndex = j;
                    }
                }
            }

            // Update the current point to the closest unvisited point
            if (nextPoint != null) {
                visited[nextIndex] = true;
                path.add(nextPoint);
                currentPoint = nextPoint;
                totalDistance += shortestDistance;
            }
        }

        // Draw the path in green
        drawOptimizedPath(path);

        // Print the total distance
        System.out.println("Total shortest distance: " + totalDistance);
    }

    private void drawOptimizedPath(ArrayList<Point> path) {
        Graphics g = gridPanel.getGraphics();
        g.setColor(Color.GREEN);

        Point prevPoint = path.get(0);
        for (int i = 1; i < path.size(); i++) {
            Point currentPoint = path.get(i);
            g.drawLine(prevPoint.x + 5, prevPoint.y + 5, currentPoint.x + 5, currentPoint.y + 5);
            prevPoint = currentPoint;
        }
    }

    private double calculateDistance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2));
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClickablePointGrid::new);

    }
}
