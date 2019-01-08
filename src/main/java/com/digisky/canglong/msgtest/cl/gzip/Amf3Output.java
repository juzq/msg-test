package com.digisky.canglong.msgtest.cl.gzip;

import java.io.Externalizable;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.sql.RowSet;

import flex.messaging.MessageException;
import flex.messaging.io.ArrayCollection;
import flex.messaging.io.PagedRowSet;
import flex.messaging.io.PropertyProxy;
import flex.messaging.io.PropertyProxyRegistry;
import flex.messaging.io.SerializationContext;
import flex.messaging.io.SerializationDescriptor;
import flex.messaging.io.StatusInfoProxy;
import flex.messaging.io.amf.ASObject;
import flex.messaging.io.amf.Amf3Types;
import flex.messaging.io.amf.TraitsInfo;
import flex.messaging.util.Trace;
import org.w3c.dom.Document;

@SuppressWarnings({"rawtypes","unchecked"})
public class Amf3Output extends AbstractAmfOutput implements Amf3Types {
	protected IdentityHashMap objectTable;
	protected HashMap traitsTable;
	protected HashMap stringTable;

	public Amf3Output(SerializationContext context) {
		super(context);

		this.objectTable = new IdentityHashMap(64);
		this.traitsTable = new HashMap(10);
		this.stringTable = new HashMap(64);

		context.supportDatesByReference = true;
	}

	@Override
	public void reset() {
		super.reset();
		this.objectTable.clear();
		this.traitsTable.clear();
		this.stringTable.clear();
	}

	@Override
	public void writeObject(Object o) throws IOException {
		if (o == null) {
			writeAMFNull();
			return;
		}

		if (((o instanceof String)) || ((o instanceof Character))) {
			String s = o.toString();
			writeAMFString(s);
		} else if ((o instanceof Number)) {
			if (((o instanceof Integer)) || ((o instanceof Short))
					|| ((o instanceof Byte))) {
				int i = ((Number) o).intValue();
				writeAMFInt(i);
			} else if ((!this.context.legacyBigNumbers)
					&& (((o instanceof BigInteger)) || ((o instanceof BigDecimal)))) {
				writeAMFString(((Number) o).toString());
			} else {
				double d = ((Number) o).doubleValue();
				writeAMFDouble(d);
			}
		} else if ((o instanceof Boolean)) {
			writeAMFBoolean(((Boolean) o).booleanValue());
		} else if ((o instanceof Date)) {
			writeAMFDate((Date) o);
		} else if ((o instanceof Calendar)) {
			writeAMFDate(((Calendar) o).getTime());
		} else if ((o instanceof Document)) {
			if (this.context.legacyXMLDocument)
				this.out.write(7);
			else {
				this.out.write(11);
			}
			String xml = documentToString(o);
			if (this.isDebug) {
//				this.trace.write(xml);
			}
			writeAMFUTF(xml);
		} else {
			Class cls = o.getClass();

			if ((this.context.legacyMap) && ((o instanceof Map))
					&& (!(o instanceof ASObject))) {
				writeMapAsECMAArray((Map) o);
			} else if ((o instanceof Collection)) {
				if (this.context.legacyCollection)
					writeCollection((Collection) o, null);
				else
					writeArrayCollection((Collection) o, null);
			} else if (cls.isArray()) {
				writeAMFArray(o, cls.getComponentType());
			} else {
				if ((o instanceof RowSet)) {
					o = new PagedRowSet((RowSet) o, 2147483647, false);
				} else if ((this.context.legacyThrowable)
						&& ((o instanceof Throwable))) {
					o = new StatusInfoProxy((Throwable) o);
				}

				writeCustomObject(o);
			}
		}
	}

	@Override
	public void writeObjectTraits(TraitsInfo ti) throws IOException {
		String className = ti.getClassName();

//		if (this.isDebug) {
//			if (ti.isExternalizable())
////				this.trace.startExternalizableObject(className,
////						this.objectTable.size() - 1);
//			else {
//				this.trace.startAMFObject(className,
//						this.objectTable.size() - 1);
//			}
//		}
		if (!byReference(ti)) {
			int count = 0;
			List propertyNames = null;
			boolean externalizable = ti.isExternalizable();

			if (!externalizable) {
				propertyNames = ti.getProperties();
				if (propertyNames != null) {
					count = propertyNames.size();
				}
			}
			boolean dynamic = ti.isDynamic();

			writeUInt29(0x3 | (externalizable ? 4 : 0) | (dynamic ? 8 : 0)
					| count << 4);
			writeStringWithoutType(className);

			if ((!externalizable) && (propertyNames != null)) {
				for (int i = 0; i < count; i++) {
					String propName = ti.getProperty(i);
					writeStringWithoutType(propName);
				}
			}
		}
	}

	@Override
	public void writeObjectProperty(String name, Object value)
			throws IOException {
//		if (this.isDebug)
//			this.trace.namedElement(name);
		writeObject(value);
	}

	@Override
	public void writeObjectEnd() throws IOException {
//		if (this.isDebug)
//			this.trace.endAMFObject();
	}

	protected void writeAMFBoolean(boolean b) throws IOException {
//		if (this.isDebug) {
//			this.trace.write(b);
//		}
		if (b)
			this.out.write(3);
		else
			this.out.write(2);
	}

	protected void writeAMFDate(Date d) throws IOException {
		this.out.write(8);

		if (!byReference(d)) {
//			if (this.isDebug) {
//				this.trace.write(d);
//			}

			writeUInt29(1);

			this.out.writeDouble(d.getTime());
		}
	}

	protected void writeAMFDouble(double d) throws IOException {
//		if (this.isDebug) {
//			this.trace.write(d);
//		}
		this.out.write(5);
		this.out.writeDouble(d);
	}

	protected void writeAMFInt(int i) throws IOException {
		if ((i >= -268435456) && (i <= 268435455)) {
//			if (this.isDebug) {
//				this.trace.write(i);
//			}

			i &= 536870911;
			this.out.write(4);
			writeUInt29(i);
		} else {
			writeAMFDouble(i);
		}
	}

	protected void writeMapAsECMAArray(Map map) throws IOException {
		this.out.write(9);

		if (!byReference(map)) {
//			if (this.isDebug) {
//				this.trace.startECMAArray(this.objectTable.size() - 1);
//			}
			writeUInt29(1);

			Iterator it = map.keySet().iterator();
			while (it.hasNext()) {
				Object key = it.next();
				if (key != null) {
					String propName = String.valueOf(key);//.toString();
					writeStringWithoutType(propName+"");
//					if (this.isDebug) {
//						this.trace.namedElement(propName);
//					}
					writeObject(map.get(key));
				}
			}

			writeStringWithoutType("");

//			if (this.isDebug)
//				this.trace.endAMFArray();
		}
	}

	protected void writeAMFNull() throws IOException {
//		if (this.isDebug) {
//			this.trace.writeNull();
//		}
		this.out.write(1);
	}

	protected void writeAMFString(String s) throws IOException {
		this.out.write(6);
		writeStringWithoutType(s);

//		if (this.isDebug) {
//			this.trace.writeString(s);
//		}
	}

	protected void writeStringWithoutType(String s) throws IOException {
		if (s.length() == 0) {
			writeUInt29(1);
			return;
		}

		if (!byReference(s)) {
			writeAMFUTF(s);
			return;
		}
	}

	protected void writeAMFArray(Object o, Class componentType)
			throws IOException {
		if (componentType.isPrimitive()) {
			writePrimitiveArray(o);
		} else if (componentType.equals(Byte.class)) {
			writeAMFByteArray((Byte[]) o);
		} else if (componentType.equals(Character.class)) {
			writeCharArrayAsString((Character[]) o);
		} else {
			writeObjectArray((Object[]) o, null);
		}
	}

	protected void writeArrayCollection(Collection col,
                                        SerializationDescriptor desc) throws IOException {
		this.out.write(10);

		if (!byReference(col)) {
			ArrayCollection ac;
			if ((col instanceof ArrayCollection)) {
				ac = (ArrayCollection) col;
			} else {
				ac = new ArrayCollection(col);
				if (desc != null) {
					ac.setDescriptor(desc);
				}
			}

			PropertyProxy proxy = PropertyProxyRegistry.getProxy(ac);
			writePropertyProxy(proxy, ac);
		}
	}

	protected void writeCustomObject(Object o) throws IOException {
		PropertyProxy proxy = null;

		if ((o instanceof PropertyProxy)) {
			proxy = (PropertyProxy) o;
			o = proxy.getDefaultInstance();

			if (o == null) {
				writeAMFNull();
				return;
			}

			if ((o instanceof Collection)) {
				if (this.context.legacyCollection)
					writeCollection((Collection) o, proxy.getDescriptor());
				else
					writeArrayCollection((Collection) o, proxy.getDescriptor());
				return;
			}
			if (o.getClass().isArray()) {
				writeObjectArray((Object[]) o, proxy.getDescriptor());
				return;
			}
			if ((this.context.legacyMap) && ((o instanceof Map))
					&& (!(o instanceof ASObject))) {
				writeMapAsECMAArray((Map) o);
				return;
			}
		}

		this.out.write(10);

		if (!byReference(o)) {
			if (proxy == null) {
				proxy = PropertyProxyRegistry.getProxyAndRegister(o);
			}

			writePropertyProxy(proxy, o);
		}
	}

	protected void writePropertyProxy(PropertyProxy proxy, Object instance)
			throws IOException {

		List propertyNames = null;
		boolean externalizable = proxy.isExternalizable(instance);

		if (!externalizable) {
			propertyNames = proxy.getPropertyNames(instance);
		}
		TraitsInfo ti = new TraitsInfo(proxy.getAlias(instance), proxy
				.isDynamic(), externalizable, propertyNames);
		writeObjectTraits(ti);

		if (externalizable) {
			((Externalizable) instance).writeExternal(this);
		} else if (propertyNames != null) {
			Iterator it = propertyNames.iterator();
			while (it.hasNext()) {
				Object propName = it.next();
//				String propName = (String) it.next();
				Object value = null;
				if(propName.toString().matches("\\d*")){
					value = ((Map)instance).get(Integer.parseInt(propName.toString()));
					if(value == null){
						value = proxy.getValue(instance, (String)propName);
					}
				}else
					value = proxy.getValue(instance, (String)propName);
				writeObjectProperty((String)propName, value);
			}
		}

		writeObjectEnd();
	}

	protected void writePrimitiveArray(Object obj) throws IOException {
		Class aType = obj.getClass().getComponentType();

		if (aType.equals(Character.TYPE)) {
			char[] c = (char[]) obj;
			writeCharArrayAsString(c);
		} else if (aType.equals(Byte.TYPE)) {
			writeAMFByteArray((byte[]) obj);
		} else {
			this.out.write(9);

			if (!byReference(obj)) {
				if (aType.equals(Boolean.TYPE)) {
					boolean[] b = (boolean[]) obj;

					writeUInt29(b.length << 1 | 0x1);

					writeStringWithoutType("");

					if (this.isDebug) {
//						this.trace.startAMFArray(this.objectTable.size() - 1);

						for (int i = 0; i < b.length; i++) {
//							this.trace.arrayElement(i);
							writeAMFBoolean(b[i]);
						}

//						this.trace.endAMFArray();
					} else {
						for (int i = 0; i < b.length; i++) {
							writeAMFBoolean(b[i]);
						}
					}
				} else if ((aType.equals(Integer.TYPE))
						|| (aType.equals(Short.TYPE))) {
					int length = Array.getLength(obj);

					writeUInt29(length << 1 | 0x1);

					writeStringWithoutType("");

					if (this.isDebug) {
//						this.trace.startAMFArray(this.objectTable.size() - 1);

						for (int i = 0; i < length; i++) {
//							this.trace.arrayElement(i);
							int v = Array.getInt(obj, i);
							writeAMFInt(v);
						}

//						this.trace.endAMFArray();
					} else {
						for (int i = 0; i < length; i++) {
							int v = Array.getInt(obj, i);
							writeAMFInt(v);
						}

					}

				} else {
					int length = Array.getLength(obj);

					writeUInt29(length << 1 | 0x1);

					writeStringWithoutType("");

					if (this.isDebug) {
//						this.trace.startAMFArray(this.objectTable.size() - 1);

						for (int i = 0; i < length; i++) {
//							this.trace.arrayElement(i);
							double v = Array.getDouble(obj, i);
							writeAMFDouble(v);
						}

//						this.trace.endAMFArray();
					} else {
						for (int i = 0; i < length; i++) {
							double v = Array.getDouble(obj, i);
							writeAMFDouble(v);
						}
					}
				}
			}
		}
	}

	protected void writeAMFByteArray(byte[] ba) throws IOException {
		this.out.write(12);

		if (!byReference(ba)) {
			int length = ba.length;

			writeUInt29(length << 1 | 0x1);

//			if (this.isDebug) {
//				this.trace.startByteArray(this.objectTable.size() - 1, length);
//			}

			this.out.write(ba, 0, length);
		}
	}

	protected void writeAMFByteArray(Byte[] ba) throws IOException {
		this.out.write(12);

		if (!byReference(ba)) {
			int length = ba.length;

			writeUInt29(length << 1 | 0x1);

//			if (this.isDebug) {
//				this.trace.startByteArray(this.objectTable.size() - 1, length);
//			}

			for (int i = 0; i < ba.length; i++) {
				Byte b = ba[i];
				if (b == null)
					this.out.write(0);
				else
					this.out.write(b.byteValue());
			}
		}
	}

	protected void writeCharArrayAsString(Character[] ca) throws IOException {
		int length = ca.length;
		char[] chars = new char[length];

		for (int i = 0; i < length; i++) {
			Character c = ca[i];
			if (c == null)
				chars[i] = '\000';
			else
				chars[i] = ca[i].charValue();
		}
		writeCharArrayAsString(chars);
	}

	protected void writeCharArrayAsString(char[] ca) throws IOException {
		String str = new String(ca);
		writeAMFString(str);
	}

	protected void writeObjectArray(Object[] values,
                                    SerializationDescriptor descriptor) throws IOException {
		this.out.write(9);

		if (!byReference(values)) {
//			if (this.isDebug) {
//				this.trace.startAMFArray(this.objectTable.size() - 1);
//			}
			writeUInt29(values.length << 1 | 0x1);

			writeStringWithoutType("");

			for (int i = 0; i < values.length; i++) {
//				if (this.isDebug) {
//					this.trace.arrayElement(i);
//				}
				Object item = values[i];
				if ((item != null) && (descriptor != null)
						&& (!(item instanceof String))
						&& (!(item instanceof Number))
						&& (!(item instanceof Boolean))
						&& (!(item instanceof Character))) {
					PropertyProxy proxy = PropertyProxyRegistry.getProxy(item);
					proxy = (PropertyProxy) proxy.clone();
					proxy.setDescriptor(descriptor);
					proxy.setDefaultInstance(item);
					item = proxy;
				}
				writeObject(item);
			}

//			if (this.isDebug)
//				this.trace.endAMFArray();
		}
	}

	protected void writeCollection(Collection c,
                                   SerializationDescriptor descriptor) throws IOException {
		this.out.write(9);

		if (!byReference(c)) {
//			if (this.isDebug) {
//				this.trace.startAMFArray(this.objectTable.size() - 1);
//			}
			writeUInt29(c.size() << 1 | 0x1);

			writeStringWithoutType("");

			Iterator it = c.iterator();
			int i = 0;
			while (it.hasNext()) {
//				if (this.isDebug) {
//					this.trace.arrayElement(i);
//				}
				Object item = it.next();

				if ((item != null) && (descriptor != null)
						&& (!(item instanceof String))
						&& (!(item instanceof Number))
						&& (!(item instanceof Boolean))
						&& (!(item instanceof Character))) {
					PropertyProxy proxy = PropertyProxyRegistry.getProxy(item);
					proxy = (PropertyProxy) proxy.clone();
					proxy.setDescriptor(descriptor);
					proxy.setDefaultInstance(item);
					item = proxy;
				}
				writeObject(item);

				i++;
			}

//			if (this.isDebug)
//				this.trace.endAMFArray();
		}
	}

	protected void writeUInt29(int ref) throws IOException {
		if (ref < 128) {
			this.out.writeByte(ref);
		} else if (ref < 16384) {
			this.out.writeByte(ref >> 7 & 0x7F | 0x80);
			this.out.writeByte(ref & 0x7F);
		} else if (ref < 2097152) {
			this.out.writeByte(ref >> 14 & 0x7F | 0x80);
			this.out.writeByte(ref >> 7 & 0x7F | 0x80);
			this.out.writeByte(ref & 0x7F);
		} else if (ref < 1073741824) {
			this.out.writeByte(ref >> 22 & 0x7F | 0x80);
			this.out.writeByte(ref >> 15 & 0x7F | 0x80);
			this.out.writeByte(ref >> 8 & 0x7F | 0x80);
			this.out.writeByte(ref & 0xFF);
		} else {
//			throw new MessageException("Integer out of range: " + ref);
		}
	}

	public void writeAMFUTF(String s) throws IOException {
		int strlen = s.length();
		int utflen = 0;
		int count = 0;

		char[] charr = getTempCharArray(strlen);
		s.getChars(0, strlen, charr, 0);

		for (int i = 0; i < strlen; i++) {
			int c = charr[i];
			if (c <= 127) {
				utflen++;
			} else if (c > 2047) {
				utflen += 3;
			} else {
				utflen += 2;
			}
		}

		writeUInt29(utflen << 1 | 0x1);

		byte[] bytearr = getTempByteArray(utflen);

		for (int i = 0; i < strlen; i++) {
			int c = charr[i];
			if (c <= 127) {
				bytearr[(count++)] = (byte) c;
			} else if (c > 2047) {
				bytearr[(count++)] = (byte) (0xE0 | c >> 12 & 0xF);
				bytearr[(count++)] = (byte) (0x80 | c >> 6 & 0x3F);
				bytearr[(count++)] = (byte) (0x80 | c >> 0 & 0x3F);
			} else {
				bytearr[(count++)] = (byte) (0xC0 | c >> 6 & 0x1F);
				bytearr[(count++)] = (byte) (0x80 | c >> 0 & 0x3F);
			}
		}
		this.out.write(bytearr, 0, utflen);
	}

	protected boolean byReference(Object o) throws IOException {
		Object ref = this.objectTable.get(o);

		if (ref != null) {
			try {
				int refNum = ((Integer) ref).intValue();

//				if (this.isDebug) {
//					this.trace.writeRef(refNum);
//				}
				writeUInt29(refNum << 1);
			} catch (ClassCastException e) {
				throw new IOException("Object reference is not an Integer");
			}
		} else {
			this.objectTable.put(o, new Integer(this.objectTable.size()));
		}

		return ref != null;
	}

	public void addObjectReference(Object o) throws IOException {
		byReference(o);
	}

	protected boolean byReference(String s) throws IOException {
		Object ref = this.stringTable.get(s);

		if (ref != null) {
			try {
				int refNum = ((Integer) ref).intValue();

				writeUInt29(refNum << 1);

//				if ((Trace.amf) && (this.isDebug)) {
//					this.trace.writeStringRef(refNum);
//				}
			} catch (ClassCastException e) {
				throw new IOException("String reference is not an Integer");
			}
		} else {
			this.stringTable.put(s, new Integer(this.stringTable.size()));
		}

		return ref != null;
	}

	protected boolean byReference(TraitsInfo ti) throws IOException {
		Object ref = this.traitsTable.get(ti);

		if (ref != null) {
			try {
				int refNum = ((Integer) ref).intValue();

				writeUInt29(refNum << 2 | 0x1);

//				if ((Trace.amf) && (this.isDebug)) {
//					this.trace.writeTraitsInfoRef(refNum);
//				}
			} catch (ClassCastException e) {
				throw new IOException("TraitsInfo reference is not an Integer");
			}
		} else {
			this.traitsTable.put(ti, new Integer(this.traitsTable.size()));
		}

		return ref != null;
	}
	

	
}