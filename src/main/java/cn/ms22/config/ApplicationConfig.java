package cn.ms22.config;

/**
 * @author baopz
 * @date 2019.02.18
 */
public final class ApplicationConfig {
    private String driver;
    private int parallels;
    private String passwords;
    private String runApplication;
    private String output;
    private String place;

    public int getPasswdsCount() {
        return passwdsCount;
    }

    public void setPasswdsCount(int passwdsCount) {
        this.passwdsCount = passwdsCount;
    }

    private int passwdsCount;

    @Override
    public String toString() {
        return "ApplicationConfig{" +
                "driver='" + driver + '\'' +
                ", parallels=" + parallels +
                ", passwords='" + passwords + '\'' +
                ", runApplication='" + runApplication + '\'' +
                ", output='" + output + '\'' +
                ", place='" + place + '\'' +
                ", passwdsCount=" + passwdsCount +
                '}';
    }

    private static ApplicationConfig applicationConfig;

    private ApplicationConfig() {

    }

    public synchronized static ApplicationConfig getInstance() {
        if (applicationConfig == null) {
            applicationConfig = new ApplicationConfig();
        }
        return applicationConfig;
    }

    public String getDriver() {
        return driver;
    }


    public String getRunApplication() {
        return runApplication;
    }

    public void setRunApplication(String runApplication) {
        this.runApplication = runApplication;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public int getParallels() {
        return parallels;
    }

    public void setParallels(int parallels) {
        this.parallels = parallels;
    }

    public String getPasswords() {
        return passwords;
    }

    public void setPasswords(String passwords) {
        this.passwords = passwords;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}
