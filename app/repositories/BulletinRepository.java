package repositories;

import io.ebean.PagedList;
import io.ebean.annotation.Transactional;
import models.BulletinBoard;
import models.BulletinThread;

import java.util.List;

public class BulletinRepository implements IBulletinRepository {
    private static final int THREAD_COUNT_PER_PAGE = 10;
    public static final int MAX_BOARD_NUM = 1000;
    @Override
    public PagedList<BulletinThread> getPagedThreadList(int page) {
        PagedList<BulletinThread> threadList = BulletinThread.finder
                .query()
                .order().desc("updated_at")
                .order().desc("id")
                .setFirstRow((page - 1) * THREAD_COUNT_PER_PAGE)
                .setMaxRows(THREAD_COUNT_PER_PAGE)
                .findPagedList();
        return threadList;
    }

    @Override
    public List<BulletinBoard> getBoardList(int threadId) {
        List<BulletinBoard> boardList = BulletinBoard.finder
                .query()
                .where().eq("thread_id", threadId)
                .order().asc("created_at")
                .order().asc("id")
                .setMaxRows(MAX_BOARD_NUM)
                .findList();
        return boardList;
    }

    @Override
    public BulletinThread getThread(int id) {
        return BulletinThread.finder.byId(id);
    }

    @Override
    @Transactional
    public void createThread(BulletinThread thread) {
        thread.save();
    }

    @Override
    @Transactional
    public void registerBoard(BulletinBoard board) {
        BulletinThread thread = getThread(board.getThreadId());
        board.save();
        thread.update();
    }

    @Override
    public int getBoardCount(int threadId) {
        return BulletinBoard.finder.query().where().eq("thread_id", threadId).findCount();
    }

}
