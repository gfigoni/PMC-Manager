package models;

/**
 * Trackmania TMEnvironment
 * @author gehef
 */
public enum TMEnvironment {

    STADIUM, BAY, ISLAND, COAST, RALLY, SPEED, ALPINE, CANYON, VALLEY;

    public static TMEnvironment getEnvironment(String name) {
        return TMEnvironment.valueOf(name.toUpperCase());
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
