package spw4.game2048;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class GameImplTest {

    private GameImpl sut;

    @BeforeEach
    void beforeEach() {
        sut = new GameImpl();
    }

    @DisplayName("GameImpl.ctor when called, random is not null")
    @Test
    void ctorRandomIsNotNull() {
        new GameImpl();

        assertThat(GameImpl.random).isNotNull();
    }

    @DisplayName("GameImpl.initialize when called, tiles count is equal to 2")
    @Test
    void initializeTilesCountEqualsTwo() {
        sut.initialize();

        assertThat(sut.getTilesCount()).isEqualTo(2);
    }

    @DisplayName("GameImpl.initialize when called, tiles are placed at the correct position")
    @Test
    void initializeTilesArePlaced() {
        GameImpl.random = new IntRandomStub(List.of(0, 1, 0, 2, 2, 0));

        sut.initialize();

        assertThat(sut.getValueAt(0, 1)).isGreaterThan(0);
        assertThat(sut.getValueAt(2, 2)).isGreaterThan(0);
    }

    @DisplayName("GameImpl.initialize when called, tiles are placed with the correct values")
    @Test
    void initializeTilesArePlacedWithCorrectNumber() {
        GameImpl.random = new IntRandomStub(List.of(0, 1, 5, 2, 2, 9));

        sut.initialize();

        assertThat(sut.getValueAt(0, 1)).isEqualTo(2);
        assertThat(sut.getValueAt(2, 2)).isEqualTo(4);
    }

    @DisplayName("GameImpl.getValueAt when index valid get valid value")
    @ParameterizedTest(name = "{0} and {1}")
    @CsvSource({"0, 0", "0, 3", "3, 0", "3, 3"})
    void getValueAtWithValidIndexReturnsValue(int x, int y) {
        sut.initialize();

        int result = sut.getValueAt(x, y);

        assertThat(result).isGreaterThanOrEqualTo(0);
        assertThat(result).isLessThanOrEqualTo(2048);
        assertThat(isPowerOfTwoOrZero(result)).isTrue();
    }

    @DisplayName("GameImpl.getValueAt when invalid index throws IllegalArgumentException")
    @ParameterizedTest(name = "{0} and {1}")
    @CsvSource({"-1, 0", "0, -1", "4, 0", "0, 4"})
    void getValueAtWithInvalidIndexThrowsException(int x, int y) {
        assertThrows(IllegalArgumentException.class,
                () -> sut.getValueAt(x, y));
    }

    @DisplayName("GameImpl.isWon when one tile is 2048 return true")
    @Test
    void isWonWhenTileIs2048ReturnsTrue() {
        sut.getGameBoard()[2][3] = 2048;

        boolean result = sut.isWon();

        assertThat(result).isTrue();
    }

    @DisplayName("GameImpl.isWon when no tile is 2048 return true")
    @Test
    void isWonWhenNoTileIsReturnsFalse() {
        sut.getGameBoard()[2][3] = 1024;
        sut.getGameBoard()[0][2] = 4;

        boolean result = sut.isWon();

        assertThat(result).isFalse();
    }

    @DisplayName("GameImpl.isOver when all tiles are full returns true")
    @Test
    void isOverWhenTilesAreFullReturnsTrue() {
        int[][] gameBoard = sut.getGameBoard();
        for (var ints : gameBoard) {
            Arrays.fill(ints, 2);
        }

        boolean result = sut.isOver();

        assertThat(result).isTrue();
    }

    @DisplayName("GameImpl.isOver when not all tiles are full returns false")
    @Test
    void isOverWhenNotAllTilesAreFullReturnsFalse() {
        sut.getGameBoard()[2][3] = 1024;
        sut.getGameBoard()[0][2] = 4;

        boolean result = sut.isOver();

        assertThat(result).isFalse();
    }

    @DisplayName("GameImpl.getMoves returns zero after initialize")
    @Test
    void getMovesReturnsZeroAfterInitialize() {
        int result = sut.getMoves();

        assertThat(result).isZero();
    }

    @DisplayName("GameImpl.getMoves returns one after one move")
    @Test
    void getMovesReturnsOneAfterMoveInitialize() {
        sut.move(Direction.down);

        int result = sut.getMoves();

        assertThat(result).isOne();
    }

    @DisplayName("GameImpl.getScore returns zero after initialize")
    @Test
    void getScoreReturnsZeroAfterInitialize() {
        sut.initialize();

        int result = sut.getScore();

        assertThat(result).isZero();
    }

    @DisplayName("GameImpl.toString returns valid String after initialize")
    @Test
    void toStringReturnsValidStringAfterInitialize() {
        GameImpl.random = new IntRandomStub(List.of(0, 1, 5, 2, 2, 9));
        sut.initialize();

        String result = sut.toString();

        assertThat(result).isEqualTo("" +
                "Moves: 0\t\tScore: 0\n" +
                ".\t\t.\t\t.\t\t.\t\t\n" +
                "2\t\t.\t\t.\t\t.\t\t\n" +
                ".\t\t.\t\t4\t\t.\t\t\n" +
                ".\t\t.\t\t.\t\t.\t\t\n");
    }

    private boolean isPowerOfTwoOrZero(int n) {
        if (n == 0) return true;

        return (int) (Math.ceil((Math.log(n) / Math.log(2)))) ==
                (int) (Math.floor(((Math.log(n) / Math.log(2)))));
    }
}