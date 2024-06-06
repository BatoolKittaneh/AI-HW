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
        getContentPane().setBackground(new Color(95, 158, 160));
        initComponents();
        getContentPane().setLayout(new FlowLayout());
        setSize(700, 650); // Set the size of the JFrame to 800x800 pixels
        setVisible(true);
    }
    private void initComponents() {
        points = new ArrayList<>();
        convexHull = new Stack<>();
        depot = new Point(50, 50, 0); // Depot coordinates
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

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(500, 500); // Set the preferred size of the panel
            }

            private void drawDepot(Graphics g) {
                g.setColor(Color.RED); // Set color for depot point
                g.fillOval(depot.x - 5, depot.y - 5, 10, 10); // Draw a circle for depot point
                g.drawString("Depot", depot.x + 10, depot.y - 5); // Label the depot point
            }

            private void drawGrid(Graphics g) {
                int gridSize = 20; // Increase the grid size
                g.setColor(new Color(95, 158, 160)); // Set the color to RGB(95, 158, 160)
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

                for (int i = 0; i < 10; i++) {
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
        Font fieldFont = new Font("Arial", Font.BOLD, 20); // Example: Arial, bold, size 16
        capacityField.setFont(fieldFont);

        JPanel inputPanel = new JPanel();
        inputPanel.setBackground(new Color(246,182,182));
        JLabel capacityLabel = new JLabel("Capacity:");
        inputPanel.add(capacityLabel);
        Font labelFont = new Font("Arial", Font.BOLD, 20); // Example: Arial, bold, size 16
       capacityLabel.setFont(labelFont);
        inputPanel.add(capacityField);
        JButton findPathButton = new JButton("Find the Path");
// Set font for findPathButton
        Font buttonFont = new Font("Arial", Font.BOLD, 20); // Example: Arial, bold, size 16
        findPathButton.setFont(buttonFont);
// Set size for findPathButton
        findPathButton.setPreferredSize(new Dimension(220, 50)); // Example: width 200, height 50
// Set background color for findPathButton
        findPathButton.setBackground(new Color(246,182,182)); // RGB(240, 128, 128)

        JButton optimizePathButton = new JButton("Optimize the Path");
// Set font for optimizePathButton
        optimizePathButton.setFont(buttonFont);
// Set size for optimizePathButton
        optimizePathButton.setPreferredSize(new Dimension(220, 50)); // Example: width 200, height 50
// Set background color for optimizePathButton
        optimizePathButton.setBackground(new Color(246,182,182)); // RGB(240, 128, 128)
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


        optimizePathButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                optimizePath();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(95, 158, 160));
        buttonPanel.add(findPathButton);
        buttonPanel.add(optimizePathButton);

        gridPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String capacityText = capacityField.getText();
                if (capacityText.isEmpty()) {
                    // Capacity not set, prompt user to set capacity first
                    JOptionPane.showMessageDialog(null, "Please enter the capacity before adding points.", "Capacity Not Set", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    capacityValue = Integer.parseInt(capacityText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid capacity value: " + capacityText, "Invalid Capacity", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int x = e.getX();
                int y = e.getY();
                int weight=0;
                boolean validWeight = false;

                // Keep prompting the user until a valid weight is entered
                while (!validWeight) {
                    String weightInput = JOptionPane.showInputDialog(null, "Enter weight for this point:", "Point Weight", JOptionPane.PLAIN_MESSAGE);
                    if (weightInput == null) {
                        // User clicked cancel, exit the method
                        return;
                    }
                    try {
                        weight = Integer.parseInt(weightInput);
                        if (weight <= capacityValue) {
                            validWeight = true;
                        } else {
                            JOptionPane.showMessageDialog(null, "Weight cannot exceed the capacity (" + capacityValue + "). Please enter a new weight.", "Invalid Weight", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid integer.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    }
                }

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

    public void optimizePath() {
        if (points.size() < 2) {
            System.out.println("Not enough points to optimize the path.");
            return;
        }


                    }
                }

                // Update current point and path
                if (nextPoint != null) {
                    visited[nextIndex] = true;
                    path.add(nextPoint);
                    currentPointIndex = nextIndex;
                }
            }


            }

            // Draw the optimized path with a starting and ending green line segment
            Graphics g = gridPanel.getGraphics();
            g.setColor(Color.GREEN);
            Point firstPoint = path.get(0);
            Point lastPoint = path.get(path.size() - 1);
            g.drawLine(depot.x + 5, depot.y + 5, firstPoint.x + 5, firstPoint.y + 5);
            drawOptimizedPath(path); // Draw remaining path segments in green
            g.drawLine(lastPoint.x + 5, lastPoint.y + 5, depot.x + 5, depot.y + 5);

            // Print the total distance for each vehicle
            double totalDistance = calculateTotalDistance(path);
            System.out.println("Total shortest distance for Vehicle " + i + ": " + totalDistance);
        }
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