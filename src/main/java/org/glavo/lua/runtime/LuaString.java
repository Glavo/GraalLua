/*
 * Copyright 2021 Glavo
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <https://openjdk.java.net/legal/gplv2+ce.html>.
 */

package org.glavo.lua.runtime;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;

@ExportLibrary(InteropLibrary.class)
public class LuaString implements TruffleObject {
    public static final LuaString TRUE_STRING = new LuaString("true");
    public static final LuaString FALSE_STRING = new LuaString("false");
    public static final LuaString NIL_STRING = new LuaString("nil");

    static final byte[] DigitTens = {
            '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
            '1', '1', '1', '1', '1', '1', '1', '1', '1', '1',
            '2', '2', '2', '2', '2', '2', '2', '2', '2', '2',
            '3', '3', '3', '3', '3', '3', '3', '3', '3', '3',
            '4', '4', '4', '4', '4', '4', '4', '4', '4', '4',
            '5', '5', '5', '5', '5', '5', '5', '5', '5', '5',
            '6', '6', '6', '6', '6', '6', '6', '6', '6', '6',
            '7', '7', '7', '7', '7', '7', '7', '7', '7', '7',
            '8', '8', '8', '8', '8', '8', '8', '8', '8', '8',
            '9', '9', '9', '9', '9', '9', '9', '9', '9', '9',
    };

    static final byte[] DigitOnes = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
    };

    @CompilerDirectives.CompilationFinal(dimensions = 1)
    byte[] elements;

    private volatile WeakReference<String> cache;

    public LuaString(byte[] elements) {
        assert elements != null;
        this.elements = elements;
    }

    public LuaString(String source) {
        this.elements = source.getBytes(StandardCharsets.UTF_8);
        this.cache = new WeakReference<>(source);
    }

    private static int stringSize(long x) {
        int d = 1;
        if (x >= 0) {
            d = 0;
            x = -x;
        }
        long p = -10;
        for (int i = 1; i < 19; i++) {
            if (x > p)
                return i + d;
            p = 10 * p;
        }
        return 19 + d;
    }

    public static LuaString valueOf(long value) {
        int size = stringSize(value);
        byte[] buf = new byte[size];
        long i = value;
        long q;
        int r;
        int charPos = size;

        boolean negative = (i < 0);
        if (!negative) {
            i = -i;
        }

        // Get 2 digits/iteration using longs until quotient fits into an int
        while (i <= Integer.MIN_VALUE) {
            q = i / 100;
            r = (int) ((q * 100) - i);
            i = q;
            buf[--charPos] = DigitOnes[r];
            buf[--charPos] = DigitTens[r];
        }

        // Get 2 digits/iteration using ints
        int q2;
        int i2 = (int) i;
        while (i2 <= -100) {
            q2 = i2 / 100;
            r = (q2 * 100) - i2;
            i2 = q2;
            buf[--charPos] = DigitOnes[r];
            buf[--charPos] = DigitTens[r];
        }

        // We know there are at most two digits left at this point.
        q2 = i2 / 10;
        r = (q2 * 10) - i2;
        buf[--charPos] = (byte) ('0' + r);

        // Whatever left is the remaining digit.
        if (q2 < 0) {
            buf[--charPos] = (byte) ('0' - q2);
        }

        if (negative) {
            buf[--charPos] = (byte) '-';
        }
        return new LuaString(buf);
    }

    @ExportMessage
    public boolean isString() {
        return true;
    }

    @ExportMessage
    public String asString() {
        Reference<String> cache = this.cache;
        String str;
        if (cache == null || (str = cache.get()) == null) {
            final byte[] elements = this.elements;
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (elements) {
                if ((cache = this.cache) == null || (str = cache.get()) == null) {
                    str = new String(this.elements, StandardCharsets.UTF_8);
                    this.cache = new WeakReference<>(str);
                    return str;
                }
            }
        }
        return str;
    }

    @Override
    public String toString() {
        return asString();
    }
}
