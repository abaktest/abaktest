package com.example.abaktest.fragments;

import com.example.abaktest.MainActivity;
import com.example.abaktest.R;
import com.example.abaktest.R.id;
import com.example.abaktest.R.layout;
import com.example.abaktest.listeners.OnLoadClickListener;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainFragment extends Fragment
{

	public MainFragment()
	{
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		rootView.findViewById(R.id.load_button).setOnClickListener(new OnLoadClickListener((MainActivity) this.getActivity()));
		return rootView;
	}
}