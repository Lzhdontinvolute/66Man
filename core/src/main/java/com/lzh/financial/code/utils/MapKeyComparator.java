package com.lzh.financial.code.utils;

import java.util.Comparator;

public class MapKeyComparator implements Comparator<String> {

	@Override
	public int compare(String str1, String str2) {
		
		return str2.compareTo(str1);
	}

}
