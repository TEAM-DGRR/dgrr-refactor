package live.dgrr.global.util;

import live.dgrr.global.entity.Rank;

public class RankCalculator {



    private RankCalculator() {
    }

    private static final int GOLD_THRESHOLD =1600;
    private static final int SILVER_THRESHOLD =1400;

    public static Rank calculateRank(int score) {
        if(score >= GOLD_THRESHOLD) {
            return Rank.GOLD;
        }
        else if(score >= SILVER_THRESHOLD) {
            return Rank.SILVER;
        }
        else {
            return Rank.BRONZE;
        }
    }
}
