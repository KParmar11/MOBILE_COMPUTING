package br.com.thiengo.webviewusersignup.domain;


import java.io.File;
import java.util.Observer;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UserJS {
    private String method;
    private String email;
    private String password;
    private ImageJS imageJS;


    public UserJS(Observer observer){
        imageJS = new ImageJS( observer );
    }

    public RequestBody getMethodRequestBody() {
        return RequestBody.create( MediaType.parse("multipart/form-data"), method);
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public RequestBody getEmailRequestBody() {
        return RequestBody.create( MediaType.parse("multipart/form-data"), email);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public RequestBody getPasswordRequestBody() {
        return RequestBody.create( MediaType.parse("multipart/form-data"), password);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ImageJS getImageJS() {
        return imageJS;
    }


    public MultipartBody.Part getImageJSRequestBody() {
        File file = imageJS.getAsFile();
        RequestBody requestFile = RequestBody.create( MediaType.parse("multipart/form-data"), file );
        return MultipartBody.Part.createFormData( "in-img", file.getName(), requestFile );
    }
}
