package models;

import io.ebean.Finder;
import io.ebean.Model;
import jdk.nashorn.internal.objects.annotations.Getter;
import jdk.nashorn.internal.objects.annotations.Setter;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@MappedSuperclass
public class BaseModel extends Model {

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    @PrePersist
    public void onPrePersist() {
        createdAt = new Date();
        updatedAt = new Date();
    }

    @PreUpdate
    public void onPreUpdate() {
        updatedAt = new Date();
    }

    /**
     *
     * @return createdAt
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     *
     * @return updatedAt
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     *
     * @return createdAt (yyyy-MM-dd HH:mm:ss)
     */
    public String getCreatedAtWithFormat() {
        return dateWithFormat(createdAt);
    }

    /**
     *
     * @return updatedAt (yyyy-MM-dd HH:mm:ss)
     */
    public String getUpdatedAtWithFormat() {
        return dateWithFormat(updatedAt);
    }

    private String dateWithFormat(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(date);
    }
}
