package cn.ms22.entity;

import java.util.List;
import java.util.Objects;

/**
 * 带激活码的Order
 *
 * @author baopz
 */
public class CodeOrder implements Formator{
    private String username;
    private String platform;
    private String name;
    private String code;
    /**
     * dlc扩展
     */
    private List<String> codeExs;
    @Override
    public String format() {
        return username + "-" + name + "-" + code;
    }

    @Override
    public String toString() {
        return "CodeOrder{" +
                "username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", platform='" + platform + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CodeOrder)) return false;
        CodeOrder codeOrder = (CodeOrder) o;
        return username.equals(codeOrder.username) &&
                name.equals(codeOrder.name) &&
                code.equals(codeOrder.code) &&
                platform.equals(codeOrder.platform);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, name, code, platform);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public List<String> getCodeExs() {
        return codeExs;
    }

    public void setCodeExs(List<String> codeExs) {
        this.codeExs = codeExs;
    }

}
