package com.example.abaktest.adapters;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.example.abaktest.MainActivity;
import com.example.abaktest.R;
import com.example.abaktest.R.id;
import com.example.abaktest.R.layout;
import com.example.abaktest.R.string;
import com.example.abaktest.data.Image;
import com.example.abaktest.data.Product;
import com.example.abaktest.utils.ImageSetter;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListAdapter extends BaseAdapter {

	private MainActivity mActivity;
	private Product[] mProducts;
	private ImageSetter imageSetter;

	public ListAdapter(MainActivity context, Product[] products) {
		mActivity = context;
		mProducts = products;
		imageSetter = new ImageSetter();
		
	}

	static class ViewHolder {
		public ImageView image;
		public TextView productName;
		public TextView imageCount;
		public int position;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) (mActivity).getLayoutInflater();
			convertView = inflater.inflate(R.layout.list_element, null);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView.findViewById(R.id.photo_thumb);
			holder.imageCount = (TextView) convertView.findViewById(R.id.photo_count);
			holder.productName = (TextView) convertView.findViewById(R.id.name_product);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.productName.setText(mProducts[position].name);
		holder.imageCount.setText(String.valueOf(mProducts[position].imagesCount) + " " + mActivity.getString(R.string.photo));
		Image image = mActivity.getDBHelper().getFirstImageByProduct(mProducts[position]);
		String filePath = mActivity.getFilesDir().getPath() + File.separator + image.id + ".jpg";
		imageSetter.download(filePath, image.pathThumb, holder.image);
		
		return convertView;
	}

	@Override
	public int getCount() {
		return mProducts.length;
	}


	@Override
	public Object getItem(int pos) {
		return mProducts[pos].name;
	}


	@Override
	public long getItemId(int pos) {
		return pos;
	}

	public int getGroupId(int pos) {
		return mProducts[pos].id;
	}
}
