package br.ufop.decom.client;

import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@SuppressWarnings({"FieldCanBeLocal", "WeakerAccess", "unused"})
public class CloudClient {
    protected Socket cloudSocket;
    protected ObjectInputStream cloudIn;

    protected Socket endpointSocket;
    protected ObjectOutputStream endpointOut;
    protected ObjectInputStream endpointIn;

    protected Logger logger;

    public CloudClient(Logger logger) {
        this.logger = logger;
    }

    /**
     * Connect to pixel cloud server.
     * @param serverAddress the server IP address.
     * @param serverPort the server port service.
     * */
    public void connect(String serverAddress, int serverPort) throws IOException {
        cloudSocket = new Socket(serverAddress, serverPort);
        cloudIn = new ObjectInputStream(cloudSocket.getInputStream());

        new Thread(this::updateEndpoint).start();
    }

    /**
     * Disconnect from the cloud server
     * */
    public void disconnect() throws IOException {
        cloudSocket.close();
        endpointSocket.close();
    }

    /**
     * Runs in an infinite loop.
     * Receives a message with the assigned endpoint after connecting to the cloud or when the connected endpoint breaks.
     * */
    public synchronized void updateEndpoint() {
        while (cloudSocket.isConnected()) {
            try {
                // Receive the new endpoint address and port
                String[] connectionInfo = (String[]) cloudIn.readObject();
                String serverAddress = connectionInfo[0];
                int serverPort = Integer.parseInt(connectionInfo[1]);

                // Close current connection if already connected
                if (endpointSocket != null && !endpointSocket.isClosed()) endpointSocket.close();

                // Renew the connection
                endpointSocket = new Socket(serverAddress, serverPort);
                endpointOut = new ObjectOutputStream(endpointSocket.getOutputStream());
                endpointIn = new ObjectInputStream(endpointSocket.getInputStream());
            } catch (IOException | ClassNotFoundException e) {
                if (!cloudSocket.isClosed())
                    e.printStackTrace();
                else
                    logger.info("Cloud connection closed!");
            }
        }
    }
}
