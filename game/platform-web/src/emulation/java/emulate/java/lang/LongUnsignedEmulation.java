package emulate.java.lang;

import com.github.xpenatan.gdx.backends.teavm.gen.Emulate;

@Emulate(value = Long.class, updateCode = true)
public class LongUnsignedEmulation {

    @Emulate
    public static String toUnsignedString(long value) {
        if (value >= 0) {
            return Long.toString(value);
        }
        long quotient = (value >>> 1) / 5;
        long remainder = value - quotient * 10;
        return Long.toString(quotient) + remainder;
    }
}
