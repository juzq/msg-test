package com.digisky.canglong.msgtest.cl.gzip;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import flex.messaging.io.SerializationContext;
import flex.messaging.io.amf.ActionMessageOutput;
import flex.messaging.util.XMLUtil;
import org.w3c.dom.Document;

public abstract class AbstractAmfOutput extends AmfIO implements
        ActionMessageOutput {
	protected DataOutputStream out;

	public AbstractAmfOutput(SerializationContext context) {
		super(context);
	}

	@Override
	public void setOutputStream(OutputStream out) {
		if ((out instanceof DataOutputStream)) {
			this.out = ((DataOutputStream) out);
		} else {
			this.out = new DataOutputStream(out);
		}
		reset();
	}

	protected String documentToString(Object value) throws IOException {
		return XMLUtil.documentToString((Document) value);
	}

	@Override
	public void close() throws IOException {
		this.out.close();
	}

	@Override
	public void flush() throws IOException {
		this.out.flush();
	}

	@Override
	public void write(int b) throws IOException {
		this.out.write(b);
	}

	@Override
	public void write(byte[] bytes) throws IOException {
		this.out.write(bytes);
	}

	@Override
	public void write(byte[] bytes, int offset, int length) throws IOException {
		this.out.write(bytes, offset, length);
	}

	@Override
	public void writeBoolean(boolean v) throws IOException {
		this.out.writeBoolean(v);
	}

	@Override
	public void writeByte(int v) throws IOException {
		this.out.writeByte(v);
	}

	@Override
	public void writeBytes(String s) throws IOException {
		this.out.writeBytes(s);
	}

	@Override
	public void writeChar(int v) throws IOException {
		this.out.writeChar(v);
	}

	@Override
	public void writeChars(String s) throws IOException {
		this.out.writeChars(s);
	}

	@Override
	public void writeDouble(double v) throws IOException {
		this.out.writeDouble(v);
	}

	@Override
	public void writeFloat(float v) throws IOException {
		this.out.writeFloat(v);
	}

	@Override
	public void writeInt(int v) throws IOException {
		this.out.writeInt(v);
	}

	@Override
	public void writeLong(long v) throws IOException {
		this.out.writeLong(v);
	}

	@Override
	public void writeShort(int v) throws IOException {
		this.out.writeShort(v);
	}

	@Override
	public void writeUTF(String s) throws IOException {
		this.out.writeUTF(s);
	}
}