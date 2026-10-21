package de.beispiel.demo;

public enum option {
    SELLER,
    STOREMAN;

    private option() {}

    public String value() {
        return name();

    }

    public static option fromValue(String v) {
        return valueOf(v);
    }
}
