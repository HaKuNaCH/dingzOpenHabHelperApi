package ch.hakuna;

public class ManageConfig {

    private static String exposeApiName;
    private static String openhabHostname;
    private static String allowedDevices;
    private static String openhabToken;
    private static String resetMode;
    private static String resetValue;

    public static String getExposeApiName() { return exposeApiName; }
    public void setExposeApiName(String exposeApiName) { ManageConfig.exposeApiName = exposeApiName; }

    public static String getOpenhabHostname() {
        return openhabHostname;
    }

    public void setOpenhabHostname(String openhabHostname) { ManageConfig.openhabHostname = openhabHostname; }

    public static String getAllowedDevices() {
        return allowedDevices;
    }

    public void setAllowedDevices(String allowedDevices) {
        ManageConfig.allowedDevices = allowedDevices;
    }

    public static String getOpenhabToken() {
        return openhabToken;
    }

    public void setOpenhabToken(String openhabToken) {
        ManageConfig.openhabToken = openhabToken;
    }

    public static String getResetMode() {
        return resetMode;
    }

    public void setResetMode(String resetMode) {
        ManageConfig.resetMode = resetMode;
    }

    public static String getResetValue() {
        return resetValue;
    }

    public void setResetValue(String resetValue) {
        ManageConfig.resetValue = resetValue;
    }
}
