package controller;

import model.Session;
import router.RequestMapping;
import service.SessionService;
import service.UserService;
import webserver.model.Request;
import webserver.model.Request.Method;
import webserver.model.Response;

import java.util.HashMap;
import java.util.Map;

import static http.HttpUtil.*;
import static http.HttpParser.*;
import static model.User.PASSWORD;
import static model.User.USERID;

public class ServiceController {

    @RequestMapping(value="/user/create", method=Method.POST)
    public Response userCreate(Request request) {
        Map<String, String> bodyParameterMap = parseBodyParameter(request.getBody());

        UserService.userSignUp(bodyParameterMap);

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(HEADER_REDIRECT_LOCATION, INDEX_URL);

        return new Response(STATUS.SEE_OTHER, headerMap, null);
    }

    @RequestMapping(value="/user/login", method=Method.POST)
    public Response userLogin(Request request) {
        Map<String, String> bodyParameterMap = parseBodyParameter(request.getBody());
        String userId = bodyParameterMap.get(USERID);
        String password = bodyParameterMap.get(PASSWORD);

        // ID/PW 검증
        if(!UserService.validateUser(userId, password)) {
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put(HEADER_REDIRECT_LOCATION, "/user/login_failed.html");
            return new Response(STATUS.SEE_OTHER, headerMap, null);
        }

        // Session ID 추가
        Session session = SessionService.getSessionByUserId(userId);
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(HEADER_REDIRECT_LOCATION, INDEX_URL);
        headerMap.put(HEADER_SET_COOKIE, HEADER_SESSION_ID + session.getSessionId() + HEADER_COOKIE_PATH);

        return new Response(STATUS.SEE_OTHER, headerMap, null);
    }
}
