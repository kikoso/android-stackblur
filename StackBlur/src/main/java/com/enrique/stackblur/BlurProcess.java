package com.enrique.stackblur;

import android.graphics.Bitmap;

interface BlurProcess {
	/**
	 * Process the given image, blurring by the supplied radius.
	 * If radius is 0, this will return original
	 * @param original the bitmap to be blurred
	 * @param radius the radius in pixels to blur the image
	 * @return the blurred version of the image.
	 */
    public Bitmap blur(Bitmap original, float radius);
}
