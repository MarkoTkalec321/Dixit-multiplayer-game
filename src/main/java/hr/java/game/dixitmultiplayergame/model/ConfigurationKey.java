package hr.java.game.dixitmultiplayergame.model;

public enum ConfigurationKey {
    RMI_HOST("rmi.host"),
    RMI_PORT("rmi.port"),
    RANDOM_PORT_HINT("random.port.hint");

    private final String key;

    ConfigurationKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
