import com.google.common.collect.ImmutableMap;
import exceptions.BoardFullException;
import exceptions.ContentsNotFoundException;
import io.ebean.PagedList;
import models.BulletinBoard;
import models.BulletinThread;
import models.ServiceUser;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.WithApplication;
import repositories.IBulletinRepository;
import services.BulletinService;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import static org.junit.Assert.*;
import static play.inject.Bindings.bind;

public class BulletinServiceTest extends WithApplication {
    private BulletinService service;

    @Override
    protected Application provideApplication() {
        Map<String, Object> configMap = ImmutableMap.of(
                "db.default.driver" ,"org.h2.Driver",
                "db.default.url" , "jdbc:h2:mem:play"
        );
        return new GuiceApplicationBuilder()
                .configure(configMap)
                .overrides(bind(IBulletinRepository.class).to(MockBulletinRepository.class))
                .build();


    }

    @Before
    public void init() {
        service = app.injector().instanceOf(BulletinService.class);
        MockBulletinRepository.boardList = new ArrayList<>();
        MockBulletinRepository.threadList = new ArrayList<>();
    }

    @Test
    public void testGetThread() {
        BulletinThread testThread = new BulletinThread();
        testThread.setUserCode("testuser");
        testThread.setTitle("testtitle");
        testThread.setName("testname");
        testThread.setBody("testBody\n & <");
        MockBulletinRepository.threadList.add(testThread);

        BulletinThread thread = null;
        try {
            thread = service.getThread(1);
        } catch (ContentsNotFoundException e) {
            fail();
        }

        assertNotNull(thread);
        assertEquals("testBody<br>&nbsp;&amp;&nbsp;&lt;", thread.getBody());
    }

    @Test
    public void testGetPagedThread() {
        PagedList pagedThreadList = service.getPagedThreadList(1);
        assertNotNull(pagedThreadList);
        assertEquals(0, pagedThreadList.getList().size());

        for(int i = 0; i < 20; i++) {
            BulletinThread testThread = new BulletinThread();
            testThread.setUserCode("testuser" + i);
            testThread.setTitle("testtitle" + i);
            testThread.setName("testname" + i);
            testThread.setBody("testBody" + i);
            MockBulletinRepository.threadList.add(testThread);
        }

        pagedThreadList = service.getPagedThreadList(1);
        assertNotNull(pagedThreadList);
        assertEquals(20, pagedThreadList.getList().size());

    }

    @Test
    public void testGetBoardInThread() {
        for(int i = 0; i < 10; i++) {
            BulletinBoard testBoard = new BulletinBoard();
            testBoard.setThreadId(1);
            testBoard.setBody("testBody\n <a> & \"" + i);
            testBoard.setName("testName" + i);
            testBoard.setUserCode("testCode" + i);
            MockBulletinRepository.boardList.add(testBoard);
        }

        List<BulletinBoard> boards = service.getBoardListInThread(1);
        assertNotNull(boards);
        assertEquals(10, boards.size());
        for(int i = 0; i < 10; i++) {
            assertEquals("testBody<br>&nbsp;&lt;a&gt;&nbsp;&amp;&nbsp;&quot;" + i, boards.get(i).getBody());
        }
    }

    @Test
    public void testRegisterThread() {
        ServiceUser user = new ServiceUser();
        user.setIp("127.0.0.1");
        user.setCode("testcode");

        BulletinThread thread = new BulletinThread();
        thread.setName("testName");
        thread.setTitle("testTitle");
        thread.setBody("testBody<a>\n");
        thread.setUserCode("testCode_dummy");

        service.registerThread(thread, user);

        assertEquals(1, MockBulletinRepository.threadList.size());
        BulletinThread result = MockBulletinRepository.threadList.get(0);
        assertEquals(thread.getName(), result.getName());
        assertEquals(thread.getBody(), result.getBody());
        assertEquals("testcode", result.getUserCode());
    }

    @Test
    public void testRegisterBoard() {
        ServiceUser user = new ServiceUser();
        user.setIp("127.0.0.1");
        user.setCode("testcode");

        BulletinBoard board = new BulletinBoard();
        board.setName("testName");
        board.setBody("testBody");
        board.setUserCode("test_Code_dummy");
        board.setThreadId(1);

        try {
            service.registerBoard(1, board, user);
        } catch (BoardFullException e) {
            fail("unexpected BoardFullException");
        }

        assertEquals(1, MockBulletinRepository.boardList.size());
        BulletinBoard result = MockBulletinRepository.boardList.get(0);
        assertEquals(board.getName(), result.getName());
        assertEquals(board.getBody(), result.getBody());
        assertEquals(user.getCode(), result.getUserCode());
    }

    @Test
    public void testRegisterBoardException() {
        for(int i = 0; i < 1000; i++) {
            BulletinBoard testBoard = new BulletinBoard();
            testBoard.setThreadId(1);
            testBoard.setBody("testBody\n <a> & \"" + i);
            testBoard.setName("testName" + i);
            testBoard.setUserCode("testCode" + i);
            MockBulletinRepository.boardList.add(testBoard);
        }

        ServiceUser user = new ServiceUser();
        user.setIp("127.0.0.1");
        user.setCode("testcode");

        BulletinBoard board = new BulletinBoard();
        board.setName("testName");
        board.setBody("testBody");
        board.setUserCode("test_Code_dummy");
        board.setThreadId(1);

        try {
            service.registerBoard(1, board, user);
            fail();
        } catch (BoardFullException e) {
            // 意図的な例外
        }

        assertEquals(1000, MockBulletinRepository.boardList.size());
    }

    public static class MockBulletinRepository implements IBulletinRepository {
        public static List<BulletinThread> threadList = new ArrayList<>();
        public static List<BulletinBoard> boardList = new ArrayList<>();

        @Override
        public PagedList<BulletinThread> getPagedThreadList(int page) {
            PagedList<BulletinThread> pagedList = new PagedList<BulletinThread>() {
                @Override
                public void loadCount() {

                }

                @Nonnull
                @Override
                public Future<Integer> getFutureCount() {
                    return null;
                }

                @Nonnull
                @Override
                public List<BulletinThread> getList() {
                    return threadList;
                }

                @Override
                public int getTotalCount() {
                    return threadList.size();
                }

                @Override
                public int getTotalPageCount() {
                    return 20;
                }

                @Override
                public int getPageSize() {
                    return 20;
                }

                @Override
                public int getPageIndex() {
                    return 1;
                }

                @Override
                public boolean hasNext() {
                    return true;
                }

                @Override
                public boolean hasPrev() {
                    return true;
                }

                @Override
                public String getDisplayXtoYofZ(String to, String of) {
                    return null;
                }
            };
            return pagedList;
        }

        @Override
        public List<BulletinBoard> getBoardList(int threadId) {
            return boardList;
        }

        @Override
        public BulletinThread getThread(int id) {
            return threadList.get(id - 1);
        }

        @Override
        public void createThread(BulletinThread thread) {
            threadList.add(thread);
        }

        @Override
        public void registerBoard(BulletinBoard board) {
            boardList.add(board);
        }

        @Override
        public int getBoardCount(int threadId) {
            return boardList.size();
        }
    }
}
