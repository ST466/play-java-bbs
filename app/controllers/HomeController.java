package controllers;

import play.mvc.*;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    // 現状掲示板のみのためbbsへリダイレクト
    public Result index() {
        return redirect(routes.BBS.index(1));
    }

}
