import com.google.common.collect.ImmutableMap;
import io.ebean.PagedList;
import models.BulletinBoard;
import models.BulletinThread;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.db.DBApi;
import play.db.evolutions.Evolutions;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.WithApplication;
import repositories.BulletinRepository;

import java.util.List;
import java.util.Map;
import static org.junit.Assert.*;

public class BulletinRepositoryTest extends WithApplication {
    private BulletinRepository repo;
    private DBApi db;

    @Override
    protected Application provideApplication() {
        Map<String, Object> configMap = ImmutableMap.of(
                "db.default.driver" ,"org.h2.Driver",
                "db.default.url" , "jdbc:h2:mem:play;MODE=MYSQL"
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

        repo = app.injector().instanceOf(BulletinRepository.class);

        for(int i = 0; i < 55; i ++) {
            BulletinThread thread = new BulletinThread();
            thread.setTitle("title_" + (i + 1));
            thread.setBody("body_" + (i + 1));
            thread.setUserCode("userCode_" + (i + 1));
            thread.setName("userName_" + (i + 1));
            thread.save();
            for (int j = 0; j < 10; j++) {
                BulletinBoard board = new BulletinBoard();
                board.setThreadId(i + 1);
                board.setUserCode("userCode_" + (j + 1));
                board.setName("userName_" + (j + 1));
                board.setBody("boardbody_" + (j + (i + 1) * 100));
                board.save();
            }
        }
    }

    @Test
    public void testGetPagedThreadListPage1() {
        PagedList<BulletinThread> pagedThreadList = repo.getPagedThreadList(1);
        assertNotNull(pagedThreadList);

        assertEquals(10, pagedThreadList.getList().size());
        List<BulletinThread> threadList = pagedThreadList.getList();
        for (int i = 0; i < 10; i++) {
            BulletinThread thread = threadList.get(i);
            assertEquals(55 - i, thread.getId());
            assertEquals("title_" + (55 - i), thread.getTitle());
            assertEquals("body_" + (55 - i), thread.getBody());
        }

        assertEquals(0, pagedThreadList.getPageIndex());
        assertEquals(6, pagedThreadList.getTotalPageCount());
        assertEquals(55, pagedThreadList.getTotalCount());
        assertEquals(10, pagedThreadList.getPageSize());

    }

    @Test
    public void testGetPagedThreadListPage6() {
        PagedList<BulletinThread> pagedThreadList = repo.getPagedThreadList(6);
        assertNotNull(pagedThreadList);

        assertEquals(5, pagedThreadList.getList().size());
        List<BulletinThread> threadList = pagedThreadList.getList();
        for (int i = 0; i < 5; i++) {
            BulletinThread thread = threadList.get(i);
            assertEquals(5 - i, thread.getId());
            assertEquals("title_" + (5 - i), thread.getTitle());
            assertEquals("body_" + (5 - i), thread.getBody());
        }

        assertEquals(5, pagedThreadList.getPageIndex());
        assertEquals(6, pagedThreadList.getTotalPageCount());
        assertEquals(55, pagedThreadList.getTotalCount());
        assertEquals(10, pagedThreadList.getPageSize());

    }

    @Test
    public void testGetPagedThreadListPage10() {
        PagedList<BulletinThread> pagedThreadList = repo.getPagedThreadList(10);
        assertNotNull(pagedThreadList);

        assertEquals(0, pagedThreadList.getList().size());
        List<BulletinThread> threadList = pagedThreadList.getList();
        assertEquals(9, pagedThreadList.getPageIndex());
        assertEquals(6, pagedThreadList.getTotalPageCount());
        assertEquals(55, pagedThreadList.getTotalCount());
        assertEquals(10, pagedThreadList.getPageSize());

    }

    @Test
    public void testGetBoardList() {
        List<BulletinBoard> boardList = repo.getBoardList(5);
        assertNotNull(boardList);
        assertEquals(10, boardList.size());

        for (int i = 0; i < 10; i ++) {
            BulletinBoard board = boardList.get(i);
            assertEquals(41 + i, board.getId());
            assertEquals(5, board.getThreadId());
            assertEquals("userCode_" + (i + 1), board.getUserCode());
            assertEquals("boardbody_" + (i + 500), board.getBody());
        }
    }

    @Test
    public void testGetBoardListOutOfThread() {
        List<BulletinBoard> boardList = repo.getBoardList(100);
        assertNotNull(boardList);
        assertEquals(0, boardList.size());
    }

    @Test
    public void testGetThread() {
        BulletinThread thread = repo.getThread(30);
        assertNotNull(thread);
        assertEquals(30, thread.getId());
        assertEquals("title_30", thread.getTitle());
    }

    @Test
    public void testGetThreadOutOfRange() {
        BulletinThread thread = repo.getThread(1000);
        assertNull(thread);
    }

    @Test
    public void testCreateThread() {
        BulletinThread thread = new BulletinThread();
        thread.setName("testName");
        thread.setBody("testBody");
        thread.setTitle("testTitle");
        thread.setUserCode("userCode");
        repo.createThread(thread);

        assertEquals(56, BulletinThread.finder.all().size());
        BulletinThread result = BulletinThread.finder.byId(56);

        assertEquals(thread.getBody(), result.getBody());
        assertEquals(thread.getName(), result.getName());

    }

    @Test
    public void testCreateBoard() {
        BulletinBoard board = new BulletinBoard();
        board.setBody("testBody");
        board.setName("testName");
        board.setUserCode("testUserCode");
        board.setThreadId(10);
        repo.registerBoard(board);
        assertEquals(551, BulletinBoard.finder.all().size());
        BulletinBoard result = BulletinBoard.finder.byId(551);

        assertEquals(board.getThreadId(), result.getThreadId());
        assertEquals(board.getBody(), result.getBody());
    }

    @Test
    public void testGetBoardCount() {
        int count = repo.getBoardCount(20);
        assertEquals(10, count);
    }


}
