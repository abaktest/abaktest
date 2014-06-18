package com.example.abaktest;


import com.example.abaktest.data.DBHelper;
import com.example.abaktest.fragments.ListFragment;
import com.example.abaktest.fragments.MainFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.CalendarContract.Instances;

public class MainActivity extends FragmentActivity
{
	
	private Fragment currentFragment;
	private DBHelper dbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		dbHelper = new DBHelper(this);
		SharedPreferences prefs = getSharedPreferences(getString(R.string.prefs), Context.MODE_PRIVATE);
		boolean isListLoaded = prefs.getBoolean(getString(R.string.prefs_is_fully_loaded), false);
		if(!isListLoaded){
			loadMainFragment();
		} else {
			loadListFragment(true);
		}
		
	}
	
	public DBHelper getDBHelper(){
		return dbHelper;
	}
	
	public void loadMainFragment(){
		Fragment fragment = new MainFragment();
		getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
		currentFragment = fragment;
	}
	
	public void loadListFragment(boolean isListLoaded){
		if(!isListLoaded)
			dbHelper.manualResetBase();
		Fragment fragment = new ListFragment();
		Bundle args = new Bundle();
		args.putBoolean(ListFragment.IS_LIST_LOADED, isListLoaded);
		fragment.setArguments(args);
		getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
		currentFragment = fragment;
	}
	
	@Override
	public void onBackPressed()
	{
		if(currentFragment != null && currentFragment instanceof ListFragment){
			loadMainFragment();
		} else {
			super.onBackPressed();	
		}
		
	}

}
