package com.enrique.stackblur;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.*;


/**
 * @see JavaBlurProcess
 * Blur using renderscript.
 * This should be very fast, even though it uses gausian blurring, it can be run on the gpu,
 * and uses hand-optimised assembly to be as fast as possible.
 */
class RSBlurProcess implements BlurProcess {
	private RenderScript _rs;

	public RSBlurProcess(Context context) {
		_rs = RenderScript.create(context);
	}

	@Override
	public Bitmap blur(Bitmap original, float radius) {
		if(radius == 0)
			return original;

		int width = original.getWidth();
		int height = original.getHeight();

		double scale = 1;
		// ScriptIntrinsicBlur only allows a maximum blur of 25 px
		while(radius > 25) {
			radius /= 2;
			scale /= 2;
		}

		int newWidth = (int)(width * scale);
		// ScriptIntrinsicBlur requires width to be a multiple of 4
		// See https://plus.google.com/+RomanNurik/posts/TLkVQC3M6jW
		newWidth += 4 - newWidth % 4;
		scale = (double)newWidth / width;
		int newHeight = (int) (height * scale);

		Bitmap scaledOriginal = Bitmap.createScaledBitmap(original, newWidth, newHeight, false);
		Bitmap scaledOutput = Bitmap.createBitmap(newWidth, newHeight, original.getConfig());

		ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(_rs, Element.U8_4(_rs));
		Allocation tmpIn = Allocation.createFromBitmap(_rs, scaledOriginal);
		Allocation tmpOut = Allocation.createFromBitmap(_rs, scaledOutput);

		blur.setRadius(radius);
		blur.setInput(tmpIn);
		blur.forEach(tmpOut);
		tmpOut.copyTo(scaledOutput);

		if(scale == 1)
			return scaledOutput;
		else
			return Bitmap.createScaledBitmap(scaledOutput, width, height, true);
	}
}
