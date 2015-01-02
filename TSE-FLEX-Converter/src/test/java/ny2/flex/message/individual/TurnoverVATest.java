package ny2.flex.message.individual;

import ny2.flex.message.individual.TurnoverVA;

import org.junit.Before;
import org.junit.Test;

public class TurnoverVATest {

    private static final String SAMPLE_MESSAGE = "VA   0   52607798300123101498 ";

    private TurnoverVA instance;

    @Before
    public void setUp() throws Exception {
        instance = new TurnoverVA(SAMPLE_MESSAGE);
    }

    @Test
    public void testConvertMessage() {
        instance.convertMessage();
//        System.out.println(instance);
    }

}
