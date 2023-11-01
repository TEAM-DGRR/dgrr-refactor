package live.dgrr.global.util;

import live.dgrr.global.entity.Tier;

public class TierCalculator {



    private TierCalculator() {
    }

    private static final int GOLD_THRESHOLD =1600;
    private static final int SILVER_THRESHOLD =1400;

    public static Tier calculateRank(int score) {
        if(score >= GOLD_THRESHOLD) {
            return Tier.GOLD;
        }
        else if(score >= SILVER_THRESHOLD) {
            return Tier.SILVER;
        }
        else {
            return Tier.BRONZE;
        }
    }
}
