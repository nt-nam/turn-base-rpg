package emu.java.text;

import org.teavm.jso.JSBody;

public final class Normalizer {

    public enum Form {
        NFD,
        NFC,
        NFKD,
        NFKC
    }

    private Normalizer() {
    }

    public static String normalize(CharSequence source, Form form) {
        return normalizeText(source.toString(), form.name());
    }

    public static boolean isNormalized(CharSequence source, Form form) {
        return normalize(source, form).contentEquals(source);
    }

    @JSBody(params = {"text", "form"}, script = "return text.normalize(form);")
    private static native String normalizeText(String text, String form);
}
