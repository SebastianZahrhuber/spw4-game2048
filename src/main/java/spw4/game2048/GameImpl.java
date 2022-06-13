package spw4.game2048;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public final class GameImpl implements Game {

    public static Random random;

    private final int SIZE = 4;
    private int[][] gameBoard = new int[SIZE][SIZE];
    private int tilesCount = 0;

    private int moves = 0;
    private int score = 0;

    public GameImpl() {
        random = new Random(1234);
    }

    GameImpl(int[][] gameBoard) {
        this();
        this.gameBoard = gameBoard;
        for (int y = 0; y < gameBoard.length; y++) {
            for (int x = 0; x < gameBoard.length; x++) {
                if (gameBoard[x][y] != 0) tilesCount++;
            }
        }
    }

    public void initialize() {
        for (int i = 0; i < 2; i++) {
            generateNewValue();
        }
    }

    private void generateNewValue() {
        int x, y;
        do {
            x = random.nextInt(SIZE);
            y = random.nextInt(SIZE);
        } while (gameBoard[x][y] != 0);

        gameBoard[x][y] = random.nextInt(10) == 9 ? 4 : 2;
        tilesCount++;
    }

    public void move(Direction direction) {
        switch (direction) {
            case left -> moveLeft();
            case right -> moveRight();
            case up -> moveUp();
            case down -> moveDown();
        }

        if (!isOver()) {
            generateNewValue();
        }
        moves++;
    }

    private void moveLeft() {
        List<Point> mergedPositions = new ArrayList<>();
        for (int y = 0; y < SIZE; y++) {
            int counter = 0;
            for (int x = 0; x < SIZE; x++) {
                if (gameBoard[x][y] != 0) {
                    if (counter > 0 && gameBoard[counter - 1][y] == gameBoard[x][y] &&
                            !mergedPositions.contains(new Point(counter - 1, y))) {
                        gameBoard[counter - 1][y] *= 2;
                        gameBoard[x][y] = 0;
                        score += gameBoard[counter - 1][y];
                        tilesCount--;
                        mergedPositions.add(new Point(counter - 1, y));
                    } else {
                        gameBoard[counter][y] = gameBoard[x][y];
                        if (counter != x) {
                            gameBoard[x][y] = 0;
                        }

                        counter++;
                    }
                }
            }
        }
    }

    private void moveRight() {
        List<Point> mergedPositions = new ArrayList<>();
        for (int y = SIZE - 1; y >= 0; y--) {
            int counter = SIZE - 1;
            for (int x = SIZE - 1; x >= 0; x--) {
                if (gameBoard[x][y] != 0) {
                    if (counter < SIZE - 1 && gameBoard[counter + 1][y] == gameBoard[x][y] &&
                            !mergedPositions.contains(new Point(counter + 1, y))) {
                        gameBoard[counter + 1][y] *= 2;
                        gameBoard[x][y] = 0;
                        score += gameBoard[counter + 1][y];
                        tilesCount--;
                        mergedPositions.add(new Point(counter + 1, y));
                    } else {
                        gameBoard[counter][y] = gameBoard[x][y];
                        if (counter != x) {
                            gameBoard[x][y] = 0;
                        }
                        counter--;
                    }
                }
            }
        }
    }

    private void moveUp() {
        List<Point> mergedPositions = new ArrayList<>();
        for (int x = 0; x < SIZE; x++) {
            int counter = 0;
            for (int y = 0; y < SIZE; y++) {
                if (gameBoard[x][y] != 0) {
                    if (counter > 0 && gameBoard[x][counter - 1] == gameBoard[x][y] &&
                            !mergedPositions.contains(new Point(x, counter - 1))) {
                        gameBoard[x][counter - 1] *= 2;
                        gameBoard[x][y] = 0;
                        score += gameBoard[x][counter - 1];
                        tilesCount--;
                        mergedPositions.add(new Point(x, counter - 1));
                    } else {
                        gameBoard[x][counter] = gameBoard[x][y];
                        if (counter != y) {
                            gameBoard[x][y] = 0;
                        }
                        counter++;
                    }
                }
            }
        }
    }

    private void moveDown() {
        List<Point> mergedPositions = new ArrayList<>();
        for (int x = SIZE - 1; x >= 0; x--) {
            int counter = SIZE - 1;
            for (int y = SIZE - 1; y >= 0; y--) {
                if (gameBoard[x][y] != 0) {
                    if (counter < SIZE - 1 && gameBoard[x][counter + 1] == gameBoard[x][y] &&
                            !mergedPositions.contains(new Point(x, counter + 1))) {
                        gameBoard[x][counter + 1] *= 2;
                        gameBoard[x][y] = 0;
                        score += gameBoard[x][counter + 1];
                        tilesCount--;
                        mergedPositions.add(new Point(x, counter + 1));
                    } else {
                        gameBoard[x][counter] = gameBoard[x][y];
                        if (counter != y) {
                            gameBoard[x][y] = 0;
                        }
                        counter--;
                    }
                }
            }
        }
    }

    public int getMoves() {
        return moves;
    }

    public int getScore() {
        return score;
    }

    public int getValueAt(int x, int y) {
        if (x < 0 || x >= SIZE) {
            throw new IllegalArgumentException("x is invalid");
        }
        if (y < 0 || y >= SIZE) {
            throw new IllegalArgumentException("y is invalid");
        }

        return gameBoard[x][y];
    }

    public boolean isOver() {
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if (gameBoard[x][y] == 0) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean isWon() {
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if (gameBoard[x][y] == 2048) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Moves: ");
        sb.append(moves);
        sb.append("\t\tScore: ");
        sb.append(score);
        sb.append("\n");
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if (gameBoard[x][y] == 0) {
                    sb.append(".\t\t");
                } else {
                    sb.append(gameBoard[x][y]);
                    sb.append("\t\t");
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    int getTilesCount() {
        return tilesCount;
    }

    int[][] getGameBoard() {
        return gameBoard;
    }
}

