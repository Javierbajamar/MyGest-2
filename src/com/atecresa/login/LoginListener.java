package com.atecresa.login;

interface LoginListener {
    void notifyLoginSucess(String res);
    void notifyLoginError();
}
