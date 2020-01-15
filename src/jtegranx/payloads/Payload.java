package jtegranx.payloads;

public class Payload {

    private final String name;
    private final String version;
    private final String downloadURL;

    public Payload(String name, String version, String downloadURL) {
        this.name = name;
        this.version = version;
        this.downloadURL = downloadURL;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }
    
    public String getDownloadURL() {
        return downloadURL;
    }
}
