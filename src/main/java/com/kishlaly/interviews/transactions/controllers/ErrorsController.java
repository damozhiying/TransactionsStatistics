package com.kishlaly.interviews.transactions.controllers;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Vladimir Kishlaly
 * @since 06.07.2017
 */
@RestController
public class ErrorsController implements ErrorController {

    private static final String PATH = "/error";

    @RequestMapping(value = PATH)
    public String error() {
        return "<h1>Nope</h1>" +
                "<br>Service does not accept this URL<br>" +
                "Please, use <a href='/statistics'>this link</a> to get fresh statistics for last minute.";
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }

}
