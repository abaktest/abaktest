package com.example.abaktest.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageResizer {
	
	public static Bitmap resizeBitmapFromUrl(String url, final int REQUIRED_SIZE) {
    	Bitmap bitmap = null;

		try{
		    URL ulrn = new URL(url);
		    HttpURLConnection con = (HttpURLConnection)ulrn.openConnection();
		    BitmapFactory.Options o = new BitmapFactory.Options();
	        o.inJustDecodeBounds = true;
	        int width_tmp = o.outWidth, height_tmp = o.outHeight;
	        int scale = 1;
	        
	        scale = width_tmp / REQUIRED_SIZE;

	        o.inSampleSize = scale;
		    InputStream is1 = con.getInputStream();
		    Bitmap bmp = BitmapFactory.decodeStream(is1, null, o);
		    is1.close();
		    if (null != bmp)
		    	bitmap = bmp;
		    else
		        System.out.println("The Bitmap is NULL");
	        
		} catch(Exception e) {
		}
		
		return bitmap;
    }

	public static Bitmap resize(String path, final int REQUIRED_SIZE) {
		Bitmap result = null;
		
		try
		{
			File file = new File(path);
			BitmapFactory.Options o = new BitmapFactory.Options();
	        o.inJustDecodeBounds = true;
	        FileInputStream stream1 = new FileInputStream(file);
	        BitmapFactory.decodeStream(stream1,null,o);
	        stream1.close();
	
	        //final int REQUIRED_SIZE = 150;
	        int width_tmp = o.outWidth, height_tmp = o.outHeight;
	        int scale = 1;
	        /*while (true)
		    {
		        if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
	                break;
	            width_tmp /= 2;
	            height_tmp /= 2;
	            scale *= 2;
	        }*/
	        /*$k1=$widthNew/$widthOld;
            $k2=$heightNew/$heightOld;
            $k = $k1>$k2 ? $k1 : $k2;
            $return['width']=intval($k*$widthOld);
            $return['height']=intval($k*$heightOld);*/
	        
	        scale = width_tmp / REQUIRED_SIZE;
	
	        BitmapFactory.Options o2 = new BitmapFactory.Options();
	        o2.inSampleSize = scale;
	        FileInputStream stream2 = new FileInputStream(file);
	        result = BitmapFactory.decodeStream(stream2, null, o2);
	        stream2.close();
	
	        //ivImage.setImageBitmap(bitmap);
		}

		catch (FileNotFoundException e){
			//continue;
		}
		catch (IOException e){
			e.printStackTrace();
			//continue;
		}
		
		return result;
	}
	
}
