package com.airvideo;

import java.util.ArrayList;

public class BitRateList extends ArrayList<Integer> {
	private static final long serialVersionUID = -7874844503664921870L;

	static BitRateList defaults() {
		BitRateList d = new BitRateList();
		/*d.add(new Integer(512));
		d.add(new Integer(768));
		d.add(new Integer(1536));
		d.add(new Integer(1024));
		d.add(new Integer(384));
		d.add(new Integer(1280));
		d.add(new Integer(256)); */
		d.add(512);
		d.add(768);
		d.add(1536);
		d.add(1024);
		d.add(384);
		d.add(1280);
		d.add(256);

		return d;
	}
}
