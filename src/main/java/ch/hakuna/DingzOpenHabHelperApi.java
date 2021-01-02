package ch.hakuna;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URI;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Main class.
 */
public class DingzOpenHabHelperApi {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    // Base URI the Grizzly HTTP server will listen on
    public static final String API_HOSTNAME_HTTPS = "http://0.0.0.0:8000/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     *
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        final ResourceConfig rc = new ResourceConfig().packages("ch.hakuna");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.printf(sdf.format(timestamp) + " - Jersey server started at " + "%s%n", API_HOSTNAME_HTTPS);

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at API_HOSTNAME:
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(API_HOSTNAME_HTTPS), rc);
    }

    /**
     * Main method.
     */
    public static void main(String[] args) throws Exception {
        getProperties();
        final HttpServer server = startServer();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                server.shutdown();
            }
        });
    }

    /**
     * Read properties
     */
    public static void getProperties() throws Exception {

        try {
            File file = new File("properties.config");
            BufferedReader br = new BufferedReader(new FileReader(file));

            String st;
            while ((st = br.readLine()) != null) {
                if (st.contains("openhabHostname=")) {
                    String[] line = st.split("=");
                    String OPENHABHOSTNAME = line[1];
                    ManageConfig openhabHostname = new ManageConfig();
                    openhabHostname.setOpenhabHostname(OPENHABHOSTNAME);
                }
                if (st.contains("allowedDevices=")) {
                    String[] line = st.split("=");
                    String ALLOWEDDEVICES = line[1];
                    ManageConfig allowedDevices = new ManageConfig();
                    allowedDevices.setAllowedDevices(ALLOWEDDEVICES);
                }
                if (st.contains("Token=")) {
                    String[] line = st.split("=");
                    String TOKEN = line[1];
                    ManageConfig token = new ManageConfig();
                    token.setOpenhabToken(TOKEN);
                }
                if (st.contains("ResetMode=")) {
                    String[] line = st.split("=");
                    String RESETLINE = line[1];
                    ManageConfig resetline = new ManageConfig();
                    resetline.setResetMode(RESETLINE);
                }
                if (st.contains("ResetValue=")) {
                    String[] line = st.split("=");
                    String RESETVALUE = line[1];
                    ManageConfig resetvalue = new ManageConfig();
                    resetvalue.setResetValue(RESETVALUE);
                }
                //System.out.println(st);
            }
        } catch (FileNotFoundException ex) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            System.out.print(sdf.format(timestamp) + " ERROR - file 'properties.config' not found.\n");
            System.exit(0);
        }
    }

}


