package com.example.abaktest.listeners;

import com.example.abaktest.MainActivity;

import android.support.v4.app.FragmentActivity;
import android.view.View;

public class OnLoadClickListener implements View.OnClickListener
{
	
	private MainActivity mActivity;
	
	public OnLoadClickListener(MainActivity activity){
		mActivity = activity;
	}

	@Override
	public void onClick(View v)
	{
		if (mActivity != null)
		{
			mActivity.loadListFragment(false);
		}
	}

}
