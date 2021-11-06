package br.usp.each.saeg.jaguar.maven.plugin;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * @author Guilherme Baufaker Rêgo based in Roberto Araujo Class
*/

public final class JaguarAgentJar {


    private static final String jaguarAgentName = "jaguar.core-1.0.0-jar-with-dependencies";
    private static final String jarExtension = ".jar";
    private static final String fullJaguarName = jaguarAgentName + jarExtension;
    private static final int BUFFER_SIZE = 4096;
    private static final int EOF = -1;
    private static final String RESOURCE = "/" + fullJaguarName;

    private JaguarAgentJar() {
        // No instances
    }

    public static URL getResource() {
        final URL url = JaguarAgentJar.class.getResource(RESOURCE);
        if (url == null) {
            throw new AssertionError("The jaguar agent resource has not been found.");
        }
        return url;
    }

    public static InputStream getResourceAsStream() {
        final InputStream stream = JaguarAgentJar.class.getResourceAsStream(RESOURCE);
        if (stream == null) {
            throw new AssertionError("The jaguar agent resource has not been found.");
        }
        return stream;
    }

    public static File extractJaguarToTempLocation() throws IOException {
        final File agentJar = File.createTempFile(jaguarAgentName, jarExtension);
        agentJar.deleteOnExit();
        extractTo(agentJar);
        return agentJar;
    }

    public static void extractTo(final File destination) throws IOException {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = getResourceAsStream();
            out = new FileOutputStream(destination);
            copy(in, out);
        } finally {
            safeClose(in);
            safeClose(out);
        }
    }

    private static void copy(final InputStream input, final OutputStream output)
            throws IOException {

        final byte[] buffer = new byte[BUFFER_SIZE];

        int n = input.read(buffer);
        while (EOF != n) {
            output.write(buffer, 0, n);
            n = input.read(buffer);
        }

        output.flush();
    }

    private static void safeClose(final Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (final IOException e) {
        }
    }

}