package com.dianping.order.client;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

/**
 * @author zhongkai.zhao
 *         13-12-20 下午10:26
 */
public class DefaultActivity extends Activity implements SurfaceHolder.Callback, HttpHelper.HttpCallback<List<DishMenu>> {

    public void onClick(View view) {
        camera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                HttpHelper.resolve(DefaultActivity.this, bytes);

                Intent intent = new Intent(getString(R.string.ACTION_DISHMENU));
                intent.putExtra("data", bytes);

                startActivity(intent);
            }
        });
    }

    @Override
    public void handle(List<DishMenu> result) {
        //TODO
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
        SurfaceView cameraView = (SurfaceView) findViewById(R.id.cameraView);
        cameraView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        cameraView.getHolder().addCallback(this);
    }

    @Override
    public void onPause() {
        if(camera != null) {
            camera.stopPreview();
            camera.release();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        try {
            camera = Camera.open();
        } catch (Throwable ex) {
            ex.printStackTrace();
            Toast.makeText(this, "打开相机失败 " + ex, Toast.LENGTH_LONG).show();
        }
        super.onResume();
    }

    private Camera camera;

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            if(camera != null) {
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) { }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) { }
}
