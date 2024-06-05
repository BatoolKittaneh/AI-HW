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
            int b;
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

                // Draw line from depot to the first point
                Point firstPoint = points.get(0);
                g.drawLine(depot.x, depot.y, firstPoint.x + 5, firstPoint.y + 5);

                // Draw lines between subsequent points
                Point prev = firstPoint;
                for (int i = 1; i < points.size(); i++) {
                    Point p = points.get(i);
                    g.drawLine(prev.x + 5, prev.y + 5, p.x + 5, p.y + 5);
                    prev = p;
                }
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
                    int s = 0;
                    for (Point p : points) {
                        if (sum <= v[s].capacity) {
                            sum += p.weight;
                            v[s].addPoint(p);
                        } else {
                            sum = p.weight;
                            s++;
                            v[s].addPoint(p);
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
        // Your optimization logic here
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClickablePointGrid::new);

    }
}
