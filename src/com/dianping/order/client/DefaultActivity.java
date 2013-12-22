package com.dianping.order.client;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.Toast;
import com.dianping.order.client.framework.Callback;
import com.dianping.order.client.framework.Cancelable;
import com.dianping.order.client.framework.ResultStatus;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author zhongkai.zhao
 *         13-12-20 下午10:26
 */
public class DefaultActivity extends Activity implements SurfaceHolder.Callback, Callback<ResolveResult> {

    private AtomicBoolean isInProgress;
    private ProgressDialog progressDialog;
    private Cancelable progress;

    public void onClick(View view) {
        if(isInProgress.compareAndSet(false, true)) {
            camera.stopPreview();
            camera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] bytes, Camera camera) {

                    progressDialog = new ProgressDialog(DefaultActivity.this);
                    progressDialog.setTitle(getString(R.string.waiting));
                    progressDialog.setMessage(getString(R.string.waiting));
                    progressDialog.setCancelable(true);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progress.cancel();
                        }
                    });
                    progressDialog.show();

                    progress = APIUse.resolve(DefaultActivity.this, bytes);
                }
            });
        }
    }

    @Override
    public void handle(final ResolveResult result, ResultStatus resultStatus) {
        progressDialog.cancel();
        progressDialog = null;
        isInProgress.set(false);
        progress = null;
        if(resultStatus == ResultStatus.SUCCESS) {
            Intent intent = new Intent(getString(R.string.ACTION_DISHMENU));
            intent.putExtra("result", result);
            startActivity(intent);
        } else {
            if(resultStatus != ResultStatus.CANCELED) {
                Toast.makeText(this, getString(R.string.networkError), Toast.LENGTH_LONG).show();
            }
            camera.startPreview();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
        SurfaceView cameraView = (SurfaceView) findViewById(R.id.cameraView);
        cameraView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        cameraView.getHolder().addCallback(this);

        for(int i = 0, l = Camera.getNumberOfCameras(); i < l; ++i) {
            Camera.CameraInfo cameraInfo = getCameraInfo(i);
            if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                break;
            }
        }

        isInProgress = new AtomicBoolean(false);
    }

    @Override
    public void onPause() {
        if(camera != null) {
            camera.stopPreview();
            camera.release();
        }
        super.onPause();
    }

    private Camera.CameraInfo getCameraInfo(int cameraId) {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, cameraInfo);
        return cameraInfo;
    }

    private int getOrientation(int rotation, int cameraOrientation) {
        int orientation = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                orientation = 0;
                break;
            case Surface.ROTATION_90:
                orientation = 90;
                break;
            case Surface.ROTATION_180:
                orientation =  180;
                break;
            case Surface.ROTATION_270:
                orientation = 270;
                break;
        }
        orientation = (orientation + cameraOrientation) % 360;
        return orientation;
    }

    private Camera.Size getSize(List<Camera.Size> supportedPreviewSizes, List<Camera.Size> supportedPictureSizes) {
        Camera.Size minSize = null;
        if(supportedPreviewSizes != null) {
            for(Camera.Size size : supportedPreviewSizes) {
                int minDim = size.width < size.height ? size.width : size.height;
                if(minDim >= 768 && supportedPictureSizes.contains(size)) {
                    if(minSize == null || size.width < minSize.width) {
                        minSize = size;
                    }
                }
            }
        }
        return minSize;
    }

    @Override
    public void onResume() {
        try {
            camera = Camera.open(cameraId);
            Camera.CameraInfo cameraInfo = getCameraInfo(cameraId);
            Display display = getWindowManager().getDefaultDisplay();

            int orientation = getOrientation(display.getRotation(), cameraInfo.orientation);
            camera.setDisplayOrientation(orientation);

            Camera.Parameters parameters = camera.getParameters();
            parameters.setRotation(orientation);
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            Camera.Size size = getSize(parameters.getSupportedPreviewSizes(), parameters.getSupportedPictureSizes());
            if(size != null) {
                parameters.setPreviewSize(size.width, size.height);
                parameters.setPictureSize(size.width, size.height);
            }
            camera.setParameters(parameters);
        } catch (Throwable ex) {
            Log.w("DefaultActivity.onResume", ex);
            Toast.makeText(this, getString(R.string.cameraError) + ex, Toast.LENGTH_LONG).show();
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
        } catch (IOException ex) {
            Log.w("DefaultActivity.surfaceCreated", ex);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) { }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) { }
}
