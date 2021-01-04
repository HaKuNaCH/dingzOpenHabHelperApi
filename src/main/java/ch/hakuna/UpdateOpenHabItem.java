package ch.hakuna;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.EmptyStackException;

/**
 * Root resource (exposed at "/" path)
 */
@Path("{resource: [a-zA-Z_0-9\\\\/.-]*}")
public class UpdateOpenHabItem {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    private String itemName;
    private String action;
    private String button;
    private String mac;

    private String allowedDevices;
    private String allowedIps;

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return WSDL, XSD or XML that will be returned as a application/xml response.
     */
    @GET
    @Produces("text/plain; charset=utf-8")
    public String getFile(@Context UriInfo uriInfo, @PathParam("resource") String resource, @Context org.glassfish.grizzly.http.server.Request re) {

        // get allowedDevices string
        allowedDevices = ManageConfig.getAllowedDevices();

        // get allowedIps string
        allowedIps = ManageConfig.getAllowedIps();

        // logoutput format
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        // API call result
        StringBuilder result = new StringBuilder();

        // get ItemName
        setItemName(resource);

        setMac("N/A");
        setAction("N/A");
        setButton("N/A");

        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
        for (String theKey : queryParams.keySet()) {
            if (theKey.equals("index")) {
                setButton(queryParams.getFirst(theKey));
            }
            if (theKey.equals("action")) {
                setAction(queryParams.getFirst(theKey));
            }
            if (theKey.equals("mac")) {
                setMac(queryParams.getFirst(theKey));
            }
        }

        // check if mac/IP is allowed
        if ((!getAllowedDevices().contains(mac) && !getAllowedDevices().contains("ANY")) ||
                (!getAllowedIps().contains(re.getRemoteAddr()) && !getAllowedIps().contains("ANY"))) {
            result.append("ERROR: Commands from device ").append(mac).append(" / IP " + re.getRemoteAddr() + " not allowed.");
            System.out.println(sdf.format(timestamp) + " - " + result);
            throw new EmptyStackException();
        } else {
            // call openHab API
            OpenHabConnect openHabConnect = new OpenHabConnect(this.getItemName(), this.getAction(), this.getButton(), this.getMac());
            String openhabResponse = openHabConnect.getOpenHabResponse();
            System.out.println(sdf.format(timestamp) + " - " + openhabResponse);
            return result.toString();
        }
    }

    /* getter and setter */
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
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

    public void setMac(String mac) { this.mac = mac; }

    public String getAllowedDevices() { return allowedDevices; }

    public void setAllowedDevices(String allowedDevices) {
        this.allowedDevices = allowedDevices;
    }

    public String getAllowedIps() { return allowedIps; }

    public void setAllowedIps(String allowedIps) { this.allowedIps = allowedIps; }

}