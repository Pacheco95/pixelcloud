package br.ufop.decom.client;

import br.ufop.decom.util.Commons;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;

public class GridViewerApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = loadScene();

        stage.setTitle(Commons.TITLE);
        stage.getIcons().add(Commons.APP_ICON);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    private Scene loadScene() throws java.io.IOException {
        File cloudViewFile = new File(Commons.RESOURCES_PATH + "/view/cloud_grid_view.fxml");
        FXMLLoader loader = new FXMLLoader(cloudViewFile.toURI().toURL());
        return new Scene(loader.load());
    }

}
