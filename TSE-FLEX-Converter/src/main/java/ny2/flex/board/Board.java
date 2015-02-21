package ny2.flex.board;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.TreeMap;

import lombok.ToString;
import ny2.flex.common.BidAsk;
import ny2.flex.common.Side;
import ny2.flex.data.CurrentPrice;
import ny2.flex.data.Data;
import ny2.flex.data.MarketDepth;
import ny2.flex.data.MarketTrade;
import ny2.flex.message.IssueClassificationType;
import ny2.flex.message.MessageBundle;
import ny2.flex.message.individual.CurrentPrice1P;
import ny2.flex.message.individual.QuoteQBQS;
import ny2.flex.message.individual.TradingVolumeVL;
import ny2.flex.message.individual.TurnoverVA;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ToString
public class Board {

    // //////////////////////////////////////
    // Field
    // //////////////////////////////////////

    private static final Logger logger = LoggerFactory.getLogger(Board.class);

    /** Issue Code of board */
    private final String issueCode;

    /** Issue Classification */
    private final IssueClassificationType issueClassificationType;

    /** Max depth for MarketDepth */
    private final int maxDepthOutput;

    /** price is integer or not(include decimal number) */
    private final boolean isIntegerPrice;

    /** In continuous execution, this prevent output MarketDepth. */
    private boolean removeContinuousExecutionMarketDepth;

    /** Last serial no */
    private long lastSerialNo;

    /** Last update no of board */
    private int lastIssueUpdateNo;

    /** Last update time of board */
    private LocalTime updateTime;

    /** UpdateType for last message */
    private UpdateType updateType;

    /** Board for Bid */
    private NavigableMap<Long, BoardRow> bidBoardMap = new TreeMap<>();
    /** Board for Ask */
    private NavigableMap<Long, BoardRow> askBoardMap = new TreeMap<>();

    // Row for Market Order
    private BoardRow bidMarketOrderRow = new BoardRow(0L);
    private BoardRow askMarketOrderRow = new BoardRow(0L);

    // last trading status
    private double currentPrice;
    private long longCurrentPrice;
    private long tradingVolume;
    private long turnover;
    private Side lastSide;

    // When Continuous execution, set true.
    private boolean continuousExecution = false;

    // After first message executed, it will be true.
    private boolean initialized = false;

    // update flag for consider UpdateType
    boolean updateQuoteBid = false;
    boolean updateQuoteAsk = false;
    boolean updateDealBuy = false;
    boolean updateDealSell = false;

    // //////////////////////////////////////
    // Constructor
    // //////////////////////////////////////

    public Board(String issueCode, IssueClassificationType issueClassificationType, int maxDepth, boolean isIntegerPrice, boolean removeContinuousExecutionMarketDepth) {
        this.issueCode = issueCode;
        this.issueClassificationType = issueClassificationType;
        this.maxDepthOutput = maxDepth;
        this.isIntegerPrice = isIntegerPrice;
        this.removeContinuousExecutionMarketDepth = removeContinuousExecutionMarketDepth;
    }

    // //////////////////////////////////////
    // Method
    // //////////////////////////////////////

    /**
     * Update board from message.
     *
     * @param bundle
     * @return
     */
    public List<Data> updateBoard(MessageBundle bundle) {
        try {
            // create MarketDepth with last state
            MarketDepth lastMarketDepth = null;
            if (initialized && bundle.hasQuoteMessage()) {
                lastMarketDepth = createMarketDepth();
            } if (bundle.hasQuoteMessage()) {
                initialized = true;
            }

            // Update Board info
            this.lastSerialNo = bundle.getSerialNo();
            this.lastIssueUpdateNo = bundle.getIssueUpdateNo();
            List<Data> dataList = updateBoardInternal(bundle);

            // if NOT in continuousExecution, add MarketDepth
            if ((!continuousExecution || !removeContinuousExecutionMarketDepth) && lastMarketDepth != null) {
                dataList.add(lastMarketDepth);
            }

            return dataList;

        } catch (Exception e) {
            logger.error("Error in update board. Board Info = {}", toString(), e);
            return null;
        }
    }

    private List<Data> updateBoardInternal(MessageBundle bundle) {
        // List of Data for DB
        List<Data> dataList = new ArrayList<>();
        // items to calculate trade
        LocalTime lastUpdateTime = updateTime;
        long lastLongCurrentPrice = longCurrentPrice;
        long lastTradingVolume = tradingVolume;
        // reset continuousExecution
        continuousExecution = false;

        // execute Current Price Message
        if (bundle.hasCurrentPriceMessage()) {
            CurrentPrice1P currentPrice1P = bundle.getCurrentPrice1P();
            // set last current price
            longCurrentPrice = currentPrice1P.getLongPrice();
            currentPrice = currentPrice1P.getPrice();
            // create data
            dataList.add(createCurrentPrice(currentPrice1P));
        }

        // execute Trade Message
        if (bundle.hasTradingMessage()) {
            // pull messages from bundle
            TradingVolumeVL tradingVolumeVL = bundle.getTradingVolumeVL();
            TurnoverVA turnoverVA = bundle.getTurnoverVA();
            tradingVolume = tradingVolumeVL.getTradingVolume();
            turnover = turnoverVA.getTurnover();

            // create data
            MarketTrade marketTrade = createMarketTrade(tradingVolumeVL, turnoverVA, lastUpdateTime, lastLongCurrentPrice, lastTradingVolume);
            dataList.add(marketTrade);

            // Update updateTime
            this.updateTime = tradingVolumeVL.getTime();
        }

        // execute Quote message
        if (bundle.hasQuoteMessage()) {
            for (QuoteQBQS message : bundle.getQuoteMessages()) {
                switch (message.getMessageType()) {
                    case QS:
                        executeQuote(askBoardMap, message, bundle.getIssueUpdateNo());
                        updateQuoteAsk = true;
                        break;
                    case QB:
                        executeQuote(bidBoardMap, message, bundle.getIssueUpdateNo());
                        updateQuoteBid = true;
                        break;
                    case SC:
                        break;
                    case BC:
                        break;
                    default:
                        break;
                }
            }
            // update last update type
            updateUpdateType();
            // create data
            // For continuous execution, don't create MarketDepth here. Create it for next update head.
            // dataList.add(createMarketDepth());
        }

        return dataList;
    }

    /**
     * Create MarketDepth of last state. Since MarketDepth is created with next update, last state is now outputed.
     * @return
     */
    public MarketDepth changeDateWithCreationDepth() {
        if (initialized) {
            return createMarketDepth();
        } else {
            return null;
        }

    }

    /**
     * Execute quote message.
     *
     * @param boardMap
     * @param quote
     * @param issueUpdateNo
     */
    private void executeQuote(NavigableMap<Long, BoardRow> boardMap, QuoteQBQS quote, int issueUpdateNo) {
        // Update updateTime
        this.updateTime = quote.getTime();

        // For market order
        if (quote.isMarketOrder()) {
            if (quote.getBidAsk() == BidAsk.BID) {
                bidMarketOrderRow.updateRow(quote, issueUpdateNo);
            } else {
                askMarketOrderRow.updateRow(quote, issueUpdateNo);
            }
            return;
        }

        // For normal quote
        Long longPrice = quote.getLongPrice();
        BoardRow row = boardMap.get(longPrice);

        // If target price is not exist in board, create it.
        if (row == null) {
            row = new BoardRow(longPrice);
            boardMap.put(longPrice, row);
        }

        // Update Row
        if (quote.existQuote()) {
            row.updateRow(quote, issueUpdateNo);
        } else {
            // If quote disappear, delete row.
            boardMap.remove(longPrice);
        }
    }

    /**
     * Update lastUpdatType
     */
    private void updateUpdateType() {

        if (isLastUpdateDeal()) {
            // Update by Deal
            if (updateDealBuy && updateDealSell) {
                updateType = UpdateType.DEAL_BOTH;
            } else if (updateDealBuy) {
                updateType = UpdateType.DEAL_BUY;
            } else if (updateDealSell) {
                updateType = UpdateType.DEAL_SELL;
            }

        } else if (isLastUpdateQuote()) {
            // Update by Quote
            if (updateQuoteAsk && updateQuoteBid) {
                updateType = UpdateType.QUOTE_BOTH;
            } else if (updateQuoteBid) {
                updateType = UpdateType.QUOTE_BID;
            } else if (updateQuoteAsk) {
                updateType = UpdateType.QUOTE_ASK;
            }

        } else {
            updateType = UpdateType.OTHERS;
        }

        // reset last update flags
        updateQuoteBid = false;
        updateQuoteAsk = false;
        updateDealBuy = false;
        updateDealSell = false;
    }

    private boolean isLastUpdateQuote() {
        if (updateQuoteBid || updateQuoteAsk) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isLastUpdateDeal() {
        if (updateDealBuy || updateDealSell) {
            return true;
        } else {
            return false;
        }
    }

    private boolean existBidMarketOrder() {
        if (bidMarketOrderRow.getQuantity() > 0) {
            return true;
        } else {
            return false;
        }
    }

    private boolean existAskMarketOrder() {
        if (askMarketOrderRow.getQuantity() > 0) {
            return true;
        } else {
            return false;
        }
    }

    private long getBestBidLongPrice() {
        Optional<BoardRow> bestBid = getBestBidRow();
        if (bestBid.isPresent()) {
            return bestBid.get().getLongPrice();
        } else {
            return Long.MIN_VALUE;
        }
    }

    private long getBestAskLongPrice() {
        Optional<BoardRow> bestAsk = getBestAskRow();
        if (bestAsk.isPresent()) {
            return bestAsk.get().getLongPrice();
        } else {
            return Long.MAX_VALUE;
        }
    }

    /**
     * Judge the price is bid or ask. If price is between bid and ask, return null.
     *
     * @param longPrice
     * @return
     */
    private Optional<BidAsk> judgeBidOrAsk(long longPrice) {
        long bestBidPrice = getBestBidLongPrice();
        long bestAskPrice = getBestAskLongPrice();

        if (longPrice <= bestBidPrice) {
            return Optional.of(BidAsk.BID);
        } else if (longPrice >= bestAskPrice) {
            return Optional.of(BidAsk.ASK);
        } else {
            // if price is between bid and ask, return null.
            return Optional.empty();
        }
    }

    private boolean isBidAskReversed() {
        long bestBidPrice = getBestBidLongPrice();
        long bestAskPrice = getBestAskLongPrice();
        if (bestBidPrice >= bestAskPrice) {
            return true;
        } else {
            return false;
        }
    }

    // //////////////////////////////////////
    // Method (Create Data)
    // //////////////////////////////////////

    /**
     * Create CurrentPrice Data for DB
     *
     * @param currentPrice1P
     * @return
     */
    private CurrentPrice createCurrentPrice(CurrentPrice1P currentPrice1P) {
        CurrentPrice currentPrice = new CurrentPrice();

        currentPrice.setTime(currentPrice1P.getTime());
        currentPrice.setSym(issueCode);
        currentPrice.setPrice(currentPrice1P.getPrice());
        currentPrice.setState(currentPrice1P.getState());
        currentPrice.setUpdateNo(lastIssueUpdateNo);
        currentPrice.setSerialNo(lastSerialNo);
        currentPrice.setIntegerPrice(isIntegerPrice);

        return currentPrice;
    }

    /**
     * Create MarketTrade Data for DB
     *
     * @param tradingVolumeVL
     * @param turnoverVA
     * @param lastTradingVolume
     * @return
     */
    private MarketTrade createMarketTrade(TradingVolumeVL tradingVolumeVL, TurnoverVA turnoverVA,
            LocalTime lastUpdateTime, long lastLongCurrentPrice, long lastTradingVolume) {
        MarketTrade marketTrade = new MarketTrade();

        marketTrade.setTime(tradingVolumeVL.getTime());
        marketTrade.setSym(issueCode);

        // Side
        Side side = null;
        // If update time is the same as lastUpdateTime and price is worse, it is probably a part of the one trade.(Continuous execution)
        if (tradingVolumeVL.getTime().equals(lastUpdateTime) &&
             ((lastSide == Side.BUY && longCurrentPrice > lastLongCurrentPrice) || (lastSide == Side.SELL && longCurrentPrice < lastLongCurrentPrice))) {
                continuousExecution = true;
                // set side the same as last.
                side = lastSide;

        } else {
            // If best bid and ask is reversed, it will be opening matching.
            if (isBidAskReversed()) {
                side = Side.BOTH;
            } else {
                // Decide side from price.
                Optional<BidAsk> bidAsk = judgeBidOrAsk(longCurrentPrice);
                if (bidAsk.isPresent()) {
                    if (bidAsk.get() == BidAsk.BID) {
                        side = Side.SELL;
                    } else {
                        side = Side.BUY;
                    }
                } else {
                    side = Side.BOTH;
                }
            }
        }
        // set side
        lastSide = side;
        marketTrade.setSide(side);

        // set update deal
        switch (side) {
            case BUY:
                updateDealBuy = true;
                break;
            case SELL:
                updateDealSell = true;
                break;
            case BOTH:
                updateDealBuy = true;
                updateDealSell = true;
                break;
            default:
                break;
        }

        // Price, etc
        long quantity = tradingVolume - lastTradingVolume;
        marketTrade.setPrice(currentPrice);
        marketTrade.setQuantity(quantity);

        marketTrade.setTotalQuantity(tradingVolume);
        marketTrade.setTotalTurnover(turnover);

        marketTrade.setUpdateNo(lastIssueUpdateNo);
        marketTrade.setSerialNo(lastSerialNo);

        marketTrade.setIntegerPrice(isIntegerPrice);

        return marketTrade;
    }

    /**
     * Create MarketDepth Data for DB
     *
     * @return
     */
    private MarketDepth createMarketDepth() {
        MarketDepth marketDepth = new MarketDepth();

        marketDepth.setTime(updateTime);
        marketDepth.setSym(issueCode);
        marketDepth.setClassification(issueClassificationType);

        // Best
        Optional<BoardRow> bestBid = getBestBidRow();
        if (bestBid.isPresent()) {
            marketDepth.setBidPrice(bestBid.get().getPrice());
            marketDepth.setBidQuantity(bestBid.get().getQuantity());
            marketDepth.setBidNumOrder(bestBid.get().getNumberOfOrders());
        } else {
            marketDepth.setBidPrice(0);
            marketDepth.setBidQuantity(0);
            marketDepth.setBidNumOrder(0);
        }

        Optional<BoardRow> bestAsk = getBestAskRow();
        if (bestAsk.isPresent()) {
            marketDepth.setAskPrice(bestAsk.get().getPrice());
            marketDepth.setAskQuantity(bestAsk.get().getQuantity());
            marketDepth.setAskNumOrder(bestAsk.get().getNumberOfOrders());
        } else {
            marketDepth.setAskPrice(0);
            marketDepth.setAskQuantity(0);
            marketDepth.setAskNumOrder(0);
        }

        // Depth
        setDepth(marketDepth, BidAsk.BID, bidBoardMap, bidMarketOrderRow);
        setDepth(marketDepth, BidAsk.ASK, askBoardMap, askMarketOrderRow);

        // Others
        marketDepth.setUpdateType(updateType);
        marketDepth.setUpdateNo(lastIssueUpdateNo);
        marketDepth.setSerialNo(lastSerialNo);

        marketDepth.setIntegerPrice(isIntegerPrice);

        return marketDepth;
    }

    private void setDepth(MarketDepth marketDepth, BidAsk bidAsk, NavigableMap<Long, BoardRow> boardMap, BoardRow marketOrderRow) {

        int depth = boardMap.size();
        int index = 0;
        NavigableSet<Long> boardKeySet;

        // Check market order / set keyset
        if (bidAsk == BidAsk.BID) {
            boardKeySet = boardMap.descendingKeySet();
            if (existBidMarketOrder()) {
                depth++;
            }
        } else {
            boardKeySet = boardMap.navigableKeySet();
            if (existAskMarketOrder()) {
                depth++;
            }
        }

        // Size limit of max depth
        if (depth >= maxDepthOutput) {
            depth = maxDepthOutput;
        }

        // define value arrays
        double[] prices = new double[depth];
        long[] quantities = new long[depth];
        long[] numOrders = new long[depth];

        // Add market order first
        if ((bidAsk == BidAsk.BID && existBidMarketOrder()) ||
                (bidAsk == BidAsk.ASK && existAskMarketOrder())) {
            // set zero for price of market order
            prices[index] = marketOrderRow.getPrice();
            quantities[index] = marketOrderRow.getQuantity();
            numOrders[index] = marketOrderRow.getNumberOfOrders();
            index++;
        }

        // add all prices
        for (Long longPrice : boardKeySet) {
            BoardRow row = boardMap.get(longPrice);
            prices[index] = row.getPrice();
            quantities[index] = row.getQuantity();
            numOrders[index] = row.getNumberOfOrders();
            index++;

            // Limit of max depth
            if (index >= maxDepthOutput) {
                break;
            }
        }

        // Set to MarketDepth
        if (bidAsk == BidAsk.BID) {
            marketDepth.setBidPrices(prices);
            marketDepth.setBidQuantities(quantities);
            marketDepth.setBidNumOrders(numOrders);
        } else {
            marketDepth.setAskPrices(prices);
            marketDepth.setAskQuantities(quantities);
            marketDepth.setAskNumOrders(numOrders);
        }
    }

    // //////////////////////////////////////
    // Getters and Setters
    // //////////////////////////////////////

    public Optional<BoardRow> getBestBidRow() {
        Entry<Long, BoardRow> entry = bidBoardMap.lastEntry();
        if (entry != null) {
            return Optional.of(entry.getValue());
        } else {
            return Optional.empty();
        }

    }

    public Optional<BoardRow> getBestAskRow() {
        Entry<Long, BoardRow> entry = askBoardMap.firstEntry();
        if (entry != null) {
            return Optional.of(entry.getValue());
        } else {
            return Optional.empty();
        }
    }

    public boolean isContiniousExecution() {
        return continuousExecution;
    }
}
