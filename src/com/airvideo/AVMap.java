package com.airvideo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class AVMap extends LinkedHashMap {
	String name;
	//String __input;
	int __counter;
	DataInputStream __input;
	
	AVMap() {
		this.name = "";
		this.__counter = 0;
	}

	public String to_avmap(Object o, boolean resetCounter, DataOutputStream out) throws Exception {
		String mem = "";
		String kids = "";
		int kidcount = 0;
		if (resetCounter) __counter = 0;
		if (o == null) {
			//mem = "n";
			out.writeBytes("n");
		} else if (o instanceof BitRateList) {
			//mem = "e" + pack(__counter++) + pack(((BitRateList)o).size());
			out.writeBytes("e");
			out.writeInt(__counter++);
			out.writeInt(((BitRateList)o).size());
			for (Object i : (BitRateList) o) {
				//mem += to_avmap(i, false);
				to_avmap(i, false, out);
			}
		} else if (o instanceof ArrayList) {
			//mem = "a" + pack(__counter++) + pack(((ArrayList)o).size());
			out.writeBytes("a");
			out.writeInt(__counter++);
			out.writeInt(((ArrayList)o).size());

			for (Object i : (ArrayList) o) {
				//mem += to_avmap(i, false);
				to_avmap(i, false, out);
			}
		} else if (o instanceof AVMap) {
			AVMap h = (AVMap)o;
			int version = 1;
			if (h.name == "air.video.ConversionRequest") {
				version = 221;
			}
			//mem = "o" + pack(__counter++) + pack(h.name.length()) + h.name + pack(version);
			out.writeBytes("o");
			out.writeInt(__counter++);
			out.writeInt(h.name.length());
			out.writeBytes(h.name);
			out.writeInt(version);

			/*kids = "";
			String [] keys = (String[]) h.keySet().toArray();
			kidcount = keys.length;
			for (int i = 0; i < kidcount; i++)  {
				String key = keys[i];
				kids += key.length() + key + to_avmap(h.get(key), false);
			}
			*/
			
			Iterator<Object> i = h.keySet().iterator();
			kids = "";
			kidcount = 0;
			out.writeInt(h.size());
			while (i.hasNext()) {
				Object k = i.next();
				if (k instanceof String) {
					out.writeInt(((String)k).length());
					//kids += pack(((String)k).length());
					out.writeBytes((String)k);
					//kids += (String)k;
					//kids += to_avmap( h.get(k) , false);
					to_avmap(h.get(k), false, out);
					kidcount++;
				}
			}
			
//			mem +=  pack(kidcount) + kids;
		} else if (o instanceof AVBinary) {
			out.writeBytes("x");
			out.writeInt(__counter++);
			out.writeInt(((AVBinary)o).data.length());
			out.writeBytes(((AVBinary)o).data); 

			//mem = "x" + pack(__counter++) + pack(((AVBinary)o).data.length()) + ((AVBinary)o).data;
		} else if (o instanceof String) {
			out.writeBytes("s");
			out.writeInt(__counter++);
			out.writeInt(((String)o).length());
			out.writeBytes((String)o);

			//mem = "s" + pack(__counter++) + pack(((String)o).length()) + (String)o;
		} else if (o instanceof URL) {
			out.writeBytes("s");
			out.writeInt(__counter++);
			out.writeInt(((URL)o).toString().length());
			out.writeBytes(((URL)o).toString());
			
			//mem = "s" + pack(__counter++) + pack(((URL)o).toString().length()) + ((URL)o).toString();
		} else if (o instanceof Integer) {
			out.writeBytes("i");
			out.writeInt(((Integer)o).intValue());

			//mem = "i" + pack(((Integer)o).intValue());
		} else if (o instanceof Float) {
			out.writeBytes("f");
			out.writeFloat((Float)o);
			

			//mem = "f" + pack((Float)o);
		} else {
			throw new Exception("Don't know how to package this datatype");
		}
		return mem;
	}
	
	private String pack(int i) {
		long l = (long)i;
		if (i == 221) {
			l = (long)i;
		}
		byte [] bytes = new byte[4];
		bytes[0] = (byte)((l & 0xFF000000L) >> 24);
		bytes[1] = (byte)((l & 0x00FF0000L) >> 16);
		bytes[2] = (byte)((l & 0x0000FF00L) >> 8);
		bytes[3] = (byte)((l & 0x000000FFL));
		try {
			return new String(bytes, "ISO-8859-1");
			
		} catch (Exception e) {
			return "\0\0\0\0";
		}
	}
	
	private String pack(float f) {
		return pack(Float.floatToRawIntBits(f));
	}

	public static AVMap parse (InputStream i) {
		AVMap obj = new AVMap();
		obj.__input = new DataInputStream(i);
		
		AVMap result = null;
		
		try {
			result = (AVMap)obj.readIdentifier(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = null;
		}
		return result;
	}
	
	public String readString(int c_byte) throws IOException {
		byte [] b = new byte[c_byte];
		__input.read(b);
		return new String(b);
	}
	
	Object readIdentifier(int depth) throws Exception {
		byte ident;	 
		int namelength;
		int payloadlength;
		String name;
		int version;
		int childrencount;
		int unknown;
		int keylen;
		String key; 
		
		byte [] b;
		
		int counter;
		
		ident = __input.readByte();
		
		switch (ident) {
		case 'o': // hash
			AVMap map = new AVMap();
			unknown = __input.readInt();
			namelength = __input.readInt();
			map.name = readString(namelength);
			unknown = __input.readInt();
			childrencount = __input.readInt();
			for (counter = 0; counter < childrencount; counter++) {
				keylen = __input.readInt();
				key = readString(keylen);
				Object d = readIdentifier (depth + 1 );
				map.put(key, d);
			}
			return map;
		case 's': // string
			unknown = __input.readInt();
			payloadlength = __input.readInt();
			return readString(payloadlength);
		case 'i':
		case 'r': // int
			unknown = __input.readInt();
			return unknown;
		case 'a':
		case 'e': // array
			unknown = __input.readInt();
			childrencount = __input.readInt();
			ArrayList <Object> a = new ArrayList <Object> ();
			for (counter = 0; counter < childrencount; counter++) {
				a.add(readIdentifier(depth + 1));
			}
			return a;
		case 'n': // null
			return null;
		case 'f': // float
			float f = __input.readFloat();
			return f;
		case 'l': // big int
			unknown = __input.readInt();
			childrencount = __input.readInt();
			return 0;
		case 'x': // binary
			AVBinary bin = new AVBinary("");
			unknown = __input.readInt();
			childrencount = __input.readInt();
			bin.data = readString(childrencount);
			return bin;
		default:
			throw new Exception("Unknown identifier " + ident);
		}
	}
}
