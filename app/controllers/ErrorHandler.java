package controllers;

import play.http.HttpErrorHandler;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Singleton
public class ErrorHandler implements HttpErrorHandler {
    @Override
    public CompletionStage<Result> onClientError(Http.RequestHeader request, int statusCode, String message) {
        String errorMessage = "";
        if (statusCode == 404) {
            errorMessage = "404 NOT FOUND";
        }
        return CompletableFuture.completedFuture(
                Results.status(statusCode, views.html.error.render(errorMessage))
        );
    }

    @Override
    public CompletionStage<Result> onServerError(Http.RequestHeader request, Throwable exception) {
        return CompletableFuture.completedFuture(
                Results.internalServerError(views.html.error.render(""))
        );
    }
}
