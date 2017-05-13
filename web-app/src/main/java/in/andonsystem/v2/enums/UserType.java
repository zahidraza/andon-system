package in.andonsystem.v2.enums;

public enum UserType {
    FACTORY("FACTORY"), SAMPLING("SAMPLING"), MERCHANDISING("MERCHANDISING");

    private String value;

    UserType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static UserType parse(String value) {
        UserType userType = null;
        for (UserType item : UserType.values()) {
            if (item.getValue().equals(value)) {
                userType = item;
                break;
            }
        }
        return userType;
    }
}
