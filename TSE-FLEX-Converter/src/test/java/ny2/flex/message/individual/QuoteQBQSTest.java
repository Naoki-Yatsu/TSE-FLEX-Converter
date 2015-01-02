package ny2.flex.message.individual;

import static org.junit.Assert.assertEquals;
import ny2.flex.message.individual.QuoteQBQS;

import org.junit.Before;
import org.junit.Test;

public class QuoteQBQSTest {

    private static final String SAMPLE_MESSAGE_BID = "QB  14      73210000+144826190 0                                0";
    private static final String SAMPLE_MESSAGE_ASK = "QS  14      74100000+085941406100           500+0             1+1";
    private static final String SAMPLE_MESSAGE_MARKETORDER = "QS  1                080000347100           100+0             1+0";

    private QuoteQBQS instanceQB;
    private QuoteQBQS instanceQS;
    private QuoteQBQS instanceMkt;

    @Before
    public void setUp() throws Exception {
        instanceQB = QuoteQBQS.createBidQuote(SAMPLE_MESSAGE_BID);
        instanceQS = QuoteQBQS.createAskQuote(SAMPLE_MESSAGE_ASK);
        instanceMkt = QuoteQBQS.createAskQuote(SAMPLE_MESSAGE_MARKETORDER);

        instanceQB.convertMessage();
        instanceQS.convertMessage();
        instanceMkt.convertMessage();
    }

    @Test
    public void testConvertMessage() {
        // System.out.println(instanceQB);
        // System.out.println(instanceQS);
        // System.out.println(instanceMkt);
    }

    @Test
    public void testGetPrice() {
        assertEquals(7321d, instanceQB.getPrice(), 0.00001);
        assertEquals(7410d, instanceQS.getPrice(), 0.00001);
        assertEquals(0d, instanceMkt.getPrice(), 0.00001);
    }

    @Test
    public void testIsBestBidAsk() {
        assertEquals(false, instanceQB.isBestBidAsk());
        assertEquals(true, instanceQS.isBestBidAsk());
        assertEquals(false, instanceMkt.isBestBidAsk());
    }

    @Test
    public void testExistQuote() {
        assertEquals(false, instanceQB.existQuote());
        assertEquals(true, instanceQS.existQuote());
        assertEquals(true, instanceMkt.existQuote());
    }

    @Test
    public void testIsMarketOrder() {
        assertEquals(false, instanceQB.isMarketOrder());
        assertEquals(false, instanceQS.isMarketOrder());
        assertEquals(true, instanceMkt.isMarketOrder());
    }

}
