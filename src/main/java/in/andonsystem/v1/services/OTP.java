/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.andonsystem.v1.services;

import java.util.Random;

/**
 *
 * @author Md Zahid Raza
 */
public class OTP {

private static final char[] symbols;

    static {
        StringBuilder tmp = new StringBuilder();
        for (char ch = '0'; ch <= '9'; ++ch)
            tmp.append(ch);
        symbols = tmp.toString().toCharArray();
    }   

    private final Random random = new Random();

    private final char[] buf;

    public OTP(int length) {
        buf = new char[length];
    }

    public String nextString() {
        for (int idx = 0; idx < buf.length; ++idx) 
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }
}