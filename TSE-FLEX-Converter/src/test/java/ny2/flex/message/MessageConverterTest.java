package ny2.flex.message;

import ny2.flex.message.MessageBundle;
import ny2.flex.message.MessageConverter;

import org.junit.Before;
import org.junit.Test;

public class MessageConverterTest {

    private static final String SAMPLE_MESSAGE = "   260052000000931001  0111       7203 \tNO      14    1    1\tQS  1                080000347100           100+0             1+0\tQS  14      73710000+080000347 0                                1\tQS  14      76200000+080000347100           200+0             1+0";
    private static final String SAMPLE_MESSAGE2 = "    96064000000271001  0124       3719  NO       1    1    1    1P11              ";

    MessageBundle bundle;
    MessageBundle bundle2;

    @Before
    public void setUp() throws Exception {
        bundle = MessageConverter.extract(SAMPLE_MESSAGE);
        bundle2 = MessageConverter.extract(SAMPLE_MESSAGE2);
    }
    @Test
    public void testExtract() {
//        System.out.println(bundle);
//        System.out.println(bundle2);
    }
}
