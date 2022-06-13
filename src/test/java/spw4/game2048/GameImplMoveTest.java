package spw4.game2048;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import javax.print.DocFlavor;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

public class GameImplMoveTest {

    private GameImpl sut;

    @BeforeEach
    void beforeEach() {
        sut = new GameImpl();
    }

    @DisplayName("GameImpl.move moves without merge")
    @ParameterizedTest(name = "{2}: multiple per row/column {0}")
    @MethodSource("generateMoveDataWithoutMerge")
    void moveWithoutMerge(boolean multiple, List<Integer> randomValues, Direction direction,
                          List<Integer> firstAssertValues, List<Integer> secondAssertValues) {
        GameImpl.random = new IntRandomStub(randomValues);
        sut.initialize();

        sut.move(direction);

        assertThat(sut.getValueAt(firstAssertValues.get(0), firstAssertValues.get(1)))
                .isEqualTo(firstAssertValues.get(2));
        assertThat(sut.getValueAt(secondAssertValues.get(0), secondAssertValues.get(1)))
                .isEqualTo(secondAssertValues.get(2));
    }

    private static Stream<Arguments> generateMoveDataWithoutMerge() {
        return Stream.of(
                Arguments.of(false, List.of(0, 1, 5, 2, 2, 9), Direction.left,
                        List.of(0, 1, 2), List.of(0, 2, 4)),
                Arguments.of(true, List.of(0, 1, 5, 3, 1, 9), Direction.left,
                        List.of(0, 1, 2), List.of(1, 1, 4)),
                Arguments.of(false, List.of(0, 1, 5, 2, 2, 9), Direction.right,
                        List.of(3, 1, 2), List.of(3, 2, 4)),
                Arguments.of(true, List.of(0, 1, 5, 3, 1, 9), Direction.right,
                        List.of(2, 1, 2), List.of(3, 1, 4)),
                Arguments.of(false, List.of(0, 1, 5, 2, 2, 9), Direction.up,
                        List.of(0, 0, 2), List.of(2, 0, 4)),
                Arguments.of(true, List.of(0, 1, 5, 0, 3, 9), Direction.up,
                        List.of(0, 0, 2), List.of(0, 1, 4)),
                Arguments.of(false, List.of(0, 1, 5, 2, 2, 9), Direction.down,
                        List.of(0, 3, 2), List.of(2, 3, 4)),
                Arguments.of(true, List.of(0, 1, 5, 0, 3, 9), Direction.down,
                        List.of(0, 2, 2), List.of(0, 3, 4)));
    }

    @DisplayName("GameImpl.move generates new value after move")
    @Test
    void moveGeneratesNewValue() {
        GameImpl.random = new IntRandomStub(
                List.of(0, 1, 5, 2, 2, 9, 1, 0, 5, 3, 2, 9));
        sut.initialize();

        sut.move(Direction.down);
        sut.move(Direction.down);

        assertThat(sut.getTilesCount()).isEqualTo(4);
    }

    @DisplayName("GameImpl.move generates new value if target position is empty")
    @Test
    void moveGeneratesNewValueIfPositionIsEmpty() {
        GameImpl.random = new IntRandomStub(
                List.of(0, 1, 5, 2, 2, 9, 1, 0, 5, 1, 0, 5, 3, 2, 9));
        sut.initialize();

        sut.move(Direction.down);
        sut.move(Direction.down);

        assertThat(sut.getTilesCount()).isEqualTo(4);
    }

    @DisplayName("GameImpl.move move with full un-movable gameBoard stays over")
    @Test
    void moveWithFullGameBoardStaysOver() {
        sut = new GameImpl(new int[][] {
                {2, 4, 2, 4},
                {4, 2, 4, 2},
                {2, 4, 2, 4},
                {4, 2, 4, 2}});

        sut.move(Direction.down);
        sut.move(Direction.left);

        assertThat(sut.isOver()).isTrue();
    }

    @DisplayName("GameImpl.move move changes nothing if value is already on correct position")
    @ParameterizedTest(name = "{2} does not move")
    @MethodSource("generateMoveWithPerfectData")
    void moveDoesNotMoveOnCorrectPosition(int x, int y, Direction direction) {
        GameImpl.random = new IntRandomStub(
                List.of(x, y, 5, 2, 2, 2, 1, 1, 5));
        sut.initialize();

        sut.move(direction);

        assertThat(sut.getValueAt(x, y)).isNotEqualTo(0);
    }

    private static Stream<Arguments> generateMoveWithPerfectData() {
        return Stream.of(
                Arguments.of(0, 3, Direction.left),
                Arguments.of(3, 0, Direction.right),
                Arguments.of(0, 0, Direction.up),
                Arguments.of(3, 3, Direction.down));
    }

        @DisplayName("GameImpl.merge moves with merge")
    @ParameterizedTest(name = "{2}: multiple per row/column {0}")
    @MethodSource("generateMoveDataWithMerge")
    void moveWithMerge(boolean multiple, int[][] gameBoard, Direction direction,
                       int[][] assertGameBoard, int tilesCount) {
        sut = new GameImpl(gameBoard);
        GameImpl.random = new IntRandomStub(List.of(3, 3, 2));

        sut.move(direction);

        assertThat(sut.getTilesCount()).isEqualTo(tilesCount);
        for (int y = 0; y < gameBoard.length; y++) {
            for (int x = 0; x < gameBoard.length; x++) {
                if (!(x == 3 && y == 3)) {
                    assertThat(assertGameBoard[x][y])
                            .isEqualTo(sut.getValueAt(x, y));
                }
            }
        }
    }

    private static Stream<Arguments> generateMoveDataWithMerge() {
        return Stream.of(
                Arguments.of(false,
                        new int[][]{
                                {0, 0, 0, 0},
                                {2, 0, 0, 0},
                                {0, 0, 0, 0},
                                {2, 0, 0, 0}},
                        Direction.left,
                        new int[][]{
                                {4, 0, 0, 0},
                                {0, 0, 0, 0},
                                {0, 0, 0, 0},
                                {0, 0, 0, 0}},
                        2),
                Arguments.of(true,
                        new int[][]{
                                {2, 0, 0, 0},
                                {2, 0, 0, 0},
                                {2, 0, 0, 0},
                                {2, 0, 0, 0}},
                        Direction.left,
                        new int[][]{
                                {4, 0, 0, 0},
                                {4, 0, 0, 0},
                                {0, 0, 0, 0},
                                {0, 0, 0, 0}},
                        3),
                Arguments.of(false,
                        new int[][]{
                                {0, 0, 0, 0},
                                {2, 0, 0, 0},
                                {0, 0, 0, 0},
                                {2, 0, 0, 0}},
                        Direction.right,
                        new int[][]{
                                {0, 0, 0, 0},
                                {0, 0, 0, 0},
                                {0, 0, 0, 0},
                                {4, 0, 0, 0}},
                        2),
                Arguments.of(true,
                        new int[][]{
                                {2, 0, 0, 0},
                                {2, 0, 0, 0},
                                {2, 0, 0, 0},
                                {2, 0, 0, 0}},
                        Direction.right,
                        new int[][]{
                                {0, 0, 0, 0},
                                {0, 0, 0, 0},
                                {4, 0, 0, 0},
                                {4, 0, 0, 0}},
                        3),
                Arguments.of(false,
                        new int[][]{
                                {0, 2, 0, 2},
                                {0, 0, 0, 0},
                                {0, 0, 0, 0},
                                {0, 0, 0, 0}},
                        Direction.up,
                        new int[][]{
                                {4, 0, 0, 0},
                                {0, 0, 0, 0},
                                {0, 0, 0, 0},
                                {0, 0, 0, 0}},
                        2),
                Arguments.of(true,
                        new int[][]{
                                {2, 2, 2, 2},
                                {0, 0, 0, 0},
                                {0, 0, 0, 0},
                                {0, 0, 0, 0}},
                        Direction.up,
                        new int[][]{
                                {4, 4, 0, 0},
                                {0, 0, 0, 0},
                                {0, 0, 0, 0},
                                {0, 0, 0, 0}},
                        3),
                Arguments.of(false,
                        new int[][]{
                                {0, 2, 0, 2},
                                {0, 0, 0, 0},
                                {0, 0, 0, 0},
                                {0, 0, 0, 0}},
                        Direction.down,
                        new int[][]{
                                {0, 0, 0, 4},
                                {0, 0, 0, 0},
                                {0, 0, 0, 0},
                                {0, 0, 0, 0}},
                        2),
                Arguments.of(true,
                        new int[][]{
                                {2, 2, 2, 2},
                                {0, 0, 0, 0},
                                {0, 0, 0, 0},
                                {0, 0, 0, 0}},
                        Direction.down,
                        new int[][]{
                                {0, 0, 4, 4},
                                {0, 0, 0, 0},
                                {0, 0, 0, 0},
                                {0, 0, 0, 0}},
                        3)
        );
    }

    @DisplayName("GameImpl.merge moves with merge has correct score")
    @ParameterizedTest(name = "{2}: multiple per row/column {0}")
    @MethodSource("generateMoveDataWithMergeAndScore")
    void moveWithMergeHasCorrectScore(boolean multiple, int[][] gameBoard,
                                      Direction direction, int score) {
        sut = new GameImpl(gameBoard);
        GameImpl.random = new IntRandomStub(List.of(3, 3, 2));

        sut.move(direction);

        assertThat(sut.getScore()).isEqualTo(score);
    }

    private static Stream<Arguments> generateMoveDataWithMergeAndScore() {
        return Stream.of(
                Arguments.of(false,
                        new int[][]{
                                {0, 0, 0, 0},
                                {2, 0, 0, 0},
                                {0, 0, 0, 0},
                                {2, 0, 0, 0}},
                        Direction.left, 4),
                Arguments.of(true,
                        new int[][]{
                                {2, 0, 0, 0},
                                {2, 0, 0, 0},
                                {2, 0, 0, 0},
                                {2, 0, 0, 0}},
                        Direction.left, 8),
                Arguments.of(false,
                        new int[][]{
                                {0, 0, 0, 0},
                                {2, 0, 0, 0},
                                {0, 0, 0, 0},
                                {2, 0, 0, 0}},
                        Direction.right, 4),
                Arguments.of(true,
                        new int[][]{
                                {2, 0, 0, 0},
                                {2, 0, 0, 0},
                                {2, 0, 0, 0},
                                {2, 0, 0, 0}},
                        Direction.right, 8),
                Arguments.of(false,
                        new int[][]{
                                {0, 2, 0, 2},
                                {0, 0, 0, 0},
                                {0, 0, 0, 0},
                                {0, 0, 0, 0}},
                        Direction.up, 4),
                Arguments.of(true,
                        new int[][]{
                                {2, 2, 2, 2},
                                {0, 0, 0, 0},
                                {0, 0, 0, 0},
                                {0, 0, 0, 0}},
                        Direction.up, 8),
                Arguments.of(false,
                        new int[][]{
                                {0, 2, 0, 2},
                                {0, 0, 0, 0},
                                {0, 0, 0, 0},
                                {0, 0, 0, 0}},
                        Direction.down, 4),
                Arguments.of(true,
                        new int[][]{
                                {2, 2, 2, 2},
                                {0, 0, 0, 0},
                                {0, 0, 0, 0},
                                {0, 0, 0, 0}},
                        Direction.down, 8)


        );
    }
}
