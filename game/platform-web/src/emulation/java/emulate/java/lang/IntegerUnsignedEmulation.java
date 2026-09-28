package emulate.java.lang;

import com.github.xpenatan.gdx.backends.teavm.gen.Emulate;

@Emulate(value = Integer.class, updateCode = true)
public class IntegerUnsignedEmulation {

    @Emulate
    public static String toUnsignedString(int value) {
        return Long.toString(value & 0xffffffffL);
    }
}
