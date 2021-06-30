package controllers;

import com.google.inject.Inject;
import exceptions.BoardFullException;
import exceptions.ContentsNotFoundException;
import io.ebean.PagedList;
import models.BulletinBoard;
import models.BulletinThread;
import models.ServiceUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.Form;
import play.data.FormFactory;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.i18n.MessagesApi;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import services.IBulletinService;
import services.IServiceUserService;

import java.util.List;

public class BBS extends Controller {
    private Form<BulletinThread> threadForm;
    private Form<BulletinBoard> boardForm;
    private MessagesApi messagesApi;
    private final Logger logger = LoggerFactory.getLogger(getClass().getName());
    @Inject
    private IBulletinService bulletinService;

    @Inject
    private IServiceUserService userService;

    @Inject
    public BBS (FormFactory formFactory, MessagesApi messagesApi) {
        this.threadForm = formFactory.form(BulletinThread.class);
        this.boardForm = formFactory.form(BulletinBoard.class);
        this.messagesApi = messagesApi;
    }

    public Result index(int page) {
        PagedList<BulletinThread> pagedThreads = bulletinService.getPagedThreadList(page);

        return ok(views.html.bbs.index.render(pagedThreads.getList(), pagedThreads.getPageIndex() + 1, pagedThreads.getTotalPageCount()));
    }

    @AddCSRFToken
    public Result newThread(Http.Request request) {
        return ok(views.html.bbs.create.render(threadForm, request, messagesApi.preferred(request)));
    }

    @AddCSRFToken
    public Result thread(Http.Request request, int threadId) {
        BulletinThread thread = null;
        try {
            thread = bulletinService.getThread(threadId);
        } catch (ContentsNotFoundException e) {
            return notFound(views.html.error.render("404 NOT FOUND"));
        }
        List<BulletinBoard> boards = bulletinService.getBoardListInThread(threadId);

        return ok(views.html.bbs.board.render(thread, boards, boardForm, request, messagesApi.preferred(request)));
    }

    @RequireCSRFCheck
    public Result postThread(Http.Request request) {
        Form<BulletinThread> form = threadForm.bindFromRequest(request);

        if (form.hasErrors()) {
            return badRequest(views.html.bbs.create.render(form, request, messagesApi.preferred(request)));
        }
        BulletinThread thread = form.get();

        String ipAddr = request.remoteAddress();
        ServiceUser user =  userService.getFromIp(ipAddr);

        logger.info("thread is created by userCode:" + user.getCode());
        bulletinService.registerThread(thread, user);

        return redirect(routes.BBS.index(1));
    }

    @RequireCSRFCheck
    public Result postBoard(Http.Request request, int threadId) {
        Form<BulletinBoard> form = boardForm.bindFromRequest(request);
        if (form.hasErrors()) {
            return redirect(routes.BBS.thread(threadId));
        }
        BulletinBoard board = form.get();

        String ipAddr = request.remoteAddress();
        ServiceUser user =  userService.getFromIp(ipAddr);

        try {
            bulletinService.registerBoard(threadId, board, user);
        } catch (BoardFullException e) {
            logger.error("BoardFullException threadId:" + threadId + " userCode:" + user.getCode());
            // 現状フロント通知せずにスレッドページへ戻す
            return redirect(routes.BBS.thread(threadId));
        }

        return redirect(routes.BBS.thread(threadId));
    }
}
