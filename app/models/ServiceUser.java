package models;

import io.ebean.Finder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "service_user")
public class ServiceUser extends BaseModel {
    public static Finder<Integer, ServiceUser> finder = new Finder<>(ServiceUser.class);

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "ip")
    private String ip;

    @Column(name = "code")
    private String code;

    /**
     *
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @return ip
     */
    public String getIp() {
        return ip;
    }

    /**
     *
     * @return code
     */
    public String getCode() {
        return code;
    }

    /**
     *
     * @param ip
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     *
     * @param code
     */
    public void setCode(String code) {
        this.code = code;
    }
}
