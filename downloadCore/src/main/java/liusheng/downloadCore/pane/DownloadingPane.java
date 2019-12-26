package liusheng.downloadCore.pane;

import com.jfoenix.controls.JFXButton;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import liusheng.downloadCore.util.BindUtils;
import liusheng.downloadInterface.DownloaderController;

public class DownloadingPane extends VBox {

    private DownloadingPaneController downloadingPaneController;
    private DownloadingPaneContainer downloadingPaneContainer;
    private DownloadPane downloadPane;

    public DownloadingPaneController getDownloadingPaneController() {
        return downloadingPaneController;
    }

    public DownloadingPaneContainer getDownloadingPaneContainer() {
        return downloadingPaneContainer;
    }

    public DownloadingPane(DownloadPane downloadPane) {
        super();
        this.downloadPane = downloadPane;

        downloadingPaneContainer = new DownloadingPaneContainer(downloadPane);
        downloadingPaneController = new DownloadingPaneController();

        downloadingPaneController.getAllCancel().setOnAction((e) -> {
            downloadingPaneContainer.getListView().getItems().forEach(entity -> {
                Node pane = entity.getAbstractVideoBean().getPane();

                if (pane instanceof DownloadItemPane) {
                    DownloadItemPane itemPane = (DownloadItemPane) pane;
                    itemPane.getLocal().cancel();
                }
            });
        });

        JFXButton allPause = downloadingPaneController.getAllPause();
        allPause.setOnAction(e -> {
            downloadingPaneContainer.getListView().getItems().forEach(entity -> {
                Node pane = entity.getAbstractVideoBean().getPane();

                if (pane instanceof DownloadItemPane) {
                    DownloadItemPane itemPane = (DownloadItemPane) pane;

                    DownloaderController local = itemPane.getLocal();


                    int state = local.getState();
                    if (state == DownloaderController.EXECUTE) {
                        allPause.setText("全部开始");
                    } else if (state == DownloaderController.PAUSE) {
                        allPause.setText("全部暂停");
                    }
                    local.pause();
                }
            });
        });

        BindUtils.bind(downloadingPaneController.prefWidthProperty(), this.widthProperty());
        BindUtils.bind(downloadingPaneController.prefHeightProperty(), this.heightProperty().multiply(0.1));
        BindUtils.bind(downloadingPaneContainer.prefWidthProperty(), this.widthProperty());
        BindUtils.bind(downloadingPaneContainer.prefHeightProperty(), this.heightProperty().multiply(0.9));

        downloadingPaneController.setStyle("-fx-background-color: white");
        downloadingPaneContainer.setStyle("-fx-background-color: grey");

        this.getChildren().addAll(downloadingPaneController, downloadingPaneContainer);
    }
}
