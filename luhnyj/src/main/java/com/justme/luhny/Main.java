package com.justme.luhny;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

class Main {
    public static void main(String[] args) throws IOException {
        Masker masker = new Masker(new InputStreamReader(System.in), new OutputStreamWriter(System.out));
        masker.mask();
    }

}
