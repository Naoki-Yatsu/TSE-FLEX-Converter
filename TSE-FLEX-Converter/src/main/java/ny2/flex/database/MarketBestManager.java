package ny2.flex.database;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ny2.flex.data.MarketDepth;

/**
 * This class checks Market Best price changed or not.
 */
public class MarketBestManager {

    // //////////////////////////////////////
    // Field
    // //////////////////////////////////////

    /** Threshold must be smaller than minimum digit. */
    private static final double EQUAL_THRESHOLD = 0.000001;

    /** Last depth of all stocks. key is symbol.*/
    private Map<String, MarketDepth> lastDepthMap = new ConcurrentHashMap<>(10000);

    // //////////////////////////////////////
    // Method
    // //////////////////////////////////////

    public boolean checkBestUpdated(MarketDepth newDepth) {
        // get last depth
        MarketDepth lastDepth = lastDepthMap.get(newDepth.getSym());
        // update depth
        lastDepthMap.put(newDepth.getSym(), newDepth);
        if (lastDepth == null) {
            return true;
        }

        // check updated
        boolean notChanged = true;
        notChanged = notChanged && doubleEqual(newDepth.getBidPrice(), lastDepth.getBidPrice(), EQUAL_THRESHOLD);
        notChanged = notChanged && doubleEqual(newDepth.getAskPrice(), lastDepth.getAskPrice(), EQUAL_THRESHOLD);
        notChanged = notChanged && (newDepth.getBidQuantity() == lastDepth.getBidQuantity());
        notChanged = notChanged && (newDepth.getAskQuantity() == lastDepth.getAskQuantity());
        notChanged = notChanged && (newDepth.getBidNumOrder() == lastDepth.getBidNumOrder());
        notChanged = notChanged && (newDepth.getAskNumOrder() == lastDepth.getAskNumOrder());
        return !notChanged;
    }

    private boolean doubleEqual(double d1, double d2, double threshold) {
        if (d1 > d2 - threshold && d1 < d2 + threshold) {
            return true;
        } else {
            return false;
        }
    }
}
