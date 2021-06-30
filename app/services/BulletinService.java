package services;

import com.google.inject.Inject;
import controllers.routes;
import exceptions.BoardFullException;
import exceptions.ContentsNotFoundException;
import io.ebean.PagedList;
import javassist.NotFoundException;
import models.BulletinBoard;
import models.BulletinThread;
import models.ServiceUser;
import repositories.BulletinRepository;
import repositories.IBulletinRepository;

import java.util.List;

public class BulletinService implements IBulletinService {
    @Inject
    IBulletinRepository bulletinRepo;

    @Override
    public BulletinThread getThread(int threadId) throws ContentsNotFoundException {
        BulletinThread thread = bulletinRepo.getThread(threadId);
        if (thread == null) {
            throw new ContentsNotFoundException();
        }
        return generateServiceThread(thread);
    }

    private List<BulletinBoard> generateServiceBoard(List<BulletinBoard> boards) {
        for (BulletinBoard board : boards) {
            String src = board.getBody();
            board.setBody(replaceForService(src));
        }

        return boards;
    }

    private BulletinThread generateServiceThread(BulletinThread thread) {
        String src = thread.getBody();
        thread.setBody(replaceForService(src));
        return thread;
    }

    @Override
    public PagedList<BulletinThread> getPagedThreadList(int page) {
        return bulletinRepo.getPagedThreadList(page);
    }

    @Override
    public List<BulletinBoard> getBoardListInThread(int threadId) {
        List<BulletinBoard> boardList = bulletinRepo.getBoardList(threadId);
        return generateServiceBoard(boardList);
    }

    @Override
    public void registerThread(BulletinThread thread, ServiceUser user) {
        thread.setUserCode(user.getCode());
        bulletinRepo.createThread(thread);
    }

    @Override
    public void registerBoard(int threadId, BulletinBoard board, ServiceUser user) throws BoardFullException {
        if (bulletinRepo.getBoardCount(threadId) >= BulletinRepository.MAX_BOARD_NUM) {
            throw new BoardFullException();
        }
        board.setThreadId(threadId);
        board.setUserCode(user.getCode());
        bulletinRepo.registerBoard(board);
    }

    private static String replaceForService(String src) {
        return replaceLineFeed(replaceHtmlChar(src));
    }

    private static String replaceHtmlChar(String src) {
        return src
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("\'", "&#39;")
                .replace(" ", "&nbsp;");
    }

    private static String replaceLineFeed(String src) {
        return src.replace("\r\n", "<br>")
                .replace("\r", "<br>")
                .replace("\n", "<br>");
    }
}
