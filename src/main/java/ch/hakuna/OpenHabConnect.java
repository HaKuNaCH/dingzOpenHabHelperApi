package ch.hakuna;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class OpenHabConnect {
    private static final String OPENHAB_BASEPATH = "/rest/items/";

    private static final String OPENHAB_HOSTNAME = ManageConfig.getOpenhabHostname();
    private static final String OPENHAB_TOKEN = ManageConfig.getOpenhabToken();

    private final String openHabUri;
    private final String openHabItemName;
    private final String action;
    private final String button;
    private final String mac;

    // Example:
    // http://10.0.1.9:8082/rest/items/dingzButton3/state

    /**
     * Constructor
     */
    public OpenHabConnect(String openHabItemName, String action, String button, String mac) {
        super();

        this.openHabItemName = openHabItemName;
        this.action = action;
        this.button = button;
        this.mac = mac;
        this.openHabUri = OPENHAB_HOSTNAME + OPENHAB_BASEPATH + openHabItemName + "/state";
    }


    /**
     * Call Dingz API (PUT)
     */
    protected String getOpenHabResponse() {

        String result;

        try {
            int responseCode = executePut(this.getAction());

            // build response string, depending on parameters given
            String device = "";
            if (!mac.equals("N/A")) {
                device = device + " from device " + mac;
            }

            if (!button.equals("N/A")) {
                if (device.isEmpty()) {
                    device = device + " from button_" + button;
                } else {
                    device = device + "/button_" + button;
                }
            }

            if (responseCode == 202 || responseCode == 200) {
                // everything is fine, handle the response
                result = "INFO:  Update item '" + openHabItemName + "' to value '" + action + "'" + device + " successfully sent.";

                // check if reset value is required
                if (ManageConfig.getResetMode().equals("true")) {
                    executePut(ManageConfig.getResetValue());
                }

            } else {
                result = "ERROR: Update item '" + openHabItemName + "' to value '" + action + "'" + device + " failed with HTTP" + responseCode + ".";
            }

        } catch (IOException ex) {
            result = "ERROR: OpenHAB not available.";
        }

        return result;
    }

    private int executePut(String value) throws IOException {
        URL url = URI.create(this.getOpenHabUri()).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setDoOutput(true);
        connection.setRequestProperty("Authorization", "Bearer " + OPENHAB_TOKEN);
        connection.setRequestProperty("Accept", "*/*");
        connection.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");

        byte[] payload = value.getBytes(StandardCharsets.UTF_8);
        connection.setFixedLengthStreamingMode(payload.length);
        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(payload);
        }

        int statusCode = connection.getResponseCode();
        connection.disconnect();
        return statusCode;
    }

    /* getter */

    public String getOpenHabUri() {
        return openHabUri;
    }

    public String getAction() {
        return action;
    }


}
