package models;

import io.ebean.Finder;
import play.data.validation.Constraints;
import play.libs.F;

import javax.persistence.*;

@Entity
@Table(name = "bulletin_thread")
public class BulletinThread  extends BaseModel {
    public static Finder<Integer, BulletinThread> finder = new Finder<>(BulletinThread.class);

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "title")
    @Constraints.Required
    private String title;

    @Column(name = "body")
    @Constraints.Required
    private String body;

    @Column(name = "name")
    private String name;

    @Column(name = "user_code")
    private String userCode;

    /**
     *
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @return body
     */
    public String getBody() {
        return body;
    }

    /**
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return userCode
     */
    public String getUserCode() {
        return userCode;
    }

    /**
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @param body
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @param userCode
     */
    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
}
