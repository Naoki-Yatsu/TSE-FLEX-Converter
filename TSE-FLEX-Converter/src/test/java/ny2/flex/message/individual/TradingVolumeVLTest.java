package ny2.flex.message.individual;

import ny2.flex.message.individual.TradingVolumeVL;

import org.junit.Before;
import org.junit.Test;

public class TradingVolumeVLTest {

    private static final String SAMPLE_MESSAGE = "VL   0       7149600123101498 ";

    private TradingVolumeVL instance;

    @Before
    public void setUp() throws Exception {
        instance = new TradingVolumeVL(SAMPLE_MESSAGE);
    }

    @Test
    public void testConvertMessage() {
        instance.convertMessage();
        // System.out.println(instance);
    }

}
