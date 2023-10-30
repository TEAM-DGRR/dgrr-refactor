package live.dgrr.global.util;

import live.dgrr.domain.game.entity.GameResult;

public class EloCalculator {
    private static final int K = 30;
    private EloCalculator() {
    }

    public static int calculateRating(int myRating, int enemyRating, GameResult gameResult) {
        double expectedWinRate = 1.0 / (1.0 + Math.pow(10, (enemyRating - myRating) / 400.0));

        double result = 0;
        switch (gameResult) {
            case LOSE -> result = 0;
            case DRAW -> result = 0.5;
            case WIN -> result = 1;
        }

        double newRating = myRating + K * (result - expectedWinRate);
        return (int) newRating;
    }
}
