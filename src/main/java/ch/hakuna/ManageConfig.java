package ch.hakuna;

public class ManageConfig {

    private static String exposeApiName;
    private static String openhabHostname;
    private static String allowedDevices;
    private static String allowedIps;
    private static String openhabToken;
    private static String resetMode;
    private static String resetValue;

    public static String getExposeApiName() { return exposeApiName; }

    public static void setExposeApiName(String exposeApiName) { ManageConfig.exposeApiName = exposeApiName; }

    public static String getOpenhabHostname() {
        return openhabHostname;
    }

    public static void setOpenhabHostname(String openhabHostname) { ManageConfig.openhabHostname = openhabHostname; }

    public static String getAllowedDevices() {
        return allowedDevices;
    }

    public static void setAllowedDevices(String allowedDevices) {
        ManageConfig.allowedDevices = allowedDevices;
    }

    public static String getAllowedIps() { return allowedIps; }

    public static void setAllowedIps(String allowedIps) { ManageConfig.allowedIps = allowedIps; }

    public static String getOpenhabToken() {
        return openhabToken;
    }

    public static void setOpenhabToken(String openhabToken) {
        ManageConfig.openhabToken = openhabToken;
    }

    public static String getResetMode() {
        return resetMode;
    }

    public static void setResetMode(String resetMode) {
        ManageConfig.resetMode = resetMode;
    }

    public static String getResetValue() {
        return resetValue;
    }

    public static void setResetValue(String resetValue) {
        ManageConfig.resetValue = resetValue;
    }
}
