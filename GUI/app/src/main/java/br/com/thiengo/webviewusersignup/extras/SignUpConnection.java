package br.com.thiengo.webviewusersignup.extras;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public interface SignUpConnection {

    @Multipart
    @POST("package/ctrl/CtrlUser.php")
    public Call<ResponseBody> sendForm(
            @Part("in-method") RequestBody form,
            @Part("in-email") RequestBody email,
            @Part("in-password") RequestBody password,
            @Part("in-is-android") RequestBody isAndroid,
            @Part MultipartBody.Part image
    );
}
