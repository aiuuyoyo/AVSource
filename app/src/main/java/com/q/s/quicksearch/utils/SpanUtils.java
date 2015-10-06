package com.q.s.quicksearch.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.q.s.quicksearch.R;
import com.q.s.quicksearch.VideoDetailsActivity;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SpanUtils {
    private static final String IMAGES_PATH = Environment.getExternalStorageDirectory() + File.separator + "AVSource" + File.separator + "images";
    public static final File IMAGES_DIR = new File(IMAGES_PATH);
    private static final ExecutorService EXECUTORS = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    static {
        if (!IMAGES_DIR.exists()) {
            IMAGES_DIR.mkdirs();
        }
    }

    public static volatile SpannableStringBuilder spannableString = new SpannableStringBuilder();

    /**
     * @param htmlStr html文本
     * @param context 上下文对象
     * @return size of ImageSpan
     */
    public static int toSpannableString(VideoDetailsActivity context, String htmlStr, int maxWidth,
                                        int maxHeight) {
        spannableString.clear();
        Spanned spanned = Html.fromHtml(htmlStr);
        spannableString.append(spanned);
        int length = htmlStr.length();
        URLSpan[] urlSpans = spannableString.getSpans(0, length, URLSpan.class);
        for (URLSpan span : urlSpans) {
            MyClickSpan clickSpan = new MyClickSpan(span.getURL(), context);
            int start = spannableString.getSpanStart(span);
            int end = spannableString.getSpanEnd(span);
            spannableString.setSpan(clickSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        ImageSpan[] imageSpans = spannableString.getSpans(0, length, ImageSpan.class);
        Drawable launcher = context.getResources().getDrawable(R.mipmap.ic_launcher);
        launcher.setBounds(0, 0, launcher.getIntrinsicWidth(), launcher.getIntrinsicHeight());
        for (ImageSpan imageSpan : imageSpans) {
            String source = imageSpan.getSource();
            String name = MD5.getMD5(source);
            String path = IMAGES_PATH + File.separator + name;
            File iamge = new File(path);
            int start = spannableString.getSpanStart(imageSpan);
            int end = spannableString.getSpanEnd(imageSpan);
            if (iamge.exists()) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(path, options);
                float scale = Math.min(maxWidth * 1.0f / options.outWidth, maxHeight * 1.0f / options.outHeight);
                int width = (int) (options.outWidth * scale);
                int height = (int) (options.outHeight * scale);
                Bitmap dest = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8);
                ImageSpan what = new ImageSpan(getDrawable(context, dest, width, height));
                spannableString.setSpan(what, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                EXECUTORS.submit(new LoadLocalThread(context, options, path, maxWidth, maxHeight, start, end));
            } else {
                ImageSpan what = new ImageSpan(launcher);
                spannableString.setSpan(what, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                EXECUTORS.submit(new LoadNetThread(context, source, iamge, maxWidth, maxHeight, start, end));
            }
        }
        return imageSpans.length;
    }

    private static class LoadLocalThread implements Runnable {
        private VideoDetailsActivity context;
        private BitmapFactory.Options options;
        private String path;
        private int maxWidth, maxHeight, start, end;

        public LoadLocalThread(VideoDetailsActivity context, BitmapFactory.Options options, String path,
                               int maxWidth, int maxHeight, int start, int end) {
            this.context = context;
            this.options = options;
            this.path = path;
            this.maxWidth = maxWidth;
            this.maxHeight = maxHeight;
            this.start = start;
            this.end = end;
        }

        @Override
        public void run() {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            try {
                float scale = Math.min(maxWidth * 1.0f / options.outWidth, maxHeight * 1.0f / options.outHeight);
                int width = (int) (options.outWidth * scale);
                int height = (int) (options.outHeight * scale);
                Bitmap destBitmap = null;
                options.inJustDecodeBounds = false;
                if (scale > 0 && scale <= 1.0f) {//加载缩小图片
                    destBitmap = decodeImage(path, width, height, options);
                } else {//原始图片
                    Bitmap srcBitmap = BitmapFactory.decodeFile(path, options);
                    destBitmap = Bitmap.createScaledBitmap(srcBitmap, width, height, true);
                    if (srcBitmap != null) {
                        srcBitmap.recycle();
                        srcBitmap = null;
                    }
                }
                Drawable d2 = getDrawable(context, destBitmap, width, height);
                ImageSpan what = new ImageSpan(d2);
                spannableString.setSpan(what, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } finally {
                context.refresh();
            }
        }
    }

    private static class LoadNetThread implements Runnable {
        private VideoDetailsActivity context;
        private String source;
        private File image;
        private int maxWidth, maxHeight, start, end;

        public LoadNetThread(VideoDetailsActivity context, String source, File image,
                             int maxWidth, int maxHeight, int start, int end) {
            this.context = context;
            this.source = source;
            this.image = image;
            this.maxWidth = maxWidth;
            this.maxHeight = maxHeight;
            this.start = start;
            this.end = end;
        }

        @Override
        public void run() {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            boolean isSuccess = false;
            try {
                isSuccess = loadNetworkImageByOkhttp(context, source, image);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (!isSuccess) {
                    context.count();
                } else {
                    String path = image.getAbsolutePath();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(path, options);
                    float scale = Math.min(maxWidth * 1.0f / options.outWidth, maxHeight * 1.0f / options.outHeight);
                    int width = (int) (options.outWidth * scale);
                    int height = (int) (options.outHeight * scale);
                    Bitmap destBitmap = null;
                    options.inJustDecodeBounds = false;
                    if (scale > 0 && scale <= 1.0f) {//加载缩小图片
                        destBitmap = decodeImage(path, width, height, options);
                    } else {//原始图片
                        Bitmap src = BitmapFactory.decodeFile(path, options);
                        destBitmap = Bitmap.createScaledBitmap(src, width, height, true);
                        if (src != null) {
                            src.recycle();
                            src = null;
                        }
                    }
                    Drawable d2 = SpanUtils.getDrawable(context, destBitmap, width, height);
                    ImageSpan what = new ImageSpan(d2);
                    spannableString.setSpan(what, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    context.refresh();
                }
            }
        }
    }

    public static Drawable getDrawable(Context context, Bitmap bitmap, int reqWidth, int reqHeight) {
        @SuppressWarnings("deprecation")
        BitmapDrawable d = new BitmapDrawable(bitmap);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);

        int width, height;
        int originalWidthScaled = (int) (d.getIntrinsicWidth() * metrics.density);
        int originalHeightScaled = (int) (d.getIntrinsicHeight() * metrics.density);
        if (originalWidthScaled > reqWidth) {
            height = reqHeight;
            width = reqWidth;
        } else {
            height = originalHeightScaled;
            width = originalWidthScaled;
        }
        d.setBounds(0, 0, width, height);
        return d;
    }

    private static boolean loadNetworkImageByOkhttp(Context context, String source, File image) throws IOException {
        if (!NetUtils.checkNetConnectivityAvailable(context)) {
            return false;
        }
        boolean ret = false;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(source).build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            File tmpFile = new File(image.getAbsolutePath() + ".tmp");
            OutputStream outputStream = new FileOutputStream(tmpFile);
            InputStream inputStream = response.body().byteStream();
            try {
                byte[] buf = new byte[16 * 1024];
                int len = 0;
                while ((len = inputStream.read(buf)) != -1) {
                    outputStream.write(buf, 0, len);
                }
                outputStream.flush();
                tmpFile.renameTo(image);
                ret = true;
            } finally {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream == null) {
                    inputStream.close();
                }
            }
        }
        return ret;
    }

    /**
     * @param reqWidth  需要加载的图片宽度
     * @param reqHeight
     * @param options
     * @return
     */
    private static Bitmap decodeImage(String path, int reqWidth, int reqHeight, BitmapFactory.Options options) {
        return EffecientLoadImageUtils.decodeSampledBitmapFromFile(path, reqWidth, reqHeight, options);
    }
}