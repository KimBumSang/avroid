package com.airvideo;

import java.util.ArrayList;

public class BitRateList extends ArrayList<Integer> {
	static BitRateList defaults() {
		BitRateList d = new BitRateList();
		d.add(new Integer(512));
		d.add(new Integer(768));
		d.add(new Integer(1536));
		d.add(new Integer(1024));
		d.add(new Integer(384));
		d.add(new Integer(1280));
		d.add(new Integer(256));
		return d;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
