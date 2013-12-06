package com.enrique.stackblur;

import android.graphics.Bitmap;

/**
 * @see JavaBlurProcess
 * Blur using the NDK and native code.
 */
class NativeBlurProcess implements BlurProcess {
	private native void functionToBlur(Bitmap bitmapIn, Bitmap bitmapOut, int radius) ;

	static {
		System.loadLibrary("blur");
	}

	@Override
	public Bitmap blur(Bitmap original, float radius) {
		Bitmap bitmapIn = original.copy(Bitmap.Config.ARGB_8888, true);
		//Create a copy
		Bitmap bitmapOut = original.copy(Bitmap.Config.ARGB_8888, true);
		//BlurProcess the copy
		functionToBlur(bitmapIn, bitmapOut, (int)radius);
		return bitmapOut;
	}
}
