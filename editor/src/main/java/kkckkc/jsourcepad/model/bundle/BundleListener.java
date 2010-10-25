package kkckkc.jsourcepad.model.bundle;

public interface BundleListener {
    public void bundleAdded(Bundle bundle);
    public void bundleRemoved(Bundle bundle);
    public void bundleUpdated(Bundle bundle);

    public void languagesUpdated();
}
