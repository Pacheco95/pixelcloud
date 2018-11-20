package br.ufop.decom.client;

import br.ufop.decom.controllers.CloudGridController;
import br.ufop.decom.util.Pixel;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;

@SuppressWarnings("unused")
public class Subscriber extends CloudClient {

    private CloudGridController cloudGridController;

    public Subscriber(CloudGridController cloudGridController) {
        super(LogManager.getLogger());
        this.cloudGridController = cloudGridController;
    }

    /**
     * Subscribe on a single quadrant of the cloud.
     * Thus, every change in the given quadrant will be notified to this subscriber.
     *
     * @param quadX the x index of the interest quadrant.
     * @param quadY the x index of the interest quadrant.
     * */
    void subscribe(int quadX, int quadY) {
        try {
            endpointOut.writeObject(new Object[] { quadX, quadY });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method runs in an infinite loop.
     * Receive a notify message every time that a change on the subscribed quadrant occurs.
     * */
    public void update() {
        try {
            while (!endpointSocket.isClosed()) {
                Pixel newPixel = (Pixel) endpointIn.readObject();
                cloudGridController.drawPixel(newPixel);
            }
        } catch (IOException | ClassNotFoundException e) {
            if (!endpointSocket.isClosed())
                e.printStackTrace();
            else logger.info("Endpoint connection closed!");
        }
    }
}
