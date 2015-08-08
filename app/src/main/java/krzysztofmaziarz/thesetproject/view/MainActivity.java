package krzysztofmaziarz.thesetproject.view;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import krzysztofmaziarz.thesetproject.R;
import krzysztofmaziarz.thesetproject.core.ImageProcessor;
import krzysztofmaziarz.thesetproject.view.utils.CameraUtils;

@SuppressWarnings("deprecation")
public class MainActivity extends Activity {

    private Camera camera;
    private CameraPreview cameraPreview;
    private boolean inCameraPreview;

    private static final String OPEN_CV_NAME = "opencv_java";

    static {
        System.loadLibrary(OPEN_CV_NAME);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        camera = CameraUtils.getCameraInstance();

        getLayout().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {

            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {

                getLayout().removeOnLayoutChangeListener(this);
                double ratio = (double) getLayout().getWidth() / getLayout().getHeight();

                Camera.Size optimalSize = CameraUtils.getOptimalSize(camera, 700, ratio);
                CameraUtils.setCameraSizes(camera, optimalSize);
            }
        });

        cameraPreview = new CameraPreview(this, camera);
        setView(cameraPreview);

        getLayout().setOnClickListener(view -> {
            if (inCameraPreview) {
                camera.takePicture(null, null, getPictureCallback());
            } else {
                setView(cameraPreview);
            }
        });
    }

    private FrameLayout getLayout() {
        return (FrameLayout) findViewById(R.id.image_preview);
    }

    private void setView(View view) {
        getLayout().removeAllViews();
        getLayout().addView(view);
        inCameraPreview = view.equals(cameraPreview);
    }

    private void showImage(Bitmap bitmap) {
        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setImageBitmap(bitmap);

        setView(imageView);
    }

    private Camera.PictureCallback getPictureCallback() {
        return (data, camera) -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            showImage(ImageProcessor.markSets(bitmap));
        };
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (camera != null) {
            camera.release();
            camera = null;
        }
    }
}
