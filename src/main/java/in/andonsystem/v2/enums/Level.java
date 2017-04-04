package in.andonsystem.v2.enums;

/**
 * Created by razamd on 3/30/2017.
 */
public enum Level {
    LEVEL0("LEVEL 0"),LEVEL1("LEVEL 1"), LEVEL2("LEVEL 2"), LEVEL3("LEVEL 3"),LEVEL4("LEVEL 4");

    private String value;

    Level(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Level parse(String value) {
        Level level = null;
        for (Level item : Level.values()) {
            if (item.getValue().equals(value)) {
                level = item;
                break;
            }
        }
        return level;
    }
}
