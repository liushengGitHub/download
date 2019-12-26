package liusheng.downloadCore.entity;

public class DownloadItemPaneEntity {
    private AbstractVideoBean abstractVideoBean;
    private int quality;

    public AbstractVideoBean getAbstractVideoBean() {
        return abstractVideoBean;
    }

    public int getQuality() {
        return quality;
    }

    public DownloadItemPaneEntity(AbstractVideoBean abstractVideoBean, int quality) {

        this.abstractVideoBean = abstractVideoBean;
        this.quality = quality;
    }
}
