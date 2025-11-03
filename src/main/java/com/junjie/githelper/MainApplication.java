package com.junjie.githelper;

import atlantafx.base.theme.PrimerLight;
// import atlantafx.base.theme.PrimerDark; // 深色主题
// import atlantafx.base.theme.NordLight; // 另一个浅色主题选择
// import atlantafx.base.theme.NordDark; // 另一个深色主题选择
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // 应用 AtlantaFX 主题 - Primer Light (GitHub 风格)
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        
        // 如果想要深色主题，可以使用以下任一选项：
        // Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
        // Application.setUserAgentStylesheet(new NordLight().getUserAgentStylesheet());
        // Application.setUserAgentStylesheet(new NordDark().getUserAgentStylesheet());
        
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 700);
        stage.setTitle("Git Commit Helper");
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(500);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
