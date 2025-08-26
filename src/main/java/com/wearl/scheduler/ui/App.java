package com.wearl.scheduler.ui;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        var controller = new MainController();
        stage.setTitle("CPU Task Scheduler");
        stage.setScene(controller.createScene());
        stage.show();
    }
    public static void main(String[] args) { launch(args); }
}
