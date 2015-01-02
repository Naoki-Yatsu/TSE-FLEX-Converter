package ny2.flex.message.individual;

import ny2.flex.message.individual.CurrentPrice1P;

import org.junit.Before;
import org.junit.Test;

public class CurrentPrice1PTest {

    private static final String SAMPLE_MESSAGE = "1P004      73690000+1231014981   ";

    private CurrentPrice1P instance;

    @Before
    public void setUp() {
        instance = new CurrentPrice1P(SAMPLE_MESSAGE);
    }

    @Test
    public void testConvert() {
        instance.convertMessage();
        // System.out.println(instance);
    }

}
