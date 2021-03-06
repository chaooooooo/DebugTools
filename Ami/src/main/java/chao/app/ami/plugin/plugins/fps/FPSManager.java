package chao.app.ami.plugin.plugins.fps;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.Choreographer;
import chao.app.ami.base.AMIToast;
import chao.app.ami.logs.LogHelper;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author qinchao
 * @since 2018/9/10
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class FPSManager implements Handler.Callback {

    private static final String TAG = FPSManager.class.getSimpleName();

    private static final int HANDLER_FPS_UPDATE = 1;

    private Handler handler;

    private int lastFps = 0;

    private OnFPSUpdateListener onFPSUpdateListener;

    private FPSFrameCallback frameCallback = new FPSFrameCallback();


    public FPSManager(@NonNull OnFPSUpdateListener listener) {
        onFPSUpdateListener = listener;
        handler = new Handler(Looper.getMainLooper(), this);
    }

    private void notifyFPSUpdate(int fps) {
        Message message = handler.obtainMessage(HANDLER_FPS_UPDATE);
        message.arg1 = fps;
        handler.sendMessage(message);
    }

    public boolean start() {
        Looper looper = Looper.myLooper();
        if (looper != Looper.getMainLooper()) {
            LogHelper.w(TAG, "FPS只有在主线程下启动才能生效!");
            return false;
        }
        stop();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Choreographer.getInstance().postFrameCallback(frameCallback);
            return true;
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    AMIToast.show("fps只支持Android API16(Android4.1)以上");
                }
            });
            return false;
        }
    }

    public void stop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Choreographer.getInstance().removeFrameCallback(frameCallback);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == HANDLER_FPS_UPDATE) {
            final int fps = msg.arg1;
            if (fps < 60) {
                onFPSUpdateListener.onFpsUpdate(msg.arg1);
                return true;
            }
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onFPSUpdateListener.onFpsUpdate(fps);
                }
            }, 300);
            return true;
        }
        return false;
    }

    private class FPSFrameCallback implements Choreographer.FrameCallback {
        @Override
        public void doFrame(long frameTimeNanos) {
            executorService.execute(new CalcFrameTask());
            Choreographer.getInstance().postFrameCallback(this);
        }
    }

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private ArrayList<Long> frameTimes = new ArrayList<>();

    public class CalcFrameTask implements Runnable {

        @Override
        public void run() {
            int fps;
            long currentTime = System.currentTimeMillis();
            while (frameTimes.size() > 0) {
                long first = frameTimes.get(0);
                if (currentTime - first > 1000) {
                    frameTimes.remove(0);
                } else {
                    break;
                }
            }
            frameTimes.add(currentTime);
            fps = frameTimes.size();

            if (fps != lastFps) {
                notifyFPSUpdate(fps);
                lastFps = fps;
            }
        }
    }


    public interface OnFPSUpdateListener {
        void onFpsUpdate(int fps);
    }
}
