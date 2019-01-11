package com.digisky.canglong.msgtest.tester.bio.tools;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.mina.core.buffer.IoBuffer;


@SuppressWarnings({"rawtypes"})
public class LuaOutputStream {
	
	public IoBuffer buff = null;

	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private LuaOutputStream(){};
	
	public final static LuaOutputStream allocate(int size) {
		LuaOutputStream bout = new LuaOutputStream();
		bout.buff = IoBuffer.allocate(size).setAutoExpand(true);
		bout.buff.order(ByteOrder.LITTLE_ENDIAN);
		return bout;
	}

    public final static LuaOutputStream allocateFixedIndexLunaOutputStream(int size) {
        LuaOutputStream bout = new IndexFixedLuaOutputStream();
        bout.buff = IoBuffer.allocate(size).setAutoExpand(true);
        bout.buff.order(ByteOrder.LITTLE_ENDIAN);
        return bout;
    }

	public final void write_nil() {
		buff.put(BIOType.TYPE_NIL);
	}

	public final void write_bool(boolean v) {
		if (v)
			buff.put(BIOType.TYPE_TRUE);
		else
			buff.put(BIOType.TYPE_FALSE);
	}

	public void write_number(Number v) throws IOException {
		buff.put(BIOType.TYPE_INT);
		BioHelper.putInt(buff, v.intValue());
	}

	

	
	public final void write_long(int v) throws IOException {
		buff.put(BIOType.TYPE_LONG);
		BioHelper.putLong(buff, v);
	}

	public final void write_cstr(String v) throws IOException {
		buff.put(BIOType.TYPE_CSTR);
		BioHelper.putCStr(buff, v);
	}

	public final void write_wstr(String v) throws IOException {
		buff.put(BIOType.TYPE_WSTR);
		BioHelper.putWStr(buff, v);
	}

	public void write_table(Map v) throws IOException {
		int len = v.size();
		buff.put(BIOType.TYPE_TABLE); // type
		buff.putInt(len); // num
		Iterator it = v.keySet().iterator();
		while (it.hasNext()) {
			Object key = it.next();
			Object var = v.get(key);
			
			write_object(key);
			write_object(var);
		}
	}
	
	public void write_table(Set v) throws IOException {
		int len = v.size();
		buff.put(BIOType.TYPE_TABLE); // type
		buff.putInt(len); // num
		Integer p = 1;
		for (Object o : v) {
			Integer k = p++;
			write_object(k);
			write_object(o);
		}
	}
	
	public void write_table(List v) throws IOException {
		int len = v.size();
		buff.put(BIOType.TYPE_TABLE); // type
		buff.putInt(len); // num
		Integer p = 1;
		for (Object o : v) {
			Integer k = p++;
			write_object(k);
			write_object(o);
		}
	}
	
	public void write_table(Map[] v) throws IOException {
		int count = v.length;
		int len = 0;
		for (int i = 0;i<count;i++) {
			if(v[i]== null)
				continue;
			len++;
		}
		buff.put(BIOType.TYPE_TABLE); // type
		buff.putInt(len); // num
		int ix = 1;
		for (int i = 0;i<count;i++) {
			if(v[i]== null)
				continue;
			Integer k = ix++;
			write_object(k);
			write_object(v[i]);
		}
	}
	
	
	public void write_table(int[] v) throws IOException {
		int count = v.length;
		int len = count;
		buff.put(BIOType.TYPE_TABLE); // type
		buff.putInt(len); // num
		int ix = 1;
		for (int i = 0;i<count;i++) {
			Integer k = ix++;
			write_object(k);
			write_object(v[i]);
		}
	}
	
	public void write_table(Object[] v) throws IOException {
		int count = v.length;
		int len = count;
		buff.put(BIOType.TYPE_TABLE); // type
		buff.putInt(len); // num
		int ix = 1;
		for (int i = 0;i<count;i++) {
			Integer k = ix++;
			write_object(k);
			write_object(v[i]);
		}
	}
	
	public void write_table(Integer[] v) throws IOException {
		int count = v.length;
		int len = 0;
		for (int i = 0;i<count;i++) {
			if(v[i]== null)
				continue;
			len++;
		}
		buff.put(BIOType.TYPE_TABLE); // type
		buff.putInt(len); // num
		int ix = 1;
		for (int i = 0;i<count;i++) {
			if(v[i]== null)
				continue;
			Integer k = ix++;
			write_object(k);
			write_object(v[i]);
		}
	}
	
	

	public void write_bytes(byte[] b){
		int len = b.length;
		buff.put(BIOType.TYPE_BYTES);
		buff.putInt(len);
		buff.put(b);
	}
	
	public final void write_object(Object o) throws IOException {
		if (o == null){
			write_nil();
		}else if (o instanceof Set){
			Set v = (Set) o;
			write_table(v);
		}else if (o instanceof Map){
			Map v = (Map) o;
			write_table(v);
		}else if (o instanceof List){
			List v = (List) o;
			write_table(v);
		}else if(o instanceof Map[]){
			Map[] v = (Map[])o;
			write_table(v);	
		}else if(o instanceof Integer[]){
			Integer[] v = (Integer[])o;
			write_table(v);
		}else if(o instanceof int[]){
			int[] v = (int[])o;
			write_table(v);
		}else if (o instanceof Integer){
			write_number((Integer) o);
		}else if(o instanceof Object[]){
			Object[] v = (Object[])o;
			write_table(v);
		}
		else if (o instanceof String){
			String s = (String) o;
			if(WStr.isWStr(s)){
				write_wstr(s);
			}else{
				write_cstr(s);
			}
		}else if (o instanceof Boolean){
			boolean v = ((Boolean) o).booleanValue();
			write_bool(v);
		}else if (o instanceof byte[]){
			byte[] v = (byte[]) o;
			write_bytes(v);
		}else if (o instanceof Date){
//			long tm = ((Date)o).getTime();
//			write_int((int)tm);
			// }else if(o instanceof Date){
			 //String s = WStr.toWChar(sdf.format(o));
			String s = sdf.format(o);
			write_wstr(s);
		}else if (o instanceof Float){
			write_number((Float) o);
		}else if (o instanceof Double){
			write_number((Double) o);
		}else if (o instanceof Long){
			write_number((Long) o);
		}else{
			throw new IOException("unsupported object:"+o + " type:" + o.getClass().getName());
		}
	}
	
	public final byte[] toByteArray() throws IOException {
		return BioHelper.toByteArray(buff);
	}


	public final void toFile(String file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		byte[] b = toByteArray();
		fos.write(b);
		fos.close();
		fos = null;
	}
	
	
	public  void free(){
		buff.free();
	}
	
	public void capacity(int newCapacity){
		buff.capacity(newCapacity);
	}


    private static class IndexFixedLuaOutputStream extends LuaOutputStream {
        @Override
        public void write_table(List v) throws IOException {
            int len = v.size();
            buff.put(BIOType.TYPE_JAVA_LIST); // type
            buff.putInt(len); // num
            Integer p = 0;
            for (Object o : v) {
                Integer k = p++;
                write_object(k);
                write_object(o);
            }
        }
        
        
        public void write_number(Number v) throws IOException {
        	if(v instanceof Long){
        		buff.put(BIOType.TYPE_LONG);
        		BioHelper.putLong(buff, v.longValue());
        	}else{
        		buff.put(BIOType.TYPE_INT);
        		BioHelper.putInt(buff, v.intValue());
        	}
    		
    	}

        

        @Override
        public void write_table(Integer[] v) throws IOException {
            int count = v.length;
            int len = 0;
            for (int i = 0;i<count;i++) {
                if(v[i]== null)
                    continue;
                len++;
            }
            buff.put(BIOType.TYPE_JAVA_INTEGER_ARRAY); // type
            buff.putInt(len); // num
            int ix = 0;
            for (int i = 0;i<count;i++) {
                if(v[i]== null)
                    continue;
                Integer k = ix++;
                write_object(k);
                write_object(v[i]);
            }
        }
    }
}
