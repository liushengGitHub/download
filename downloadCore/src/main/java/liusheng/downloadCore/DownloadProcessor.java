package liusheng.downloadCore;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import javafx.scene.control.Label;
import liusheng.downloadCore.pane.DownloadingPaneContainer;

public interface DownloadProcessor {
    void processor(JFXAlert<Object> alert, JFXComboBox<Label> quality, JFXListView<DownloadListDialogItemPane> listView, DownloadingPaneContainer downloadingPaneContainer);
    default void before(JFXAlert<Object> alert, JFXComboBox<Label> quality, JFXListView<DownloadListDialogItemPane> listView, DownloadingPaneContainer downloadingPaneContainer){}
    default void after(JFXAlert<Object> alert, JFXComboBox<Label> quality, JFXListView<DownloadListDialogItemPane> listView, DownloadingPaneContainer downloadingPaneContainer){}
}
