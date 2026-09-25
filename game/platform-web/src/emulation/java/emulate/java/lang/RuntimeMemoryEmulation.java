package emulate.java.lang;

import com.github.xpenatan.gdx.backends.teavm.gen.Emulate;

@Emulate(value = Runtime.class, updateCode = true)
public class RuntimeMemoryEmulation {

    @Emulate
    public long maxMemory() {
        return Long.MAX_VALUE;
    }
}
