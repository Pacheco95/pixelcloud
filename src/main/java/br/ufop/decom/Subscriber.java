package br.ufop.decom;

import br.ufop.decom.dataStructures.Pixel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("unused")
public class Subscriber extends CloudClient {

    public Subscriber() {
        super(LogManager.getLogger());
    }

    /**
     * Subscribe on a single quadrant of the cloud.
     * Thus, every change in the given quadrant will be notified to this subscriber.
     *
     * @param quadrant the index of the interest quadrant.
     * */
    public void subscribe(int quadrant) {
        try {
            endpointOut.writeObject(quadrant);
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
                // TODO update GUI
            }
        } catch (IOException | ClassNotFoundException e) {
            if (!endpointSocket.isClosed())
                e.printStackTrace();
            else logger.info("Endpoint connection closed!");
        }
    }

    public Parent loadGUI() {
        Parent root = null;
        try {
            File cloudViewFile = new File("view/cloud_view.fxml");
            root = FXMLLoader.load(cloudViewFile.toURI().toURL());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return root;
    }
}
