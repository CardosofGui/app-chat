package com.example.appchat.Data;

public class Usuario {

    String user;
    String email;
    String senha;
    String uid;

    public String getUser() {
        return user;
    }

    public Usuario(String user, String email, String senha) {
        this.user = user;
        this.email = email;
        this.senha = senha;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
