package com.example.abaktest.data;

import android.provider.BaseColumns;

public class DBData
{
	private DBData(){
	}
	
	public static abstract class ProductsTable implements BaseColumns {
		public static final String TABLE_NAME = "products";
		public static final String COLUMN_ID = "id";
		public static final String COLUMN_NAME = "name";
		public static final String COLUMN_IMAGES_CNT = "images_cnt";
	}
	
	public static abstract class ImagesTable implements BaseColumns {
		public static final String TABLE_NAME = "images";
		public static final String COLUMN_ID = "id";
		public static final String COLUMN_PRODUCT_ID = "product_id";
		public static final String COLUMN_PATH_THUMB = "path_thumb";
		public static final String COLUMN_PATH_BIG = "path_big";
		public static final String COLUMN_POSITION = "position";
	}
}
