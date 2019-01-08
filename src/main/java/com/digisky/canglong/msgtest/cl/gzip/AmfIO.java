package com.digisky.canglong.msgtest.cl.gzip;

import flex.messaging.io.SerializationContext;
import flex.messaging.io.TypeMarshallingContext;
import flex.messaging.io.amf.AmfTrace;

abstract class AmfIO {
	protected final SerializationContext context;
	protected boolean isDebug;
	protected AmfTrace trace;
	private char[] tempCharArray = null;
	private byte[] tempByteArray = null;

	AmfIO(SerializationContext context) {
		this.context = context;
	}

	public void setDebugTrace(AmfTrace trace) {
		this.trace = trace;
//		this.isDebug = (this.trace != null);
	}

	public void reset() {
		TypeMarshallingContext marshallingContext = TypeMarshallingContext
				.getTypeMarshallingContext();
		marshallingContext.reset();
	}

	final char[] getTempCharArray(int capacity) {
		char[] result = this.tempCharArray;
		if ((result == null) || (result.length < capacity)) {
			result = new char[capacity * 2];
			this.tempCharArray = result;
		}
		return result;
	}

	final byte[] getTempByteArray(int capacity) {
		byte[] result = this.tempByteArray;
		if ((result == null) || (result.length < capacity)) {
			result = new byte[capacity * 2];
			this.tempByteArray = result;
		}
		return result;
	}
}