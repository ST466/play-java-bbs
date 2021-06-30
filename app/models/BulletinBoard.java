package models;

import io.ebean.Finder;
import play.data.validation.Constraints;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bulletin_board")
public class BulletinBoard extends BaseModel {
    public static Finder<Integer, BulletinBoard> finder = new Finder<>(BulletinBoard.class);

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "thread_id")
    private int threadId;

    @Column(name = "name")
    private String name;

    @Column(name = "user_code")
    private String userCode;

    @Constraints.Required
    @Column(name = "body")
    private String body;

    /**
     *
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @return threadId
     */
    public int getThreadId() {
        return threadId;
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
     * @return body
     */
    public String getBody() {
        return body;
    }

    /**
     *
     * @param threadId
     */
    public void setThreadId(int threadId) {
        this.threadId = threadId;
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
     * @param body
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     *
     * @param userCode
     */
    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
}
