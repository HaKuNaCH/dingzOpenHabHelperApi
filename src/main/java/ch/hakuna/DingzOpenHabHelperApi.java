package ch.hakuna;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.*;
import java.net.URI;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Main class.
 */
public class DingzOpenHabHelperApi {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     *
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        final ResourceConfig rc = new ResourceConfig().packages("ch.hakuna");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println(sdf.format(timestamp) + " - INFO:  Jersey server starting at " + ManageConfig.getExposeApiName());
        // create and start a new instance of grizzly http server
        // exposing the Jersey application at ManageConfig.getExposeApiName():
        PrintStream newErr = new PrintStream(new ByteArrayOutputStream());
        System.setErr(newErr);
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(ManageConfig.getExposeApiName()), rc);
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
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println(sdf.format(timestamp) + " - INFO:  Jersey server started at " + ManageConfig.getExposeApiName());
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
                if (st.contains("exposeApiName=")) {
                    String[] line = st.split("=");
                    String EXPOSEAPINAME = line[1];
                    ManageConfig exposeApiName = new ManageConfig();
                    exposeApiName.setExposeApiName(EXPOSEAPINAME);
                }
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
                if (st.contains("allowedIps=")) {
                    String[] line = st.split("=");
                    String ALLOWEDIPS = line[1];
                    ManageConfig allowedIps = new ManageConfig();
                    allowedIps.setAllowedIps(ALLOWEDIPS);
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
            }
        } catch (FileNotFoundException ex) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            System.out.println(sdf.format(timestamp) + " ERROR: File 'properties.config' not found.");
            System.exit(0);
        }
    }

}


