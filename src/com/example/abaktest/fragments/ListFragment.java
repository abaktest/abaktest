package com.example.abaktest.fragments;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.abaktest.MainActivity;
import com.example.abaktest.R;
import com.example.abaktest.R.id;
import com.example.abaktest.R.layout;
import com.example.abaktest.R.string;
import com.example.abaktest.adapters.ListAdapter;
import com.example.abaktest.data.Product;
import com.example.abaktest.data.asynctasks.RequestTask;
import com.example.abaktest.listeners.OnClearClickListener;

public class ListFragment extends Fragment implements RequestTask.OnRequestCompleteListener
{
	public static final String IS_LIST_LOADED = "isListLoaded";
	ListView mListView;
	View progressBar;
	private View rootView;
	private MainActivity mActivity;

	public ListFragment()
	{
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		mActivity = (MainActivity) getActivity();
		rootView = inflater.inflate(R.layout.fragment_list_layout, container, false);
		rootView.findViewById(R.id.clean_button).setOnClickListener(new OnClearClickListener((MainActivity) getActivity()));
		progressBar = rootView.findViewById(R.id.progressBar);
		mListView = (ListView) rootView.findViewById(R.id.listView);
		if(getArguments().getBoolean(IS_LIST_LOADED)){
			setListAdapter(getProducts());
		} else {
			(new RequestTask(mActivity, this)).execute();	
		}
		
		return rootView;
	}

	@Override
	public void onRequestComplete(String result)
	{
		(new GetTheListTask()).execute();
		SharedPreferences prefs = mActivity.getSharedPreferences(mActivity.getString(R.string.prefs), Context.MODE_PRIVATE);
		prefs.edit().putBoolean(mActivity.getString(R.string.prefs_is_fully_loaded), true).commit();
	}
	
	private Product[] getProducts()
	{
		ArrayList<Product> productsList = mActivity.getDBHelper().getProducts();
		Product[] products = new Product[productsList.size()];
		products = productsList.toArray(products);
		return products;
	}

	private void setListAdapter(Product[] result)
	{
		mListView.setAdapter(new ListAdapter(mActivity, result));
		progressBar.setVisibility(View.GONE);
	}

	class GetTheListTask extends AsyncTask<Void, Void, Product[]>{

		@Override
		protected Product[] doInBackground(Void... params)
		{
			return getProducts();
		}
		
		@Override
		protected void onPostExecute(Product[] result)
		{
			setListAdapter(result);
		}
		
	}
	
}