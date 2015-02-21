package ny2.flex.board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import ny2.flex.data.Data;
import ny2.flex.data.MarketDepth;
import ny2.flex.database.OutputDao;
import ny2.flex.message.Message;
import ny2.flex.message.MessageBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BoardManagerImpl implements BoardManager {

    // //////////////////////////////////////
    // Field
    // //////////////////////////////////////

    @Autowired
    @Qualifier("CsvDao")
    private OutputDao outputDao;

    @Value("${board.maxdepth}")
    private int maxDepth;

    @Value("${board.remove.ce.MarketDepth}")
    private boolean removeContinuousExecutionMarketDepth;

    /** Board Map. Key=issueCode */
    private Map<String, Board> boardMap;

    /** Temp Map for divided message. key=issueCode */
    private Map<String, List<MessageBundle>> dividedMessageMap;

//    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    // //////////////////////////////////////
    // Constructor
    // //////////////////////////////////////

    public BoardManagerImpl() {
        initialize();
    }

    private void initialize() {
        boardMap = new HashMap<>(10000);
        dividedMessageMap = new HashMap<>(10000);
    }

    // //////////////////////////////////////
    // Method
    // //////////////////////////////////////

    public void executeMessageBundle(MessageBundle bundle) {

        // For divided message
        if (bundle.isDividedMessage()) {
            Optional<MessageBundle> optional = mergeDividedMessage(bundle);
            if (optional.isPresent()) {
                bundle = optional.get();
            } else {
                return;
            }
        }

        // get target board
        String issueCode = bundle.getIssueCode();
        Board board = boardMap.get(issueCode);

        // create board is not exist
        if (board == null) {
            board = new Board(issueCode, bundle.getIssueClassificationType(), maxDepth, bundle.isIntegerPrice(), removeContinuousExecutionMarketDepth);
            boardMap.put(issueCode, board);
        }

        // execute message
        List<Data> dataList = board.updateBoard(bundle);
        outputDao.insertList(dataList);
    }

    /**
     * Merge divided message. When merged return merged message, otherwise return null.
     * @param bundle
     * @return
     */
    private Optional<MessageBundle> mergeDividedMessage(MessageBundle bundle) {
        String issueCode = bundle.getIssueCode();

        if (bundle.isLastDividedMessage()) {
            // merge messages
            List<MessageBundle> bundleList = dividedMessageMap.remove(issueCode);
            bundleList.add(bundle);
            MessageBundle mergedBundle = bundleList.get(0);
            for (int i = 1; i < bundleList.size(); i++) {
                for (Message message : bundleList.get(i).getAllMessages()) {
                    mergedBundle.addMessage(message);
                }
            }
            mergedBundle.setIssueDividedSerialNo(1);
            mergedBundle.setIssueDividedTotalNo(1);
            return Optional.of(mergedBundle);

        } else {
            // add to map
            List<MessageBundle> bundleList = dividedMessageMap.get(issueCode);
            // create messageList is not exist
            if (bundleList == null) {
                bundleList = new ArrayList<>();
                dividedMessageMap.put(issueCode, bundleList);
            }
            bundleList.add(bundle);

            return Optional.empty();
        }
    }

    @Override
    public void changeDate() {
        // output
        List<Data> dataList = new ArrayList<>(boardMap.size());
        for (Board board : boardMap.values()) {
            MarketDepth marketDepth = board.changeDateWithCreationDepth();
            if (marketDepth != null) {
                dataList.add(board.changeDateWithCreationDepth());
            }
        }
        outputDao.insertList(dataList);

        // init all board
        initialize();
    }

    // //////////////////////////////////////
    // Getters and Setters
    // //////////////////////////////////////

}
