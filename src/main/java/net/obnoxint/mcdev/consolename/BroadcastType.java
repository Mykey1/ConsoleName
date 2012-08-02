package net.obnoxint.mcdev.consolename;

public enum BroadcastType {

    SIGN("Sign"),
    SIMPLE("Simple"),
    CUSTOM("Custom");

    private final String id;

    private BroadcastType(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
