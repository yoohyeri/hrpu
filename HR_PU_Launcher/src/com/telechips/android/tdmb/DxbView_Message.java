/*
 * Copyright (C) 2013 Telechips, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.telechips.android.tdmb;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.widget.Toast;

public class DxbView_Message {
	// Toast
	static Toast mToast = null;

	// Dialog - Scan
	static ProgressDialog mDialog_Scan;
	static final int DIALOG_SCAN = 0;
	static final int DIALOG_SCAN_START = 1;
	static final int DIALOG_SCAN_FAIL = 2;

	// Dialog - Preset
	static Builder mBuilder_Preset;
	static Dialog mDialog_Preset;

	static void onPause() {
		if ((mDialog_Preset != null) && mDialog_Preset.isShowing())
			mDialog_Preset.dismiss();
	}

	static void onDestroy() {
		if (mToast != null) {
			mToast.cancel();
			mToast = null;
		}
	}

	static void removeDialog() {
		if (mDialog_Scan != null && mDialog_Scan.isShowing()) {
			DMB_Manager.mContext.removeDialog(DIALOG_SCAN);
		}
	}


	static void selectAreaCode() {
		mBuilder_Preset = new AlertDialog.Builder(DMB_Manager.mContext);

		mBuilder_Preset.setTitle(DMB_Manager.mContext.getResources().getString(R.string.area_code));
		// mBuilder_Preset.setSingleChoiceItems(R.array.area_code_select_entries,
		// Manager_Setting.g_Information.cOption.area_code,
		// DxbPlayer.ListenerOnClick_selectAreaCode);
		// mBuilder_Preset.setOnCancelListener(DxbPlayer.ListenerOnCancel_selectAreaCode);

		// mBuilder_Preset.setPositiveButton(Manager_Setting.mContext.getResources().getString(R.string.cancel),
		// DxbPlayer.ListenerOnClick_selectAreaCode);
		mDialog_Preset = mBuilder_Preset.create();
		mDialog_Preset.show();
	}
}
