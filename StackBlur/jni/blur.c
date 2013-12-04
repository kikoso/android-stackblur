#include <jni.h>
#include <string.h>
#include <math.h>
#include <stdio.h>
#include <android/log.h>
#include <android/bitmap.h>

#define LOG_TAG "libbitmaputils"
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

typedef struct {
    uint8_t red;
    uint8_t green;
    uint8_t blue;
    uint8_t alpha;
} rgba;

JNIEXPORT void JNICALL Java_com_enrique_stackblur_StackBlurManager_functionToBlur(JNIEnv* env, jobject obj, jobject bitmapIn, jobject bitmapOut, jint radius) {
    LOGI("Blurring bitmap...");

    // Properties
    AndroidBitmapInfo   infoIn;
    void*               pixelsIn;
    AndroidBitmapInfo   infoOut;
    void*               pixelsOut;

    int ret;

    // Get image info
    if ((ret = AndroidBitmap_getInfo(env, bitmapIn, &infoIn)) < 0 || (ret = AndroidBitmap_getInfo(env, bitmapOut, &infoOut)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return;
    }

    // Check image
    if (infoIn.format != ANDROID_BITMAP_FORMAT_RGBA_8888 || infoOut.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap format is not RGBA_8888!");
        LOGE("==> %d %d", infoIn.format, infoOut.format);
        return;
    }

    // Lock all images
    if ((ret = AndroidBitmap_lockPixels(env, bitmapIn, &pixelsIn)) < 0 || (ret = AndroidBitmap_lockPixels(env, bitmapOut, &pixelsOut)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
    }

    int h = infoIn.height;
    int w = infoIn.width;

    LOGI("Image size is: %i %i", w, h);

    rgba* input = (rgba*) pixelsIn;
    rgba* output = (rgba*) pixelsOut;

    int wm = w - 1;
    int hm = h - 1;
    int wh = w * h;
    int whMax = max(w, h);
    int div = radius + radius + 1;

    int r[wh];
    int g[wh];
    int b[wh];
    int rsum, gsum, bsum, x, y, i, yp, yi, yw;
    rgba p;
    int vmin[whMax];

    int divsum = (div + 1) >> 1;
    divsum *= divsum;
    int dv[256 * divsum];
    for (i = 0; i < 256 * divsum; i++) {
        dv[i] = (i / divsum);
    }

    yw = yi = 0;

    int stack[div][3];
    int stackpointer;
    int stackstart;
    int rbs;
    int ir;
    int ip;
    int r1 = radius + 1;
    int routsum, goutsum, boutsum;
    int rinsum, ginsum, binsum;

    for (y = 0; y < h; y++) {
        rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
        for (i = -radius; i <= radius; i++) {
            p = input[yi + min(wm, max(i, 0))];

            ir = i + radius; // same as sir

            stack[ir][0] = p.red;
            stack[ir][1] = p.green;
            stack[ir][2] = p.blue;
            rbs = r1 - abs(i);
            rsum += stack[ir][0] * rbs;
            gsum += stack[ir][1] * rbs;
            bsum += stack[ir][2] * rbs;
            if (i > 0) {
                rinsum += stack[ir][0];
                ginsum += stack[ir][1];
                binsum += stack[ir][2];
            } else {
                routsum += stack[ir][0];
                goutsum += stack[ir][1];
                boutsum += stack[ir][2];
            }
        }
        stackpointer = radius;

        for (x = 0; x < w; x++) {

            r[yi] = dv[rsum];
            g[yi] = dv[gsum];
            b[yi] = dv[bsum];

            rsum -= routsum;
            gsum -= goutsum;
            bsum -= boutsum;

            stackstart = stackpointer - radius + div;
            ir = stackstart % div; // same as sir

            routsum -= stack[ir][0];
            goutsum -= stack[ir][1];
            boutsum -= stack[ir][2];

            if (y == 0) {
                vmin[x] = min(x + radius + 1, wm);
            }
            p = input[yw + vmin[x]];

            stack[ir][0] = p.red;
            stack[ir][1] = p.green;
            stack[ir][2] = p.blue;

            rinsum += stack[ir][0];
            ginsum += stack[ir][1];
            binsum += stack[ir][2];

            rsum += rinsum;
            gsum += ginsum;
            bsum += binsum;

            stackpointer = (stackpointer + 1) % div;
            ir = (stackpointer) % div; // same as sir

            routsum += stack[ir][0];
            goutsum += stack[ir][1];
            boutsum += stack[ir][2];

            rinsum -= stack[ir][0];
            ginsum -= stack[ir][1];
            binsum -= stack[ir][2];

            yi++;
        }
        yw += w;
    }
    for (x = 0; x < w; x++) {
        rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
        yp = -radius * w;
        for (i = -radius; i <= radius; i++) {
            yi = max(0, yp) + x;

            ir = i + radius; // same as sir

            stack[ir][0] = r[yi];
            stack[ir][1] = g[yi];
            stack[ir][2] = b[yi];

            rbs = r1 - abs(i);

            rsum += r[yi] * rbs;
            gsum += g[yi] * rbs;
            bsum += b[yi] * rbs;

            if (i > 0) {
                rinsum += stack[ir][0];
                ginsum += stack[ir][1];
                binsum += stack[ir][2];
            } else {
                routsum += stack[ir][0];
                goutsum += stack[ir][1];
                boutsum += stack[ir][2];
            }

            if (i < hm) {
                yp += w;
            }
        }
        yi = x;
        stackpointer = radius;
        for (y = 0; y < h; y++) {
            output[yi].red = dv[rsum];
            output[yi].green = dv[gsum];
            output[yi].blue = dv[bsum];

            rsum -= routsum;
            gsum -= goutsum;
            bsum -= boutsum;

            stackstart = stackpointer - radius + div;
            ir = stackstart % div; // same as sir

            routsum -= stack[ir][0];
            goutsum -= stack[ir][1];
            boutsum -= stack[ir][2];

            if (x == 0) vmin[y] = min(y + r1, hm) * w;
            ip = x + vmin[y];

            stack[ir][0] = r[ip];
            stack[ir][1] = g[ip];
            stack[ir][2] = b[ip];

            rinsum += stack[ir][0];
            ginsum += stack[ir][1];
            binsum += stack[ir][2];

            rsum += rinsum;
            gsum += ginsum;
            bsum += binsum;

            stackpointer = (stackpointer + 1) % div;
            ir = stackpointer; // same as sir

            routsum += stack[ir][0];
            goutsum += stack[ir][1];
            boutsum += stack[ir][2];

            rinsum -= stack[ir][0];
            ginsum -= stack[ir][1];
            binsum -= stack[ir][2];

            yi += w;
        }
    }

    // Unlocks everything
    AndroidBitmap_unlockPixels(env, bitmapIn);
    AndroidBitmap_unlockPixels(env, bitmapOut);

    LOGI ("Bitmap blurred.");
}

int min(int a, int b) {
    return a > b ? b : a;
}

int max(int a, int b) {
    return a > b ? a : b;
}
