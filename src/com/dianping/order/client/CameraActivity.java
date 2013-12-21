package com.dianping.order.client;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.*;
import android.widget.Toast;

import java.io.IOException;

/**
 * @author zhongkai.zhao
 *         13-12-20 下午10:26
 */
public class CameraActivity extends Activity implements SurfaceHolder.Callback {

    private SurfaceView cameraView;

    public void onSubmit(View view) {
        camera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                setResult(RESULT_OK, getIntent().putExtra("data", bytes));
                finish();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.camera);
        cameraView = (SurfaceView) findViewById(R.id.cameraView);
        cameraView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        cameraView.getHolder().addCallback(this);
    }

    @Override
    public void onPause() {
        camera.stopPreview();
        camera.release();
        super.onPause();
    }

    @Override
    public void onResume() {
        try {
            camera = Camera.open();
        } catch (Throwable ex) {
            ex.printStackTrace();
            Toast.makeText(this, "打开相机失败 " + ex.getStackTrace(), Toast.LENGTH_LONG).show();
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
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        //Camera.Parameters parameters = camera.getParameters();
        //parameters.setPreviewSize(width, height);
        //parameters.setPreviewFormat(format);
        //camera.setParameters(parameters);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    }
}
