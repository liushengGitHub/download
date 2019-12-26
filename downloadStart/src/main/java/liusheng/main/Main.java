package liusheng.main;

import com.jfoenix.controls.JFXButton;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.Mnemonic;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import liusheng.downloadCore.pane.DownloadPane;
import liusheng.downloadCore.search.SearchAction;
import liusheng.downloadCore.search.SearchPane;
import liusheng.downloadCore.util.BindUtils;
import liusheng.downloadCore.util.PluginUtils;
import liusheng.downloadInterface.SearchPlugin;
import liusheng.downloadInterface.SearchPluginHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox main = new VBox();

        Map<Object, Node> controllerMap = new HashMap<>();

        HBox select = new HBox();
        select.setStyle("-fx-background-color: red");
        JFXButton searchButton = new JFXButton("搜索");
        JFXButton downloadButton = new JFXButton("下载");
        SearchPane searchPane = new SearchPane();
        controllerMap.put(searchButton, searchPane);

        searchButton.setOnAction(e -> {


            ObservableList<Node> children = main.getChildren();
            int size = children.size();
            // 这个事件源对象就是button

            if (size > 1) {
                children.remove(1, size);
            }
            children.addAll(searchPane);

        });
        DownloadPane downloadPane = new DownloadPane();
        downloadPane.setStyle("-fx-background-color: green");
        BindUtils.bind(downloadPane.prefWidthProperty(), main.widthProperty());
        BindUtils.bind(downloadPane.prefHeightProperty(), main.heightProperty().subtract(50));

        controllerMap.put(searchButton, searchPane);
        controllerMap.put(downloadButton, downloadPane);
        downloadButton.setOnAction(e -> {
            ObservableList<Node> children = main.getChildren();
            int size = children.size();
            // 这个事件源对象就是button

            if (size > 1) {
                Node node = children.get(size - 1);
                children.remove(1, size);
            }
            children.addAll(downloadPane);
        });
        // 绑定.实现动态变化
        //  BindUtils.bind(select.prefHeightProperty(), main.heightProperty().multiply(2 / 15.0));
        select.setPrefHeight(50);
        BindUtils.bind(select.prefWidthProperty(), main.widthProperty());
        BindUtils.bind(searchButton.prefWidthProperty(), select.widthProperty().multiply(0.5));
        BindUtils.bind(downloadButton.prefWidthProperty(), select.widthProperty().multiply(0.5));
        BindUtils.bind(searchButton.prefHeightProperty(), select.heightProperty());
        BindUtils.bind(downloadButton.prefHeightProperty(), select.heightProperty());
        BindUtils.bind(searchPane.prefHeightProperty(), main.heightProperty().subtract(50));

        select.getChildren().addAll(searchButton, downloadButton);

        main.getChildren().addAll(select, searchPane);

        Scene scene = new Scene(main, 600, 480);

        List<SearchPluginHolder> ps = PluginUtils.getSearchPlugins()
                .stream().map(SearchPlugin::get).collect(Collectors.toList());
        if (!ps.isEmpty()) {
            searchPane.getController().getSearchText().setOnAction(new SearchAction(searchPane.getController().getComboBox(),
                    searchPane.getController().getSearchText(), ps
                    , searchPane, downloadPane));
            // 触发Action事件的
            scene.addMnemonic(new Mnemonic(searchPane.getController().getSearchText(),
                    new KeyCodeCombination(KeyCode.ENTER)));
        }
        searchPane.getController().getSearchButton().setOnAction(new SearchAction(searchPane.getController().getComboBox(),
                searchPane.getController().getSearchText(), ps
                , searchPane, downloadPane));
        primaryStage.setOnCloseRequest(e -> {
            System.exit(1);
        });
        primaryStage.setScene(scene);
        primaryStage.show();

        // 要放在最后面
        searchPane.getController().getSearchText().requestFocus();
    }

    /*private ObservableList<Node> removeNonFirst(VBox main) {

    }*/
}
