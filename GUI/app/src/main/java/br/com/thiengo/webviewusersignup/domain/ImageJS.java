package br.com.thiengo.webviewusersignup.domain;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

import br.com.thiengo.webviewusersignup.extras.Util;

/**
 * Created by viniciusthiengo on 12/11/16.
 */

public class ImageJS extends Observable {
    private Uri uri;
    private String base64;


    ImageJS( Observer observer ){
        addObserver( observer );
    }

    public void setUri( String path ){
        uri = Uri.parse(path);
    }

    public File getAsFile(){
        return new File( uri.toString() );
    }

    public String getBase64(){
        return base64;
    }

    public void generateBase64(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                if( !Util.isPreKitKat() ){
                    Bitmap bitmap = generateBitmap();

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    String imgageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    base64 = "data:image/png;base64," + imgageBase64;
                }

                setChanged();
                notifyObservers();
            }
        }).start();
    }

    private Bitmap generateBitmap(){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile( uri.toString(), options );
        return bitmap;
    }
}
