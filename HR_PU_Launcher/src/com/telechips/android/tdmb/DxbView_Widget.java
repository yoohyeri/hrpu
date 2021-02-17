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

import com.mobilus.hp.launcher.HP_Manager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DxbView_Widget {
	public static Component.cWidgetView gComponent = null;

	/**
	 * Initialize
	 */
	static public void init() {
		gComponent = DMB_Manager.g_Component.widgetview;
		setComponent();
	}

	/**
	 * GUI Initialize
	 */
	static private void setComponent() {
		gComponent.vVideo = HP_Manager.mContext.findViewById(R.id.dmb_widget_preview);
		gComponent.llWidgetTitleBar = (LinearLayout) HP_Manager.mContext.findViewById(R.id.dmb_widget_title_bar);
		gComponent.channelIcon = (TextView) HP_Manager.mContext.findViewById(R.id.dmb_widget_icon);
		gComponent.channelName = (TextView) HP_Manager.mContext.findViewById(R.id.dmb_widget_channel_name);
	}

	/**
	 * 화면 클릭 시 DMB 실행
	 */
	static OnClickListener ListenerOnClick = new OnClickListener() {
		public void onClick(View v) {
			int id = v.getId();
		}
	};
}