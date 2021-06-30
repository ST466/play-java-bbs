package services;

import com.google.inject.ImplementedBy;
import models.ServiceUser;

@ImplementedBy(ServiceUserService.class)
public interface IServiceUserService {
    public ServiceUser getFromIp(String ip);
}
