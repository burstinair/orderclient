package com.dianping.order.client;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.*;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.SynchronousQueue;

/**
 * @author zhongkai.zhao
 *         13-12-20 下午10:26
 */
public class DefaultActivity extends Activity implements SurfaceHolder.Callback, HttpHelper.HttpCallback<List<DishMenu>> {

    private static DefaultActivity INSTANCE;
    public static DefaultActivity getInstance() {
        return INSTANCE;
    }

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

    private static final int TIMEOUT = 20000;

    private List<DishMenu> resolveResult;
    public List<DishMenu> getResolveResult() {
        int wait = 0, interval = 100;
        while(resolveResult == null && wait < TIMEOUT) {
            try {
                wait += interval;
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        List<DishMenu> result =  resolveResult;
        resolveResult = null;
        return result;
    }

    @Override
    public void handle(List<DishMenu> result) {
        resolveResult = result;
        HttpHelper.submit(new HttpHelper.HttpCallback<String>() {
            @Override
            public void handle(String result) {
                Toast.makeText(DefaultActivity.this, result, Toast.LENGTH_LONG).show();
            }
        }, result);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        INSTANCE = this;

        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
        SurfaceView cameraView = (SurfaceView) findViewById(R.id.cameraView);
        cameraView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        cameraView.getHolder().addCallback(this);

        for(int i = 0, l = Camera.getNumberOfCameras(); i < l; ++i) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(i, cameraInfo);
            if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                break;
            }
        }
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
            camera = Camera.open(cameraId);
            Display display = getWindowManager().getDefaultDisplay();

            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraId, cameraInfo);
            int orientation = 0;
            switch (display.getRotation()) {
                case Surface.ROTATION_0:
                    orientation = 0;
                    break;
                case Surface.ROTATION_90:
                    orientation = 90;
                    break;
                case Surface.ROTATION_180:
                    orientation = 180;
                    break;
                case Surface.ROTATION_270:
                    orientation = 270;
                    break;
            }
            orientation = (orientation + cameraInfo.orientation) % 360;
            //orientation = (360 - orientation) % 360;
            camera.setDisplayOrientation(orientation);

            Camera.Parameters parameters = camera.getParameters();
            parameters.setRotation(orientation);
            Camera.Size minSize = null;
            List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
            List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
            if(previewSizes != null) {
                for(Camera.Size size : previewSizes) {
                    int minDim = size.width < size.height ? size.width : size.height;
                    if(minDim >= 768 && pictureSizes.contains(size)) {
                        if(minSize == null || size.width < minSize.width) {
                            minSize = size;
                        }
                    }
                }
            }
            if(minSize != null) {
                parameters.setPreviewSize(minSize.width, minSize.height);
                parameters.setPictureSize(minSize.width, minSize.height);
            }
            camera.setParameters(parameters);
        } catch (Throwable ex) {
            ex.printStackTrace();
            Toast.makeText(this, "打开相机失败 " + ex, Toast.LENGTH_LONG).show();
        }
        super.onResume();
    }

    private int cameraId;
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
