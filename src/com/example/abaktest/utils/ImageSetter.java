/*
 * Copyright (C) 2010 The Android Open Source Project
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

package com.example.abaktest.utils;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ImageSetter {

    private int mRequiredSize;

	AlphaAnimation animationFadeIn;
	
	public void download(String path, String url, ImageView imageView) {
		download(path, url, imageView, 0);
	}
	
    public void download(String path, String url, ImageView imageView, int requiredSize) {
      Bitmap bitmap = getBitmapFromCache(path);
      imageView.getContext();
      
      animationFadeIn = new AlphaAnimation(0f,1f);
      animationFadeIn.setDuration(500);
      
      mRequiredSize = requiredSize;
      if (bitmap == null) {
          forceSet(path, url, imageView);
      } else {
          cancelPotentialTask(path, imageView);
          imageView.setImageBitmap(bitmap);
          imageView.setAnimation(animationFadeIn);
      }
  }

    private void forceSet(String path, String url, ImageView imageView) {
        if (path == null) {
            imageView.setImageDrawable(null);
            return;
        }
        if (cancelPotentialTask(path, imageView)) {
        	BitmapTask task = new BitmapTask(imageView);
            DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task);
            imageView.setImageDrawable(downloadedDrawable);
            task.url = url;
            task.execute(path);
        }
    }

    private static boolean cancelPotentialTask(String url, ImageView imageView) {
        BitmapTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

        if (bitmapDownloaderTask != null) {
            String bitmapUrl = bitmapDownloaderTask.path;
            if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
                bitmapDownloaderTask.cancel(true);
            } else {
                return false;
            }
        }
        return true;
    }

    private static BitmapTask getBitmapDownloaderTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof DownloadedDrawable) {
                DownloadedDrawable downloadedDrawable = (DownloadedDrawable)drawable;
                return downloadedDrawable.getBitmapDownloaderTask();
            }
        }
        return null;
    }

    private Bitmap decodeBitmapFromFilesystem(String path) {
    	Bitmap bitmap = BitmapFactory.decodeFile(path);
    	if(mRequiredSize != 0){
    		bitmap = ImageResizer.resize(path, mRequiredSize);
    	}
		return bitmap;
    }
    
    private Bitmap decodeBitmapFromUrl(String url, String filePath) {
    	Bitmap bitmap = null;

		try{
		    URL ulrn = new URL(url);
		    HttpURLConnection con = (HttpURLConnection)ulrn.openConnection();
		    InputStream is =  new BufferedInputStream(con.getInputStream());
		    FileOutputStream fos;
		    fos = new FileOutputStream(filePath);
        	
        	byte[] buffer = new byte[50*1024];
        	int character = -1;
        	while ((character = is.read(buffer)) != -1) {
        		fos.write(buffer, 0, character);
        	}
        	
        	if (is != null)
        		is.close();
        	if (fos != null)
        		fos.close();
        	bitmap = decodeBitmapFromFilesystem(filePath);

		} catch(Exception e) {
			Log.i("decodeexc", "decode failed!");
		}
		
		return bitmap;
    }

    static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int b = read();
                    if (b < 0) {
                        break;
                    } else {
                        bytesSkipped = 1;
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }

    class BitmapTask extends AsyncTask<String, Void, Bitmap> {
        private String path;
        public String url ="";
        private final WeakReference<ImageView> imageViewReference;

        public BitmapTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            path = params[0];
           
            Bitmap bitmapFromFS = decodeBitmapFromFilesystem(path);
            if(bitmapFromFS != null){
            	return bitmapFromFS;
            }
            else {
            	return decodeBitmapFromUrl(url, path);
            }
            	
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            onPostExecuteMethod(bitmap);
        }

		public void onPostExecuteMethod(Bitmap bitmap) {
			addBitmapToCache(path, bitmap);

            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
                BitmapTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
                if (this == bitmapDownloaderTask) {
                    imageView.setImageBitmap(bitmap);
                    imageView.setAnimation(animationFadeIn);
                }
            }
		}
    }
    
    static class DownloadedDrawable extends ColorDrawable {
        private final WeakReference<BitmapTask> bitmapDownloaderTaskReference;

        public DownloadedDrawable(BitmapTask bitmapDownloaderTask) {
            bitmapDownloaderTaskReference = new WeakReference<BitmapTask>(bitmapDownloaderTask);
        }

        public BitmapTask getBitmapDownloaderTask() {
            return bitmapDownloaderTaskReference.get();
        }
    }

    private static final int HARD_CACHE_CAPACITY = 10;
    private final HashMap<String, Bitmap> sHardBitmapCache =
        new LinkedHashMap<String, Bitmap>(HARD_CACHE_CAPACITY / 2, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(LinkedHashMap.Entry<String, Bitmap> eldest) {
            if (size() > HARD_CACHE_CAPACITY) {
                sSoftBitmapCache.put(eldest.getKey(), new SoftReference<Bitmap>(eldest.getValue()));
                return true;
            } else
                return false;
        }
    };

    private final static ConcurrentHashMap<String, SoftReference<Bitmap>> sSoftBitmapCache =
        new ConcurrentHashMap<String, SoftReference<Bitmap>>(HARD_CACHE_CAPACITY / 2);

    private void addBitmapToCache(String url, Bitmap bitmap) {
        if (bitmap != null) {
            synchronized (sHardBitmapCache) {
                sHardBitmapCache.put(url, bitmap);
            }
        }
    }

    private Bitmap getBitmapFromCache(String url) {
        synchronized (sHardBitmapCache) {
            final Bitmap bitmap = sHardBitmapCache.get(url);
            if (bitmap != null) {
                sHardBitmapCache.remove(url);
                sHardBitmapCache.put(url, bitmap);
                return bitmap;
            }
        }

        if(url != null && sSoftBitmapCache.get(url) != null){
        	SoftReference<Bitmap> bitmapReference = sSoftBitmapCache.get(url);
	        if (bitmapReference != null) {
	            final Bitmap bitmap = bitmapReference.get();
	            if (bitmap != null) {
	                return bitmap;
	            } else {
	                sSoftBitmapCache.remove(url);
	            }
	        }
        }
        return null;
    }
}

