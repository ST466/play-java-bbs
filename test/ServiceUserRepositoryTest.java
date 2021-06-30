import com.google.common.collect.ImmutableMap;
import models.ServiceUser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.db.DBApi;
import play.db.evolutions.Evolutions;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.WithApplication;
import repositories.IBulletinRepository;
import repositories.IServiceUserRepository;
import repositories.ServiceUserRepository;

import java.util.Map;

import static org.junit.Assert.*;

public class ServiceUserRepositoryTest extends WithApplication {
    private IServiceUserRepository repo;
    private DBApi db;

    @Override
    protected Application provideApplication() {
        Map<String, Object> configMap = ImmutableMap.of(
                "db.default.driver" ,"org.h2.Driver",
                "db.default.url" , "jdbc:h2:mem:play"
        );
        return new GuiceApplicationBuilder()
                .configure(configMap)
                .build();
    }

    @Before
    public void init() {
        db = app.injector().instanceOf(DBApi.class);
        Evolutions.cleanupEvolutions(db.getDatabase("default"));
        Evolutions.applyEvolutions(db.getDatabase("default"));

        repo = app.injector().instanceOf(ServiceUserRepository.class);

        for(int i = 0; i < 10; i++) {
            ServiceUser user = new ServiceUser();
            user.setIp("127.0.0." + i);
            user.setCode("testCode" + i);
            user.save();
        }
    }

    @Test
    public void testGetFromIp() {
        ServiceUser user = repo.getFromIp("127.0.0.3");
        assertNotNull(user);
        assertEquals("127.0.0.3", user.getIp());

        user = repo.getFromIp("aaaa");
        assertNull(user);
    }

    @Test
    public void testExistCode() {
        assertTrue(repo.existCode("testCode8"));
        assertTrue(repo.existCode("testCode1"));
        assertTrue(repo.existCode("testCode3"));
        assertFalse(repo.existCode("testCode100"));
    }

    @Test
    public void testCreate() {
        ServiceUser user = new ServiceUser();
        user.setIp("127.0.0.100");
        user.setCode("testCode100");
        repo.create(user);

        ServiceUser result = ServiceUser.finder.query().where().eq("ip", "127.0.0.100").findOne();

        assertNotNull(result);
        assertEquals(11, ServiceUser.finder.all().size());

        assertEquals("127.0.0.100", result.getIp());
        assertEquals("testCode100", result.getCode());
    }

}
