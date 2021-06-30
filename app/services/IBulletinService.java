package services;

import com.google.inject.ImplementedBy;
import exceptions.BoardFullException;
import exceptions.ContentsNotFoundException;
import io.ebean.PagedList;
import models.BulletinBoard;
import models.BulletinThread;
import models.ServiceUser;

import java.util.List;

@ImplementedBy(BulletinService.class)
public interface IBulletinService {
    public BulletinThread getThread(int threadId) throws ContentsNotFoundException;
    public PagedList<BulletinThread> getPagedThreadList(int page);
    public List<BulletinBoard> getBoardListInThread(int threadId);
    public void registerThread(BulletinThread thread, ServiceUser user);
    public void registerBoard(int threadId, BulletinBoard board, ServiceUser user) throws BoardFullException;
}
