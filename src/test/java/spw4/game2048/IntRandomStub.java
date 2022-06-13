package spw4.game2048;

import java.util.Iterator;
import java.util.Random;

public final class IntRandomStub extends Random {
    private final Iterator<Integer> iterator;
    private final int defaultValue = 3;

    public IntRandomStub(Iterable<Integer> values) {
        this.iterator = values.iterator();
    }

    @Override
    public int nextInt() {
        if (!iterator.hasNext()) {
            return 3;
        }
        return iterator.next();
    }

    @Override
    public int nextInt(int ignored) {
        if (!iterator.hasNext()) {
            return 3;
        }
        return iterator.next();
    }
}
