package birintsev.view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.MalformedURLException;
import java.net.URL;

public class GameOfLiveApplication extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        GameOfLiveApplication.class
    );

    @Override
    public void start(Stage primaryStage) {
        WebView webView = new WebView();
        Scene scene = new Scene(webView);
        primaryStage.setScene(scene);
        URL backendURL;
        try {
            backendURL = new URL(
                "http",
                "localhost",
                serverPort(),
                "gameOfLife/parallel/start"
            );
        } catch (MalformedURLException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        primaryStage.setHeight(740);
        primaryStage.setWidth(480);
        webView.getEngine().load(backendURL.toString());
        webView.setVisible(true);
        primaryStage.show();
    }

    @Override
    public void stop() {
        System.exit(0);
    }

    private int serverPort() {
        return Integer.parseInt(System.getProperty("server.port"));
    }
}
