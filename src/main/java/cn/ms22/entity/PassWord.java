package cn.ms22.entity;

/**
 * @author baopz
 * @date 2019.02.18
 */
public class PassWord {
    private String username;
    private String password;

    @Override
    public String toString() {
        return username + " " + password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
