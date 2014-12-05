package com.enrique.stackblur;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * @see JavaBlurProcess
 * Blur using the NDK and native code.
 */
class NativeBlurProcess implements BlurProcess {
	private static native void functionToBlur(Bitmap bitmapOut, int radius, int threadCount, int threadIndex, int round);

	static {
		System.loadLibrary("blur");
	}

	@Override
	public Bitmap blur(Bitmap original, float radius) {
		Bitmap bitmapOut = original.copy(Bitmap.Config.ARGB_8888, true);

		int cores = StackBlurManager.EXECUTOR_THREADS;

		ArrayList<NativeTask> horizontal = new ArrayList<NativeTask>(cores);
		ArrayList<NativeTask> vertical = new ArrayList<NativeTask>(cores);
		for (int i = 0; i < cores; i++) {
			horizontal.add(new NativeTask(bitmapOut, (int) radius, cores, i, 1));
			vertical.add(new NativeTask(bitmapOut, (int) radius, cores, i, 2));
		}

		try {
			StackBlurManager.EXECUTOR.invokeAll(horizontal);
		} catch (InterruptedException e) {
			return bitmapOut;
		}

		try {
			StackBlurManager.EXECUTOR.invokeAll(vertical);
		} catch (InterruptedException e) {
			return bitmapOut;
		}
		return bitmapOut;
	}

	private static class NativeTask implements Callable<Void> {
		private final Bitmap _bitmapOut;
		private final int _radius;
		private final int _totalCores;
		private final int _coreIndex;
		private final int _round;

		public NativeTask(Bitmap bitmapOut, int radius, int totalCores, int coreIndex, int round) {
			_bitmapOut = bitmapOut;
			_radius = radius;
			_totalCores = totalCores;
			_coreIndex = coreIndex;
			_round = round;
		}

		@Override public Void call() throws Exception {
			functionToBlur(_bitmapOut, _radius, _totalCores, _coreIndex, _round);
			return null;
		}

	}
}
