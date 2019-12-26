package liusheng.downloadCore.pane;

import com.jfoenix.controls.JFXListView;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import liusheng.downloadCore.FailListExecutorService;
import liusheng.downloadCore.entity.AbstractVideoBean;
import liusheng.downloadCore.entity.DownloadItemPaneEntity;
import liusheng.downloadCore.util.BindUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class DownloadingPaneContainer extends VBox {
    private JFXListView<DownloadItemPaneEntity> listView;
    private List<Object> indies = Collections.synchronizedList(new LinkedList<>());
    private AtomicInteger indexGenerator = new AtomicInteger();
    private DownloadPane downloadPane;

    public DownloadPane getDownloadPane() {
        return downloadPane;
    }

    public int next() {
        return indexGenerator.getAndIncrement();
    }

    public List<Object> getIndies() {
        return indies;
    }

    public JFXListView<DownloadItemPaneEntity> getListView() {
        return listView;
    }

    private final Semaphore semaphore = new Semaphore(2);

    public DownloadingPaneContainer(DownloadPane downloadPane) {
        super();
        this.downloadPane = downloadPane;

       /* FailListExecutorService.commonExecutorService().execute(()->{
            while (true) {

                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(()->{
                    indexMap.forEach((i,com)->{
                        AtomicLong size = com.getEntity().getAbstractVideoBean().getSize();
                        DownloadItemPane downloadItemPane = com.getDownloadItemPane();

                        downloadItemPane.getDownloadProgressBar().setProgress(size.get());
                    });
                });

            }
        });
        */
        FailListExecutorService.commonExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    Platform.runLater(() -> {
                        IntStream.range(0, indies.size()).forEach(i -> {
                            Object a = indies.get(i);
                            if (a instanceof AbstractVideoBean) {
                                AbstractVideoBean videoBean = (AbstractVideoBean) a;
                                long total = videoBean.getAllSize().get();
                                long part = videoBean.getSize().get();

                                if (total == 0) return;
                                DownloadItemPane pane = (DownloadItemPane) videoBean.getPane();

                                double value = part * 1.0 / total;
                                pane.getDownloadProgressBar().setProgress(value);
                                pane.getProgressValue().setText(String.format("%.2f%%", value * 100));

                            }
                        });
                    });
                }
            }
        });
        listView = new JFXListView<>();
        listView.setCellFactory(list -> {

            ListCell<DownloadItemPaneEntity> listCell = new ListCell<DownloadItemPaneEntity>() {
                @Override
                protected void updateItem(DownloadItemPaneEntity item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty && item != null) {
                        int index = getIndex();
                        Node pane = ((AbstractVideoBean) indies.get(index)).getPane();
                        setGraphic(pane);
                    } else if (empty) {
                        setGraphic(null);
                    }
                }
            };

            BindUtils.bind(listCell.prefHeightProperty(), listView.heightProperty().multiply(0.2));
            BindUtils.bind(listCell.prefWidthProperty(), listView.widthProperty());
            return listCell;
        });
        BindUtils.bind(listView.prefHeightProperty(), this.heightProperty());
        BindUtils.bind(listView.prefWidthProperty(), this.widthProperty());
        this.getChildren().addAll(listView);

    }

}
