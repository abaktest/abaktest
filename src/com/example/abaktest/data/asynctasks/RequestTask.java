package com.example.abaktest.data.asynctasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.abaktest.MainActivity;
import com.example.abaktest.R;
import com.example.abaktest.R.string;
import com.example.abaktest.data.Image;
import com.example.abaktest.data.Product;
import com.example.abaktest.utils.Constants;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class RequestTask extends AsyncTask<Void, Void, Void>
{
	
	private static final String CONTENT = "content";
	private static final String PRODUCTS = "products";
	private static final String ID = "id";
	private static final String NAME = "name";
	private static final String IMG_COUNT = "images_cnt";
	private static final String IMAGES = "images";
	private static final String POSITION = "position";
	private static final String PATH_THUMB = "path_thumb";
	private static final String PATH_BIG = "path_big";
	
	public interface OnRequestCompleteListener{
		void onRequestComplete(String result);
	}

	MainActivity mActivity;
	private OnRequestCompleteListener mListener;
	private RequestTask(){
		
	}
	
	public RequestTask(MainActivity activity, OnRequestCompleteListener listener){
		mActivity = activity;
		mListener = listener;
	}
	
	@Override
	protected Void doInBackground(Void... params)
	{
		makeRequest();
		
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result)
	{
		if(mListener != null){
			mListener.onRequestComplete("");
		}
	}
	
	private JSONArray makeRequest()
	{
		JSONArray result = null;

		try
		{

			HttpResponse httpResponse = null;
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, Constants.CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParameters, Constants.SOCKET_TIMEOUT);
			httpResponse = makeGetRequest(mActivity.getString(R.string.download_url), httpParameters);
		
			try
			{
				if (httpResponse != null)
				{
					
					InputStream inputStream = httpResponse.getEntity().getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
					StringBuilder total = new StringBuilder();
					String line;

					while ((line = reader.readLine()) != null)
						total.append(line);
					
					result = parse(total.toString());
					if (inputStream != null)
						inputStream.close();
					
					
				}
			} finally
			{
				
			}

		} catch (ConnectTimeoutException e)
		{
			e.printStackTrace();
		} catch (SocketTimeoutException e)
		{
			e.printStackTrace();
		} catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}

	private JSONArray parse(String json) throws JSONException
	{
		JSONObject rootJson = new JSONObject(json);
		JSONObject content = rootJson.optJSONObject(CONTENT);
		if(content != null){
			JSONArray productsArray = content.optJSONArray(PRODUCTS);
			if(productsArray != null){
				ArrayList<Product> products = new ArrayList<Product>();
				for (int i = 0; i < productsArray.length(); i++)
				{
					JSONObject productJson = productsArray.getJSONObject(i);
					Product product = new Product();
					int productId = productJson.optInt(ID);
					product.imagesCount = productJson.optInt(IMG_COUNT);
					product.name = productJson.optString(NAME);
					if(productId != 0){
						product.id = productId;
						products.add(product);
						JSONArray imagesArray = productJson.optJSONArray(IMAGES);
						if(imagesArray != null){
							ArrayList<Image> images = new ArrayList<Image>();
							for(int j = 0; j < imagesArray.length(); j++){
								JSONObject imageJson = imagesArray.getJSONObject(j);
								Image image = new Image();
								int imageId = imageJson.optInt(ID);
								int position = imageJson.optInt(POSITION);
								String pathThumb = imageJson.optString(PATH_THUMB);
								String pathBig = imageJson.optString(PATH_BIG);
								if(imageId != 0){
									image.id = imageId;
									image.position = position;
									image.pathBig = pathBig;
									image.pathThumb = pathThumb;
									image.productId = productId;
									images.add(image);
								}
							}
							mActivity.getDBHelper().putImagesList(images);
						}
					}
				}
				mActivity.getDBHelper().putProductsList(products);
			}
		}
		return null;
	}

	protected HttpResponse makeGetRequest(String url, HttpParams params) throws Exception
	{

		DefaultHttpClient httpclient = new DefaultHttpClient(params);

		HttpGet httpget = new HttpGet(url);

		return httpclient.execute(httpget);
	}

}
