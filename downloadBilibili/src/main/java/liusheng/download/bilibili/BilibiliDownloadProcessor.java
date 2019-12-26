package liusheng.download.bilibili;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import liusheng.downloadCore.*;
import liusheng.downloadCore.entity.AbstractVideoBean;
import liusheng.downloadCore.entity.DownloadItemPaneEntity;
import liusheng.downloadCore.pane.DownloadItemPane;
import liusheng.downloadCore.pane.DownloadingPaneContainer;
import liusheng.downloadCore.util.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BilibiliDownloadProcessor implements DownloadProcessor {

    private final List<AllPageBean> pageBeanList;
    private final PagesBean pagesBean;
    private final Logger logger = Logger.getLogger(BilibiliDownloadProcessor.class);
    private final Semaphore semaphore;

    public BilibiliDownloadProcessor(List<AllPageBean> pageBeanList, PagesBean pagesBean, Semaphore semaphore) {
        this.pageBeanList = pageBeanList;
        this.pagesBean = pagesBean;
        this.semaphore = semaphore;
    }

    @Override
    public void before(JFXAlert<Object> alert, JFXComboBox<Label> quality, JFXListView<DownloadListDialogItemPane> listView, DownloadingPaneContainer downloadingPaneContainer) {
        List<DownloadListDialogItemPane> list = pageBeanList.stream().map(allPageBean -> {
            int page = allPageBean.getPage();
            String part = allPageBean.getPart();
            return new DownloadListDialogItemPane(String.valueOf(page), part);
        }).collect(Collectors.toList());
        listView.getItems().addAll(list);
    }

    @Override
    public void processor(JFXAlert<Object> alert, JFXComboBox<Label> quality,
                          JFXListView<DownloadListDialogItemPane> listView, DownloadingPaneContainer downloadingPaneContainer) {


        SingleSelectionModel<Label> selectionModel = quality.getSelectionModel();
        selectionModel.selectLast();
        int size = quality.getItems().size();

        int i = IntStream.range(0, size).filter(selectionModel::isSelected).findFirst().orElse(size - 1);

        ObservableList<DownloadListDialogItemPane> items = listView.getItems();

        List<AllPageBean> selectAllPageBean = IntStream.range(0, items.size()).filter(index -> items.get(index).getCheckBox().isSelected())
                .boxed().map(pageBeanList::get).collect(Collectors.toList());
        // 这个视频的名字
        String videoName = Optional.ofNullable(pagesBean).map(p -> p.getVideoData()).map(videoDataBean -> StringUtils.isEmpty(videoDataBean.getTitle())
                ? UUID.randomUUID().toString() : StringUtils.fileNameHandle(videoDataBean.getTitle())).get();
        FailListExecutorService.commonExecutorService().execute(new FailTask(() -> {
            selectAllPageBean.forEach(page -> {

                try {
                    int index = page.getPage();

                    // 文件名字 ，不包含后缀
                    String name = index + "_" + page.getPart();

                    // 每一p的 url
                    String vUrl = pagesBean.getUrl() + "?p=" + index;
                    // 打印日志
                    logger.debug("Start Parse " + vUrl);
                    // 找到合适的解析器 ,解析对象，有两种类型 NewVideoBean 和 OldVideoBean
                    AdapterParam param = new AdapterParam();

                    param.setUrl(vUrl);
                    param.getMap().put("cid", pagesBean.getVideoData().getCid());
                    param.getMap().put("aid", pagesBean.getAid());
                    AbstractVideoBean abstractVideoBean = (AbstractVideoBean) new DefaultParserAdapter().handle(param).parse(vUrl);
                    abstractVideoBean.setName(StringUtils.fileNameHandle(name));
                    abstractVideoBean.setDirFile(new File("f:\\hello1\\" + videoName));
                    abstractVideoBean.setUrl(vUrl);
                    abstractVideoBean.setSize(new AtomicLong());
                    abstractVideoBean.setAllSize(new AtomicLong());
                    abstractVideoBean.setDownloadPane(downloadingPaneContainer.getDownloadPane());
                    // 进行下载 ,处理数据

                    JFXListView<DownloadItemPaneEntity> listView1 = downloadingPaneContainer.getListView();
                    DownloadItemPane downloadItemPane = new DownloadItemPane(new DefaultDownloaderController());
                    abstractVideoBean.setPane(downloadItemPane);
                    downloadItemPane.setListView(listView1);
                    List<Object> indies = downloadingPaneContainer.getIndies();


                    indies.add(abstractVideoBean);
                    DownloadItemPaneEntity e1 = new DownloadItemPaneEntity(abstractVideoBean, i);

                    downloadItemPane.setEntity(e1);
                    FailListExecutorService.commonExecutorService().execute(new FailTask(() -> {
                        if (abstractVideoBean instanceof NewVideoBean) {
                            new NewVideoBeanDownloader(semaphore, indies).download((NewVideoBean) abstractVideoBean);
                        } else if (abstractVideoBean instanceof OldVideoBean) {
                            new OldVideoBeanDownloader(semaphore, indies).download((OldVideoBean) abstractVideoBean);
                        } else {
                            throw new IllegalArgumentException();
                        }
                    }));

                    Platform.runLater(() -> {

                        listView1.getItems().add(e1);
                    });

                } catch (IOException ex) {
                    throw new RuntimeException();
                }

            });

        }));
        alert.close();
    }
}
