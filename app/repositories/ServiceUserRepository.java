package repositories;

import io.ebean.annotation.Transactional;
import models.ServiceUser;

public class ServiceUserRepository implements IServiceUserRepository {
    @Override
    public ServiceUser getFromIp(String ip) {
        return ServiceUser.finder.query().where().eq("ip", ip).findOne();
    }
    @Override
    public boolean existCode(String code) {
        return ServiceUser.finder.query().where().eq("code", code).exists();
    }
    @Override
    @Transactional
    public void create(ServiceUser user) {
        user.save();
    }
}
