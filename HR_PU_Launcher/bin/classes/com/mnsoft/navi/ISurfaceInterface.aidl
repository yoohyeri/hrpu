package com.mnsoft.navi;

import android.view.Surface;

interface ISurfaceInterface {

	void surfaceCreated(in Surface surface);
	void surfaceChanged(String key, int f, int w, int h);
	void surfaceDestroyed(String key);
}