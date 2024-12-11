import common.Constants;
import common.Direction;
import common.Position;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;

public class GamePanel extends JPanel implements ActionListener {
    private final int startingBodyPartsAmount = 6;
    private final List<Position> snakeBodyPositions = new ArrayList<>();
    private Position applePosition = new Position(0, 0);
    private Direction snakeDirection = Direction.RIGHT;
    private long score = 0;
    boolean running = false;
    Timer timer;
    Random random;

    public GamePanel() {
        random = new Random();

        for (int i = startingBodyPartsAmount; i > 0; i--) {
            snakeBodyPositions.add(new Position(Constants.UNIT_SIZE * i, 0));
        }

        this.setPreferredSize(new Dimension(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT));
        this.setBackground(Color.green);
        this.setFocusable(true);
        this.addKeyListener(new SnakeKeyAdapter());
        startGame();
    }

    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(Constants.DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        draw(graphics);
    }

    public void draw(Graphics graphics) {
        if (!running) {
            gameOver(graphics);
            return;
        }

        // Draw the apple
        graphics.setColor(Color.red);
        graphics.fillOval(applePosition.getX(), applePosition.getY(), Constants.UNIT_SIZE, Constants.UNIT_SIZE);

        // Draw the snake body parts
        for (final Position snakeBodyPart : snakeBodyPositions) {
            // the head of the snake
            if (snakeBodyPart.equals(snakeBodyPositions.get(0))) {
                graphics.setColor(Color.cyan);
            } else {
                graphics.setColor(Color.blue);
            }

            graphics.fillRect(snakeBodyPart.getX(), snakeBodyPart.getY(), Constants.UNIT_SIZE, Constants.UNIT_SIZE);
        }

        // Draw the score
        graphics.setColor(Color.black);
        graphics.setFont(new Font("Arial", Font.BOLD, 56));

        final FontMetrics metrics = getFontMetrics(graphics.getFont());
        graphics.drawString(
                "Score: " + score,
                (Constants.SCREEN_WIDTH - metrics.stringWidth("Score: " + score)) / 2,
                graphics.getFont().getSize()
        );
    }


    public void move() {
        for (int i = snakeBodyPositions.size() - 1; i > 0; i--) {
            snakeBodyPositions.set(i, snakeBodyPositions.get(i - 1));
        }

        Position head = snakeBodyPositions.get(0);
        Position newHead = new Position(head.getX(), head.getY());

        switch (snakeDirection) {
            case UP -> newHead.setY(head.getY() - Constants.UNIT_SIZE);
            case DOWN -> newHead.setY(head.getY() + Constants.UNIT_SIZE);
            case RIGHT -> newHead.setX(head.getX() + Constants.UNIT_SIZE);
            case LEFT -> newHead.setX(head.getX() - Constants.UNIT_SIZE);
        }

        snakeBodyPositions.set(0, newHead);
    }

    public void newApple() {
        Position position;
        do {
            position = getRandomPosition();
        } while (snakeBodyPositions.contains(position));

        applePosition = position;
    }

    public void checkApple() {
        if (snakeBodyPositions.get(0).equals(applePosition)) {
            snakeBodyPositions.add(new Position(-Constants.UNIT_SIZE, -Constants.UNIT_SIZE));
            score++;
            score *= random.nextInt(1, 10);
            newApple();
        }
    }

    public void checkCollision() {
        // Check head collision with body
        for (final Position position : snakeBodyPositions.subList(1, snakeBodyPositions.size())) {
            if (position.equals(snakeBodyPositions.get(0))) {
                running = false;
                break;
            }
        }

        final Position head = snakeBodyPositions.get(0);

        // Check head collision with left border
        if (head.getX() < 0) {
            running = false;
        }

        // Check head collision with right border
        if (head.getX() > Constants.SCREEN_WIDTH - Constants.UNIT_SIZE) {
            running = false;
        }

        // Check head collision with top border
        if (head.getY() < 0) {
            running = false;
        }

        // Check head collision with bottom border
        if (head.getY() > Constants.SCREEN_HEIGHT - Constants.UNIT_SIZE) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    public void gameOver(Graphics graphics) {
        graphics.setColor(Color.black);
        graphics.setFont(new Font("Arial", Font.BOLD, 56));

        final FontMetrics metrics = getFontMetrics(graphics.getFont());
        graphics.drawString(
                "Game Over :(",
                (Constants.SCREEN_WIDTH - metrics.stringWidth("Game Over :(")) / 2,
                Constants.SCREEN_HEIGHT / 2
        );
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollision();
        }

        repaint();
    }

    public class SnakeKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent event) {
            switch (event.getKeyCode()) {
                case KeyEvent.VK_LEFT: if (snakeDirection != Direction.RIGHT)
                    snakeDirection = Direction.LEFT;
                break;
                case KeyEvent.VK_UP: if (snakeDirection != Direction.DOWN)
                    snakeDirection = Direction.UP;
                break;
                case KeyEvent.VK_RIGHT: if (snakeDirection != Direction.LEFT)
                    snakeDirection = Direction.RIGHT;
                break;
                case KeyEvent.VK_DOWN: if (snakeDirection != Direction.UP)
                    snakeDirection = Direction.DOWN;
                break;
            }
        }
    }

    private Position getRandomPosition() {
        return new Position(
            random.nextInt(Constants.SCREEN_WIDTH / Constants.UNIT_SIZE) * Constants.UNIT_SIZE,
            random.nextInt(Constants.SCREEN_HEIGHT / Constants.UNIT_SIZE) * Constants.UNIT_SIZE
        );
    }
}
