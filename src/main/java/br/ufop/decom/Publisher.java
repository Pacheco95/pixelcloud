package br.ufop.decom;


import br.ufop.decom.dataStructures.Pixel;
import br.ufop.decom.util.Utils;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.util.Random;

/**
 * This class represents agents that will publish writes on the cloud.
 * */
@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public class Publisher extends CloudClient {


    public Publisher() {
        super(LogManager.getLogger());
    }

    /**
     * Writes random colored pixels on the cloud.
     * */
    public void publish() {
        Random random = new Random();
        while (!endpointSocket.isClosed()) {
            try {
                short x = (short) random.nextInt(Utils.ROWS);
                short y = (short) random.nextInt(Utils.ROWS);
                byte r = (byte) random.nextInt(256);
                byte g = (byte) random.nextInt(256);
                byte b = (byte) random.nextInt(256);

                endpointOut.writeObject(new Pixel(x, y, r, g, b));

                Thread.sleep(Utils.PUBLISH_MAX_DELAY);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
