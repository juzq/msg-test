package com.digisky.canglong.msgtest.cl.gzip;

import flex.messaging.io.amf.ASObject;

public class Packet {
	//命令编号
	private int command;
	//非AS数据
	private Object object;
	//AS数据
	private ASObject asObject;
	//0:携带错误信息的数据包. 1:正常数据包
	private int type=1;

	public Packet() {
		this(0);
	}

	public Packet(int command) {
		this(command, null);
	}

	public Packet(int command, Object obj) {
		this.command = command;
		if (obj instanceof ASObject) {
			this.asObject = (ASObject) obj;
		} else {
			this.object = obj;
		}
	}

	public Object get() {
		return object == null ? asObject : object;
	}

	public Object get(Object key) {
		if (asObject != null) {
			return asObject.get(key);
		}
		return null;
	}
	
	public int getCommand() {
		return command;
	}

	public void setCommand(int command) {
		this.command = command;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}
	
	public ASObject getASObject(){
		return asObject;
	}
	
	public int getType() {
		return type;
	}
	/**
	 * 0:携带错误信息的数据包. 1:正常数据包
	 * @param type
	 */
	public void setType(int type) {
		this.type = type;
	}

	@SuppressWarnings("unchecked")
	/**
	 * JUint临时使用
	 */
	public void setData(Object key, Object value){
		if(asObject==null)asObject=new ASObject();
		asObject.put(key, value);
	}
}
