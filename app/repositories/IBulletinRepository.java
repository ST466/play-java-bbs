package repositories;

import com.google.inject.ImplementedBy;
import io.ebean.PagedList;
import models.BulletinBoard;
import models.BulletinThread;

import java.util.List;

@ImplementedBy(BulletinRepository.class)
public interface IBulletinRepository {
    public PagedList<BulletinThread> getPagedThreadList(int page);
    public List<BulletinBoard> getBoardList(int threadId);
    public BulletinThread getThread(int id);
    public void createThread(BulletinThread thread);
    public void registerBoard(BulletinBoard board);
    public int getBoardCount(int threadId);
}
