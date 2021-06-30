package repositories;

import com.google.inject.ImplementedBy;
import models.ServiceUser;

@ImplementedBy(ServiceUserRepository.class)
public interface IServiceUserRepository {
    public ServiceUser getFromIp(String ip);
    public boolean existCode(String code);
    public void create(ServiceUser user);
}
