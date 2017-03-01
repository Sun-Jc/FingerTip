package zx.Network.View;


public class Item {
    private String maintitle;
    private String subtitle;
    private boolean active;

    public Item(String maintitle, String subtitle, boolean active) {
        this.maintitle = maintitle;
        this.subtitle = subtitle;
        this.active = active;
    }

    public Item(String maintitle, String subtitle) {
        this.maintitle = maintitle;
        this.subtitle = subtitle;
    }

    public String getTitle() {
        return maintitle;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public boolean isActive() {
        return active;
    }
}
