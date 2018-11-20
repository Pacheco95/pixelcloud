package br.ufop.decom.util;

import javafx.scene.image.Image;

@SuppressWarnings("ALL")
public class Commons {
    public static final int WIDTH = 400;
    public static final int HEIGHT = 300;
    public static final int QUAD_X = 4;
    public static final int QUAD_Y = 3;
    public static final String TITLE = "Pixel Cloud Client";
    public static final String RESOURCES_PATH = "src/main/resources/";
    public static final String IMG_PATH = RESOURCES_PATH + "images/";
    public static final Image APP_ICON = new Image("file:" + RESOURCES_PATH + "images/icon_32x32.png");
}
