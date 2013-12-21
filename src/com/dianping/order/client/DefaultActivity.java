package com.dianping.order.client;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONTokener;
import android.widget.ImageView;

import java.util.List;

public class DefaultActivity extends Activity implements HttpHelper.HttpCallback<List<DishMenu>> {

    private static final int REQUEST_CODE_CAMERA = 10;
    private static final int REQUEST_CODE_RESOLVE = 20;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void onClick(View view) {
        Intent cameraIntent = new Intent(getString(R.string.ACTION_CAMERA));
        startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if(requestCode == REQUEST_CODE_CAMERA) {
                byte[] result = (byte[]) data.getExtras().get("data");
                HttpHelper.resolve(this, REQUEST_CODE_RESOLVE, result);

                Bitmap bitmap = BitmapFactory.decodeByteArray(result, 0, result.length);
                ((ImageView)findViewById(R.id.imageView)).setImageBitmap(Blur.BoxBlurFilter(bitmap));
            }
        }
    }

    @Override
    public void handle(List<DishMenu> result, int requestCode) {
        Toast.makeText(this, "suc", Toast.LENGTH_SHORT);
    }
}
