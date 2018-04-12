package com.shopping.assistant.view;

public interface ServerAuthentication {

    String userSignIn(String user, String pass, String authType, String requestUrl) throws Exception;
}
