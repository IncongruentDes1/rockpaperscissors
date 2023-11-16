import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class rockpaperscissors extends JFrame {
    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 600;
    private static final int SHAPE_SIZE = 30;
    private static final int SPEED = 2;

    private List<Shape> shapes;
    

    public rockpaperscissors() {
        setTitle("Rock Paper Scissors");
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        shapes = new ArrayList<>();
        int numberOfShapes = promptNumberOfShapes();
        for (int i = 0; i < numberOfShapes; i++) {
            shapes.add(new Shape(getRandomCoordinate(FRAME_WIDTH - (SHAPE_SIZE*2) ), getRandomCoordinate(FRAME_HEIGHT-(SHAPE_SIZE*2)), 0));
            shapes.add(new Shape(getRandomCoordinate(FRAME_WIDTH- (SHAPE_SIZE*2)), getRandomCoordinate(FRAME_HEIGHT-(SHAPE_SIZE*2)), 1));
            shapes.add(new Shape(getRandomCoordinate(FRAME_WIDTH- (SHAPE_SIZE*2)), getRandomCoordinate(FRAME_HEIGHT-(SHAPE_SIZE*2)), 2));
        }

        ShapesComponent component = new ShapesComponent(shapes);
        add(component);

        Timer timer = new Timer(10, e -> {
            for (Shape shape : shapes) {
                shape.move();
                shape.checkCollision(shapes);
            }
            component.repaint();
        });
        timer.start();
    }

    private int promptNumberOfShapes() {
        String input = JOptionPane.showInputDialog("Enter the number of shapes of each type:");
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Defaulting to 5 shapes.");
            return 5;
        }
    }

    private int getRandomCoordinate(int max) {
        Random random = new Random();
        return random.nextInt(max);
    }

    private int getRandomShapeType() {
        Random random = new Random();
        return random.nextInt(3); // 0: rectangle, 1: circle, 2: scissors
    }

    private static class ShapesComponent extends JComponent {
        private List<Shape> shapes;
        private BufferedImage scissorsImage; // New member to hold the scissors image
        private BufferedImage rockImage;
        private BufferedImage paperImage;

        public ShapesComponent(List<Shape> shapes) {
            this.shapes = shapes;
            try {
            	rockImage = ImageIO.read(new File("rock2.png")); 
            	paperImage = ImageIO.read(new File("paper.png")); 
                scissorsImage = ImageIO.read(new File("scissors-transparent-scissors-free-png.png")); // Load the scissors image
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            for (Shape shape : shapes) {
                switch (shape.getType()) {
                    case 0:
                    	g.drawImage(paperImage, shape.getX(), shape.getY(), SHAPE_SIZE, SHAPE_SIZE, null);
                        break;
                    case 1:
                    	g.drawImage(rockImage, shape.getX(), shape.getY(), SHAPE_SIZE, SHAPE_SIZE, null);
                        break;
                    case 2:
                        // Draw the scissors image instead of the square
                        g.drawImage(scissorsImage, shape.getX(), shape.getY(), SHAPE_SIZE, SHAPE_SIZE, null);
                        break;
                }
            }
        }
    }

    // start of shape
    private static class Shape {
        private int x, y;
        private Color color;
        private int dx, dy;
        private int type; // 0: rectangle, 1: circle, 2: scissors

        public Shape(int x, int y, int type) {
            this.x = x;
            this.y = y;
            this.type = type;
            this.color = getRandomColor();
            this.dx = getRandomSpeed();
            this.dy = getRandomSpeed();
        }

        private Color getRandomColor() {
            Random random = new Random();
            return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        }

        private int getRandomSpeed() {
            Random random = new Random();
            return SPEED * (random.nextBoolean() ? 1 : -1);
        }

        public void move() {
            x += dx;
            y += dy;
            // - fix square issues
            if (x < 0 || x >= (FRAME_WIDTH - (SHAPE_SIZE * 1.25))) {
                dx = -dx;
            }
            if (y < 0 || y >= (FRAME_HEIGHT - (SHAPE_SIZE * 2))) {
                dy = -dy;
            }
        }

        public void checkCollision(List<Shape> shapes) {
            for (Shape other : shapes) {
                if (other != this && intersects(other)) {
                    if (this.type == 0 && other.type == 2) {
                        this.type = 2; // Rectangle -> Scissors
                    } else if (this.type == 1 && other.type == 0) {
                        this.type = 0; // Circle -> Rectangle
                    } else if (this.type == 2 && other.type == 1) {
                        this.type = 1; // Scissors -> Circle
                    }
                }
            }
        }

        private boolean intersects(Shape other) {
            return this.getBounds().intersects(other.getBounds());
        }

        private Rectangle getBounds() {
            return new Rectangle(x, y, SHAPE_SIZE, SHAPE_SIZE);
        }

        public Color getColor() {
            return color;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getType() {
            return type;
        }
    }

    // end of shape

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new rockpaperscissors().setVisible(true));
    }
}
