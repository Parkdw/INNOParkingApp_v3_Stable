/*
 * Copyright 2016 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nhn.android.mapviewer;

import android.app.Application;
import android.util.Log;

import com.tsengvn.typekit.Typekit;

/**
 *
 */
public class NMapViewerApplication extends Application {

	private static NMapViewerApplication instance;

	public static NMapViewerApplication getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {

		super.onCreate();

		Typekit.getInstance()
				.addNormal(Typekit.createFromAsset(this, "fonts/OSeongandHanEum-Regular.otf"))
				.addBold(Typekit.createFromAsset(this, "fonts/OSeongandHanEum-Bold.otf"));

		instance = this;
	}
}