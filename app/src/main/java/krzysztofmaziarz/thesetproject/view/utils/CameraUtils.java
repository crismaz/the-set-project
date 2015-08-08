package krzysztofmaziarz.thesetproject.view.utils;

import android.hardware.Camera;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

@SuppressWarnings("deprecation")
public class CameraUtils {

    public static @Nullable Camera getCameraInstance() {
        Camera camera = null;

        try {
            camera = Camera.open();
        }
        catch (Exception ignored) {}

        return camera;
    }

    public static Camera.Size getOptimalSize(Camera camera, int height, double targetRatio) {
        final double ASPECT_TOLERANCE = 0.2;

        List<Camera.Size> sizes = camera.getParameters().getSupportedPreviewSizes();
        sizes.retainAll(camera.getParameters().getSupportedPictureSizes());

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - height) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - height);
            }
        }

        if (optimalSize == null) {
            Log.e(CameraUtils.class.getSimpleName(), "Couldn't find any preview size with good ratio");

            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - height) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - height);
                }
            }
        }

        if (optimalSize != null) {
            Log.i(CameraUtils.class.getSimpleName(),
                    optimalSize.width + "/" + optimalSize.height);
        }

        return optimalSize;
    }

    public static void setCameraSizes(Camera camera, Camera.Size cameraSizes) {
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(cameraSizes.width, cameraSizes.height);
        parameters.setPictureSize(cameraSizes.width, cameraSizes.height);

        camera.setParameters(parameters);
    }
}
