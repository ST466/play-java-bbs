package services;

import com.google.inject.Inject;
import models.ServiceUser;
import repositories.IServiceUserRepository;
import repositories.ServiceUserRepository;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public class ServiceUserService implements IServiceUserService {
    @Inject
    IServiceUserRepository repo;

    @Override
    public ServiceUser getFromIp(String ip) {

        ServiceUser user = repo.getFromIp(ip);

        if (user != null) {
            return user;
        }

        user = new ServiceUser();
        user.setIp(ip);
        String code = generateCode();
        while (repo.existCode(code)) {
            code = generateCode();
        }
        user.setCode(generateCode());
        repo.create(user);

        return user;
    }

    private String generateCode() {
        String uuid = UUID.randomUUID().toString();

        String code = Base64.getEncoder().encodeToString(uuid.getBytes(StandardCharsets.UTF_8)).replace("=", "");

        return Base64.getDecoder().decode(code).toString();
    }
}
