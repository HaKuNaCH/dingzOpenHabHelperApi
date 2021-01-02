package ch.hakuna;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;

public class OpenHabConnect {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private static final String OPENHAB_BASEPATH = "/rest/items/";

    private static final String OPENHAB_HOSTNAME = ManageConfig.getOpenhabHostname();
    private static final String OPENHAB_TOKEN = ManageConfig.getOpenhabToken();

    private String openHabUri;
    private String openHabItemName;
    private String action;
    private String button;
    private String mac;

    // Example:
    // https://10.0.1.9:8444/rest/items/dingzButton3/state

    /**
     * Constructor
     */
    public OpenHabConnect(String openHabItemName, String action, String button, String mac) {
        super();

        this.setOpenHabItemName(openHabItemName);
        this.setAction(action);
        this.setButton(button);
        this.setMac(mac);
        this.setOpenHabUri(OPENHAB_HOSTNAME + OPENHAB_BASEPATH + openHabItemName + "/state");
    }


    /**
     * Call GitHub API for getting metadata
     */
    protected String getOpenHabResponse() {

        String result;

        try {
            HttpClient httpclient = HttpClientBuilder.create().build();

            HttpPut httpPut = new HttpPut(this.getOpenHabUri());
            httpPut.addHeader("Authorization", "Bearer " + OPENHAB_TOKEN);
            httpPut.addHeader("Accept", "accept: */*");
            httpPut.addHeader("Content-Type", "text/plain");

            httpPut.setEntity(new StringEntity(this.getAction()));

            HttpResponse response = httpclient.execute(httpPut);
            int responseCode = response.getStatusLine().getStatusCode();

            if (responseCode == 202 || responseCode == 200) {
                // everything is fine, handle the response
                result = "INFO:  Update item '" + openHabItemName + "' to value '" + action + "' from device '" + mac + "(" + button + ")' successfully sent.";

                // check if reset value is required
                if (ManageConfig.getResetMode().equals("true")) {
                    httpPut.setEntity(new StringEntity(ManageConfig.getResetValue()));
                    httpclient.execute(httpPut);
                }

            } else {
                result = "ERROR: Update item '" + openHabItemName + "' to value '" + action + "' from device '" + mac + "(" + button + ")' failed with HTTP" + responseCode + ".";
            }

        } catch (IOException ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            result = "ERROR: OpenHAB not available.";
        }

        return result;
    }

    /* getter and setter */

    public String getOpenHabUri() {
        return openHabUri;
    }

    public void setOpenHabUri(String openHabUri) {
        this.openHabUri = openHabUri;
    }

    public String getOpenHabItemName() {
        return openHabItemName;
    }

    public void setOpenHabItemName(String openHabItemName) {
        this.openHabItemName = openHabItemName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getButton() {
        return button;
    }

    public void setButton(String button) {
        this.button = button;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

}
