package com.enrique.stackblur;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;

/**
 * @see JavaBlurProcess
 * Blur using renderscript.
 */
class RSBlurProcess implements BlurProcess {
	private final Context context;
	private final RenderScript _rs;

	public RSBlurProcess(Context context) {
		this.context = context.getApplicationContext();
		_rs = RenderScript.create(this.context);
	}

	@Override
	public Bitmap blur(Bitmap original, float radius) {
		int width = original.getWidth();
		int height = original.getHeight();
		Bitmap blurred = original.copy(Bitmap.Config.ARGB_8888, true);
		ScriptC_blur blurScript = new ScriptC_blur(_rs);

		Allocation inAllocation = Allocation.createFromBitmap(_rs, blurred, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);

		blurScript.set_gIn(inAllocation);
		blurScript.set_width(width);
		blurScript.set_height(height);
		blurScript.set_radius((int) radius);

		int[] row_indices = new int[height];
		for (int i = 0; i < height; i++) {
			row_indices[i] = i;
		}

		Allocation rows = Allocation.createSized(_rs, Element.U32(_rs), height, Allocation.USAGE_SCRIPT);
		rows.copyFrom(row_indices);

		row_indices = new int[width];
		for (int i = 0; i < width; i++) {
			row_indices[i] = i;
		}

		Allocation columns = Allocation.createSized(_rs, Element.U32(_rs), width, Allocation.USAGE_SCRIPT);
		columns.copyFrom(row_indices);

		blurScript.forEach_blur_h(rows);
		blurScript.forEach_blur_v(columns);
		inAllocation.copyTo(blurred);

		return blurred;
	}
}
