package com.kemallette.MultiChoiceExpandableList;


import java.util.Arrays;

import android.support.v4.util.LongSparseArray;
import android.util.Log;


public class Util{

	private static final String	TAG	= "Util";


	public static <T> long[]
		getKeys(final LongSparseArray<T> longSparseArray){

		if (longSparseArray == null){
			Log.e(	TAG,
					"LongSparseArray lsArray was null!");
			return new long[0];
		}

		final LongSparseArray<T> array = longSparseArray;
		final int count = array.size();
		final long[] keys = new long[count];

		for (int i = 0; i < count; i++){
			keys[i] = array.keyAt(i);
		}
		return keys;
	}


	public static <T> T[] concatAll(final T[] first, final T[]... rest){

		int totalLength = first.length;
		for (final T[] array : rest){
			totalLength += array.length;
		}
		final T[] result = Arrays.copyOf(	first,
											totalLength);
		int offset = first.length;
		for (final T[] array : rest){
			System.arraycopy(	array,
								0,
								result,
								offset,
								array.length);
			offset += array.length;
		}
		return result;
	}
}
