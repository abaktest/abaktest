package com.example.abaktest.listeners;

import java.io.File;

import com.example.abaktest.MainActivity;
import com.example.abaktest.R;
import com.example.abaktest.R.string;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

public class OnClearClickListener implements View.OnClickListener
{
	
	private MainActivity mActivity;
	
	public OnClearClickListener(MainActivity activity){
		mActivity = activity;
	}

	@Override
	public void onClick(View v)
	{
		if (mActivity != null)
		{
			mActivity.loadMainFragment();
			File file[] = mActivity.getFilesDir().listFiles();
			Log.d("Files", "Size: "+ file.length);
			for (int i=0; i < file.length; i++)
			{
				try
				{
					Log.d("Files", "deleted: "+ String.valueOf(i));
				    file[i].delete();
				    mActivity.getDBHelper().dropAllTables();
				    SharedPreferences prefs = mActivity.getSharedPreferences(mActivity.getString(R.string.prefs), Context.MODE_PRIVATE);
					prefs.edit().remove(mActivity.getString(R.string.prefs_is_fully_loaded)).commit();
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}

}
