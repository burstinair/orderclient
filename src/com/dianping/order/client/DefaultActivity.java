package com.dianping.order.client;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.JsonReader;
import android.view.View;
import android.widget.Toast;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class DefaultActivity extends Activity {

    private static final int REQUEST_CAMERA = 1;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(imageIntent, REQUEST_CAMERA);

                /*Resources resources = getResources();
                HttpClient httpClient = AndroidHttpClient.newInstance(resources.getString(R.string.user_agent));
                HttpHost httpHost = new HttpHost(getString(R.string.host_name), resources.getInteger(R.integer.host_port));
                HttpRequest httpRequest = new HttpPost();
                HttpParams httpParams = new BasicHttpParams();
                httpParams.setIntParameter("a", 1);
                httpParams.setIntParameter("b", 1);
                httpRequest.setParams(httpParams);
                try {
                    HttpResponse response = httpClient.execute(httpHost, httpRequest);
                    JsonReader jsonReader = new JsonReader(new InputStreamReader(response.getEntity().getContent()));
                    jsonReader.beginObject();
                    Toast.makeText(getBaseContext(), jsonReader.nextInt(), Toast.LENGTH_LONG);
                } catch (IOException e) {
                    Toast.makeText(getBaseContext(), "exception " + Arrays.toString(e.getStackTrace()), Toast.LENGTH_LONG);
                }*/
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if(requestCode == REQUEST_CAMERA) {
                Bitmap bm = (Bitmap) data.getExtras().get("data");
                System.out.println(bm);
            }
        }
    }
}
