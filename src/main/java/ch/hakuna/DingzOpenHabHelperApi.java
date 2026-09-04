package ch.hakuna;

import com.sun.net.httpserver.HttpServer;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.URI;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Main class.
 */
public class DingzOpenHabHelperApi {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");

    /**
     * Starts the JDK HTTP server exposing JAX-RS resources defined in this application.
     *
     * @return JDK HTTP server.
     */
    public static HttpServer startServer() {
        final ResourceConfig rc = new ResourceConfig().packages("ch.hakuna");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println(sdf.format(timestamp) + " - INFO:  Jersey server starting at " + ManageConfig.getExposeApiName());
        // create and start a new instance of the JDK HTTP server
        // exposing the Jersey application at ManageConfig.getExposeApiName():
        PrintStream newErr = new PrintStream(new ByteArrayOutputStream());
        System.setErr(newErr);
        return JdkHttpServerFactory.createHttpServer(URI.create(ManageConfig.getExposeApiName()), rc);
    }

    /**
     * Main method.
     */
    public static void main(String[] args) throws IOException {
        getProperties();
        final HttpServer server = startServer();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> server.stop(0)));
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println(sdf.format(timestamp) + " - INFO:  Jersey server started at " + ManageConfig.getExposeApiName());
    }

    /**
     * Read properties
     */
    public static void getProperties() throws IOException {

        try (BufferedReader br = Files.newBufferedReader(Paths.get("properties.config"), StandardCharsets.UTF_8)) {
            String st;
            while ((st = br.readLine()) != null) {
                if (st.startsWith("exposeApiName=")) {
                    String[] line = st.split("=", 2);
                    String EXPOSEAPINAME = line[1];
                    ManageConfig.setExposeApiName(EXPOSEAPINAME);
                }
                if (st.startsWith("openhabHostname=")) {
                    String[] line = st.split("=", 2);
                    String OPENHABHOSTNAME = line[1];
                    ManageConfig.setOpenhabHostname(OPENHABHOSTNAME);
                }
                if (st.startsWith("allowedDevices=")) {
                    String[] line = st.split("=", 2);
                    String ALLOWEDDEVICES = line[1];
                    ManageConfig.setAllowedDevices(ALLOWEDDEVICES);
                }
                if (st.startsWith("allowedIps=")) {
                    String[] line = st.split("=", 2);
                    String ALLOWEDIPS = line[1];
                    ManageConfig.setAllowedIps(ALLOWEDIPS);
                }
                if (st.startsWith("Token=")) {
                    String[] line = st.split("=", 2);
                    String TOKEN = line[1];
                    ManageConfig.setOpenhabToken(TOKEN);
                }
                if (st.startsWith("ResetMode=")) {
                    String[] line = st.split("=", 2);
                    String RESETLINE = line[1];
                    ManageConfig.setResetMode(RESETLINE);
                }
                if (st.startsWith("ResetValue=")) {
                    String[] line = st.split("=", 2);
                    String RESETVALUE = line[1];
                    ManageConfig.setResetValue(RESETVALUE);
                }
            }
        } catch (FileNotFoundException ex) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            System.out.println(sdf.format(timestamp) + " ERROR: File 'properties.config' not found.");
            System.exit(0);
        }
    }

}


