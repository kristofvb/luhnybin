package com.justme.luhny;

import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

public class MaskerTest {

    @Test
    public void test() throws IOException {
        sendAndExpect("LF only ->\n<- LF only\n", "LF only ->\n<- LF only\n");
        sendAndExpect("56613959932537", "XXXXXXXXXXXXXX");
        sendAndExpect("5661395993253756613959932537", "XXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        sendAndExpect("9875610591081018250321\n", "987XXXXXXXXXXXXXXXX321\n");
        sendAndExpect("4352 7211 4223 5131\n", "XXXX XXXX XXXX XXXX\n");
        sendAndExpect("1256613959932537\n", "12XXXXXXXXXXXXXX\n");
        sendAndExpect("49536290423965\n", "49536290423965\n");
        sendAndExpect("6853371389452376\n", "XXXXXXXXXXXXXXXX\n");
    }

    private void sendAndExpect(String in, String out) throws IOException {
        StringReader input = new StringReader(in);
        StringWriter output = new StringWriter();
        new Masker(input, output).mask();
        assertEquals(out, output.toString());
    }

}
