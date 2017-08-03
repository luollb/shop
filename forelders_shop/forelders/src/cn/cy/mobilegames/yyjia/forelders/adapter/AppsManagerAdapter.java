/*
 * Copyright (C) 2010 mAPPn.Inc
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
package cn.cy.mobilegames.yyjia.forelders.adapter;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import cn.cy.mobilegames.yyjia.forelders.ApiAsyncTask.ApiRequestListener;

/**
 * Client ListView associating adapter<br>
 * It has lazyload feature, which load data on-demand.
 * 
 * @author andrew.wang
 * 
 */
public class AppsManagerAdapter extends BaseAdapter implements Observer,
		ApiRequestListener {

	/*
	 * How many items are in the data set represented by this Adapter.
	 */
	@Override
	public int getCount() {

		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg1 instanceof Integer) {

		} else if (arg1 instanceof HashMap) {

		}
	}

	@Override
	public void onSuccess(int method, Object obj) {

	}

	@Override
	public void onError(int method, int statusCode) {
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		return null;
	}
}