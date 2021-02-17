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

import android.database.Cursor;

public class Information {
	public COMM cCOMM = new COMM();
	INDI cINDI = new INDI();
	LIST cLIST = new LIST();
	OPTION cOption = new OPTION();

	public class COMM {
		// Display
		int iDisplayWidth;
		int iDisplayHeight;
		// float fDensity;

		// Service List
		Cursor curChannels = null;

		public boolean isEnable_Video = false;

		int iCount_TV = 0;
		int iCount_Radio = 0;
		int iCount_Current = 0;

		int iCurrent_TV;
		int iCurrent_Radio;
	}

	class INDI {
		int iSignal_Level = 4;
		int iSignal_Count = 0;
	}

	class LIST {
		boolean VISIBLE = true;

		int iCount_Tab = 2;
		int iCurrent_Tab = 0;
	}

	class OPTION {
		int scan_manual;
	}
}

class Page_Descriptor {
	String sLanguageCode;
	int iPage;
}
