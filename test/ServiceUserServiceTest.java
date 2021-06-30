import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Injector;
import models.ServiceUser;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.WithApplication;
import repositories.IServiceUserRepository;
import services.IServiceUserService;
import services.ServiceUserService;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static play.inject.Bindings.bind;

public class ServiceUserServiceTest extends WithApplication {
    IServiceUserService service;
    @Override
    protected Application provideApplication() {
        Map<String, Object> configMap = ImmutableMap.of(
                "db.default.driver" ,"org.h2.Driver",
                "db.default.url" , "jdbc:h2:mem:play"
        );
        return new GuiceApplicationBuilder()
                .configure(configMap)
                .overrides(bind(IServiceUserRepository.class).to(MockServiceUserRepository.class))
                .build();


    }
    @Before
    public void init() {
        service = app.injector().instanceOf(ServiceUserService.class);
        MockServiceUserRepository.users = new ArrayList<>();
    }

    @Test
    public void testGetNewUser() {
        String ip = "127.0.0.1";

        ServiceUser user = service.getFromIp(ip);

        assertEquals(ip, user.getIp());
        assertNotNull(user.getCode());

        assertEquals(1, MockServiceUserRepository.users.size());
        assertEquals(ip, MockServiceUserRepository.users.get(0).getIp());
    }

    @Test
    public void testGetExistingUser() {
        ServiceUser testUser1 = new ServiceUser();
        testUser1.setIp("127.0.0.1");
        testUser1.setCode("test1");

        ServiceUser testUser2 = new ServiceUser();
        testUser2.setIp("192.168.0.1");
        testUser2.setCode("test2");

        MockServiceUserRepository.users.add(testUser1);
        MockServiceUserRepository.users.add(testUser2);

        ServiceUser user = service.getFromIp("127.0.0.1");

        assertEquals("127.0.0.1", user.getIp());
        assertEquals("test1", user.getCode());

        user = MockServiceUserRepository.users.get(0);
        assertEquals("127.0.0.1", user.getIp());
        assertEquals("test1", user.getCode());

        assertEquals(2, MockServiceUserRepository.users.size());

    }

    public static class MockServiceUserRepository implements IServiceUserRepository {
        public static List<ServiceUser> users = new ArrayList<>();
        @Override
        public ServiceUser getFromIp(String ip) {
            return users
                    .stream()
                    .filter(u -> u.getIp().equals(ip))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public boolean existCode(String code) {
            return false;
        }

        @Override
        public void create(ServiceUser user) {
            users.add(user);
        }
    }
}
