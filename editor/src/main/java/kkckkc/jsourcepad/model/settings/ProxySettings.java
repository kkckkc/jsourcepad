package kkckkc.jsourcepad.model.settings;

public class ProxySettings implements SettingsManager.Setting {

    public static enum ProxyType { NO_PROXY, SYSTEM_PROXY, MANUAL_PROXY }

    private ProxyType proxyType;
    private String proxyHost;
    private String proxyPort;
    
    public ProxySettings() {
    }

    public ProxyType getProxyType() {
        return proxyType;
    }

    public void setProxyType(ProxyType proxyType) {
        this.proxyType = proxyType;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public String getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(String proxyPort) {
        this.proxyPort = proxyPort;
    }

    public void apply() {
        System.clearProperty("java.net.useSystemProxies");
        System.clearProperty("http.proxyPort");
        System.clearProperty("http.proxyHost");

        if (proxyType == ProxyType.SYSTEM_PROXY) {
            System.setProperty("java.net.useSystemProxies", "true");
        } else if (proxyType == ProxyType.MANUAL_PROXY) {
            System.setProperty("http.proxyPort", proxyPort);
            System.setProperty("http.proxyHost", proxyHost);
        }
    }

    @Override
    public SettingsManager.Setting getDefault() {
        ProxySettings proxySettings = new ProxySettings();
        proxySettings.setProxyType(ProxyType.SYSTEM_PROXY);
        return proxySettings; 
    }
}
