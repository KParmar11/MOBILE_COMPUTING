package br.com.thiengo.webviewusersignup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.nguyenhoanglam.imagepicker.activity.ImagePicker;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import br.com.thiengo.webviewusersignup.domain.UserJS;
import br.com.thiengo.webviewusersignup.extras.CustomWebViewClient;
import br.com.thiengo.webviewusersignup.extras.ServiceGenerator;
import br.com.thiengo.webviewusersignup.extras.SignUpConnection;
import br.com.thiengo.webviewusersignup.extras.Util;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements Observer {

    private static final int REQUEST_IMAGE_CODE = 2546;
    private WebView webView;
    private UserJS userJS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userJS = new UserJS( this );

        webView = (WebView) findViewById(R.id.wb_content);
        webView.getSettings().setJavaScriptEnabled( true );

        webView.setHorizontalScrollBarEnabled(true);
        webView.addJavascriptInterface( this, "Android" );

        webView.loadUrl( ServiceGenerator.API_BASE_URL + "view/index.php" );
        webView.setWebViewClient( new CustomWebViewClient() );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_CODE ){
            ArrayList<Image> images = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);

            if( images != null ){
                final Image image = images.get(0);
                userJS.getImageJS().setUri( image.getPath() );
                userJS.getImageJS().generateBase64();
            }
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String src = userJS.getImageJS().getBase64();
                loadWebViewDataSupport( src );
            }
        });
    }

    private void loadWebViewDataSupport( String srcBase64 ){

        if( Util.isPreKitKat() ){
            String postData = null;
            try {
                postData = "image="+ URLEncoder.encode(srcBase64, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            webView.postUrl( ServiceGenerator.API_BASE_URL + "view/index.php", postData.getBytes());
        }
        else{
            webView.loadUrl("javascript:loadImageSrc('" + srcBase64 + "')");
        }
    }


    @JavascriptInterface
    public void callGallery(){

        ImagePicker.create( this )
                .folderMode(true) // folder mode (false by default)
                .folderTitle("Galeria") // folder selection title
                .imageTitle("Clique para selecionar") // image selection title
                .single() // single mode
                .limit(1) // max images can be selected (99 by default)
                .showCamera(true) // show camera or not (true by default)
                .imageDirectory("Camera") // directory name for captured image  ("Camera" folder by default)
                .start( REQUEST_IMAGE_CODE );
    }

    @JavascriptInterface
    public void sendForm( String method, String email, String password ){
        userJS.setMethod( method );
        userJS.setEmail( email );
        userJS.setPassword( password );

        SignUpConnection signUpConnection = ServiceGenerator.createService( SignUpConnection.class );

        Call<ResponseBody> call = signUpConnection.sendForm(
                userJS.getMethodRequestBody(),
                userJS.getEmailRequestBody(),
                userJS.getPasswordRequestBody(),
                RequestBody.create( MediaType.parse("multipart/form-data"), "1"),
                userJS.getImageJSRequestBody());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String url = response.body().string();
                    webView.loadUrl("javascript:loadSignUpDonePage('" + url + "')");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }
}
