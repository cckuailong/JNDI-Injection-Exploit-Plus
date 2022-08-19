//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package weblogic.jms.common;

import weblogic.jms.JMSClientExceptionLogger;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageEOFException;
import javax.jms.MessageNotWriteableException;
import javax.jms.*;
import java.io.*;

public final class StreamMessageImpl extends MessageImpl implements StreamMessage, Externalizable {
    static final long serialVersionUID = 7748687583664395357L;
    private static final byte EXTVERSION1 = 1;
    private static final byte EXTVERSION2 = 2;
    private static final byte EXTVERSION3 = 3;
    private static final byte VERSIONMASK = 127;
    private static final byte UNKNOWN_TYPECODE = 0;
    private static final byte BOOLEAN_TYPE = 1;
    private static final byte BYTE_TYPE = 2;
    private static final byte CHAR_TYPE = 3;
    private static final byte DOUBLE_TYPE = 4;
    private static final byte FLOAT_TYPE = 5;
    private static final byte INT_TYPE = 6;
    private static final byte LONG_TYPE = 7;
    private static final byte SHORT_TYPE = 8;
    private static final byte STRING_UTF_TYPE = 9;
    private static final byte STRING_UTF32_TYPE = 10;
    private static final byte BYTES_TYPE = 11;
    private static final byte NULL_TYPE = 12;
    private static final String[] TYPE_CODE_STRINGS = new String[]{"invalid type code", "boolean", "byte", "char", "double", "float", "integer", "long", "short", "String", "String", "byte array", "null object"};
    private static final String ERROR_MSG_SEGMENT = ". Previous attempt to read bytes from the stream message is not complete. As per the JMS standard, if the readBytes method does not return the value -1, a subsequent readBytes call must be made in order to ensure that there are no more bytes left to be read in. For more information, see the JMS API doc for the method readBytes in interface StreamMessage";
    private boolean readingByteArray;
    private int available_bytes;
    private transient byte[] buffer;
    private transient int length;
    private transient boolean copyOnWrite;
    private transient BufferDataOutputStream bdos;
    private transient BufferDataInputStream bdis;

    public StreamMessageImpl() {
    }

    public StreamMessageImpl(StreamMessage var1) throws IOException, JMSException {
        this(var1, (Destination) null, (Destination) null);
    }

    public StreamMessageImpl(StreamMessage var1, Destination var2, Destination var3) throws IOException, JMSException {
        super(var1, var2, var3);
        if (!(var1 instanceof StreamMessageImpl)) {
            var1.reset();
        }

        try {
            while (true) {
                this.writeObject(var1.readObject());
            }
        } catch (MessageEOFException var5) {
            this.reset();
            this.setPropertiesWritable(false);
        }
    }

    public byte getType() {
        return 5;
    }

    public void nullBody() {
        this.length = 0;
        this.buffer = null;
        this.copyOnWrite = false;
        this.bdis = null;
        this.bdos = null;
        this.readingByteArray = false;
        this.available_bytes = 0;
    }

    private void putTypeBack() {
        if (!this.readingByteArray) {
            this.bdis.unput();
        }

    }

    private String readPastEnd() {
        return JMSClientExceptionLogger.logReadPastEndLoggable().getMessage();
    }

    private String streamReadError() {
        return JMSClientExceptionLogger.logStreamReadErrorLoggable().getMessage();
    }

    private String streamWriteError() {
        return JMSClientExceptionLogger.logStreamWriteErrorLoggable().getMessage();
    }

    private String streamConversionError(String var1, String var2) {
        return JMSClientExceptionLogger.logConversionErrorLoggable(var1, var2).getMessage();
    }

    private byte readType() throws JMSException {
        this.decompressMessageBody();
        this.checkReadable();
        if (this.readingByteArray) {
            return 11;
        } else {
            try {
                return this.bdis.readByte();
            } catch (EOFException var2) {
                throw new weblogic.jms.common.MessageEOFException(this.readPastEnd(), var2);
            } catch (IOException var3) {
                throw new weblogic.jms.common.JMSException(this.streamReadError(), var3);
            }
        }
    }

    private void writeType(byte var1) throws JMSException {
        this.checkWritable();

        try {
            this.bdos.writeByte(var1);
        } catch (IOException var3) {
            throw new weblogic.jms.common.JMSException(JMSClientExceptionLogger.logStreamWriteErrorLoggable().getMessage(), var3);
        }
    }

    public boolean readBoolean() throws JMSException {
        byte var1 = this.readType();

        try {
            switch (var1) {
                case 1:
                    return this.bdis.readBoolean();
                case 9:
                case 10:
                    return Boolean.valueOf(this.readStringInternal(var1)).booleanValue();
                default:
                    this.putTypeBack();
                    String var2 = "";
                    if (this.readingByteArray) {
                        var2 = ". Previous attempt to read bytes from the stream message is not complete. As per the JMS standard, if the readBytes method does not return the value -1, a subsequent readBytes call must be made in order to ensure that there are no more bytes left to be read in. For more information, see the JMS API doc for the method readBytes in interface StreamMessage";
                    }

                    throw new MessageFormatException(this.streamConversionError(this.typeCodeToString(var1), this.typeCodeToString(1)) + var2);
            }
        } catch (EOFException var3) {
            throw new weblogic.jms.common.MessageEOFException(this.readPastEnd(), var3);
        } catch (IOException var4) {
            throw new weblogic.jms.common.JMSException(this.streamReadError(), var4);
        }
    }

    public byte readByte() throws JMSException {
        byte var1 = this.readType();

        try {
            switch (var1) {
                case 2:
                    return this.bdis.readByte();
                case 9:
                case 10:
                    this.bdis.mark();
                    return Byte.parseByte(this.readStringInternal(var1));
                default:
                    this.putTypeBack();
                    String var2 = "";
                    if (this.readingByteArray) {
                        var2 = ". Previous attempt to read bytes from the stream message is not complete. As per the JMS standard, if the readBytes method does not return the value -1, a subsequent readBytes call must be made in order to ensure that there are no more bytes left to be read in. For more information, see the JMS API doc for the method readBytes in interface StreamMessage";
                    }

                    throw new MessageFormatException(this.streamConversionError(this.typeCodeToString(var1), this.typeCodeToString(2)) + var2);
            }
        } catch (EOFException var3) {
            throw new weblogic.jms.common.MessageEOFException(this.readPastEnd(), var3);
        } catch (IOException var4) {
            throw new weblogic.jms.common.JMSException(this.streamReadError(), var4);
        } catch (NumberFormatException var5) {
            this.bdis.backToMark();
            this.bdis.unput();
            throw var5;
        }
    }

    public short readShort() throws JMSException {
        byte var1 = this.readType();

        try {
            switch (var1) {
                case 2:
                    return (short) this.bdis.readByte();
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                default:
                    this.putTypeBack();
                    String var2 = "";
                    if (this.readingByteArray) {
                        var2 = ". Previous attempt to read bytes from the stream message is not complete. As per the JMS standard, if the readBytes method does not return the value -1, a subsequent readBytes call must be made in order to ensure that there are no more bytes left to be read in. For more information, see the JMS API doc for the method readBytes in interface StreamMessage";
                    }

                    throw new MessageFormatException(this.streamConversionError(this.typeCodeToString(var1), this.typeCodeToString(8)) + var2);
                case 8:
                    return this.bdis.readShort();
                case 9:
                case 10:
                    this.bdis.mark();
                    return Short.parseShort(this.readStringInternal(var1));
            }
        } catch (EOFException var3) {
            throw new weblogic.jms.common.MessageEOFException(this.readPastEnd(), var3);
        } catch (IOException var4) {
            throw new weblogic.jms.common.JMSException(this.streamReadError(), var4);
        } catch (NumberFormatException var5) {
            this.bdis.backToMark();
            this.bdis.unput();
            throw var5;
        }
    }

    public char readChar() throws JMSException {
        byte var1 = this.readType();

        try {
            switch (var1) {
                case 3:
                    return this.bdis.readChar();
                case 12:
                    this.putTypeBack();
                    throw new NullPointerException();
                default:
                    this.putTypeBack();
                    String var2 = "";
                    if (this.readingByteArray) {
                        var2 = ". Previous attempt to read bytes from the stream message is not complete. As per the JMS standard, if the readBytes method does not return the value -1, a subsequent readBytes call must be made in order to ensure that there are no more bytes left to be read in. For more information, see the JMS API doc for the method readBytes in interface StreamMessage";
                    }

                    throw new MessageFormatException(this.streamConversionError(this.typeCodeToString(var1), this.typeCodeToString(3)) + var2);
            }
        } catch (EOFException var3) {
            throw new weblogic.jms.common.MessageEOFException(this.readPastEnd(), var3);
        } catch (IOException var4) {
            throw new weblogic.jms.common.JMSException(this.streamReadError(), var4);
        }
    }

    public int readInt() throws JMSException {
        byte var1 = this.readType();

        try {
            switch (var1) {
                case 2:
                    return this.bdis.readByte();
                case 3:
                case 4:
                case 5:
                case 7:
                default:
                    this.putTypeBack();
                    String var2 = "";
                    if (this.readingByteArray) {
                        var2 = ". Previous attempt to read bytes from the stream message is not complete. As per the JMS standard, if the readBytes method does not return the value -1, a subsequent readBytes call must be made in order to ensure that there are no more bytes left to be read in. For more information, see the JMS API doc for the method readBytes in interface StreamMessage";
                    }

                    throw new MessageFormatException(this.streamConversionError(this.typeCodeToString(var1), this.typeCodeToString(6)) + var2);
                case 6:
                    return this.bdis.readInt();
                case 8:
                    return this.bdis.readShort();
                case 9:
                case 10:
                    this.bdis.mark();
                    return Integer.parseInt(this.readStringInternal(var1));
            }
        } catch (EOFException var3) {
            throw new weblogic.jms.common.MessageEOFException(this.readPastEnd(), var3);
        } catch (IOException var4) {
            throw new weblogic.jms.common.JMSException(this.streamReadError(), var4);
        } catch (NumberFormatException var5) {
            this.bdis.backToMark();
            this.bdis.unput();
            throw var5;
        }
    }

    public long readLong() throws JMSException {
        byte var1 = this.readType();

        try {
            switch (var1) {
                case 2:
                    return (long) this.bdis.readByte();
                case 3:
                case 4:
                case 5:
                default:
                    this.putTypeBack();
                    String var2 = "";
                    if (this.readingByteArray) {
                        var2 = ". Previous attempt to read bytes from the stream message is not complete. As per the JMS standard, if the readBytes method does not return the value -1, a subsequent readBytes call must be made in order to ensure that there are no more bytes left to be read in. For more information, see the JMS API doc for the method readBytes in interface StreamMessage";
                    }

                    throw new MessageFormatException(this.streamConversionError(this.typeCodeToString(var1), this.typeCodeToString(7)) + var2);
                case 6:
                    return (long) this.bdis.readInt();
                case 7:
                    return this.bdis.readLong();
                case 8:
                    return (long) this.bdis.readShort();
                case 9:
                case 10:
                    this.bdis.mark();
                    return Long.parseLong(this.readStringInternal(var1));
            }
        } catch (EOFException var3) {
            throw new weblogic.jms.common.MessageEOFException(this.readPastEnd(), var3);
        } catch (IOException var4) {
            throw new weblogic.jms.common.JMSException(this.streamReadError(), var4);
        } catch (NumberFormatException var5) {
            this.bdis.backToMark();
            this.bdis.unput();
            throw var5;
        }
    }

    public float readFloat() throws JMSException {
        byte var1 = this.readType();

        try {
            switch (var1) {
                case 5:
                    return this.bdis.readFloat();
                case 9:
                case 10:
                    this.bdis.mark();
                    return Float.parseFloat(this.readStringInternal(var1));
                default:
                    this.putTypeBack();
                    String var2 = "";
                    if (this.readingByteArray) {
                        var2 = ". Previous attempt to read bytes from the stream message is not complete. As per the JMS standard, if the readBytes method does not return the value -1, a subsequent readBytes call must be made in order to ensure that there are no more bytes left to be read in. For more information, see the JMS API doc for the method readBytes in interface StreamMessage";
                    }

                    throw new MessageFormatException(this.streamConversionError(this.typeCodeToString(var1), this.typeCodeToString(5)) + var2);
            }
        } catch (EOFException var3) {
            throw new weblogic.jms.common.MessageEOFException(this.readPastEnd(), var3);
        } catch (IOException var4) {
            throw new weblogic.jms.common.JMSException(this.streamReadError(), var4);
        } catch (NumberFormatException var5) {
            this.bdis.backToMark();
            this.bdis.unput();
            throw var5;
        }
    }

    public double readDouble() throws JMSException {
        byte var1 = this.readType();

        try {
            switch (var1) {
                case 4:
                    return this.bdis.readDouble();
                case 5:
                    return (double) this.bdis.readFloat();
                case 6:
                case 7:
                case 8:
                default:
                    this.putTypeBack();
                    String var2 = "";
                    if (this.readingByteArray) {
                        var2 = ". Previous attempt to read bytes from the stream message is not complete. As per the JMS standard, if the readBytes method does not return the value -1, a subsequent readBytes call must be made in order to ensure that there are no more bytes left to be read in. For more information, see the JMS API doc for the method readBytes in interface StreamMessage";
                    }

                    throw new MessageFormatException(this.streamConversionError(this.typeCodeToString(var1), this.typeCodeToString(4)) + var2);
                case 9:
                case 10:
                    this.bdis.mark();
                    return Double.parseDouble(this.readStringInternal(var1));
            }
        } catch (EOFException var3) {
            throw new weblogic.jms.common.MessageEOFException(this.readPastEnd(), var3);
        } catch (IOException var4) {
            throw new weblogic.jms.common.JMSException(this.streamReadError(), var4);
        } catch (NumberFormatException var5) {
            this.bdis.backToMark();
            this.bdis.unput();
            throw var5;
        }
    }

    public String readString() throws JMSException {
        byte var1 = this.readType();

        try {
            switch (var1) {
                case 1:
                    return String.valueOf(this.bdis.readBoolean());
                case 2:
                    return String.valueOf(this.bdis.readByte());
                case 3:
                    return String.valueOf(this.bdis.readChar());
                case 4:
                    return String.valueOf(this.bdis.readDouble());
                case 5:
                    return String.valueOf(this.bdis.readFloat());
                case 6:
                    return String.valueOf(this.bdis.readInt());
                case 7:
                    return String.valueOf(this.bdis.readLong());
                case 8:
                    return String.valueOf(this.bdis.readShort());
                case 9:
                    return this.readStringInternal(var1);
                case 10:
                    return this.readStringInternal(var1);
                case 11:
                default:
                    this.putTypeBack();
                    String var2 = "";
                    if (this.readingByteArray) {
                        var2 = ". Previous attempt to read bytes from the stream message is not complete. As per the JMS standard, if the readBytes method does not return the value -1, a subsequent readBytes call must be made in order to ensure that there are no more bytes left to be read in. For more information, see the JMS API doc for the method readBytes in interface StreamMessage";
                    }

                    throw new MessageFormatException(this.streamConversionError(this.typeCodeToString(var1), this.typeCodeToString(9)) + var2);
                case 12:
                    return null;
            }
        } catch (EOFException var3) {
            throw new weblogic.jms.common.MessageEOFException(this.readPastEnd(), var3);
        } catch (IOException var4) {
            throw new weblogic.jms.common.JMSException(this.streamReadError(), var4);
        }
    }

    public int readBytes(byte[] var1) throws JMSException {
        boolean var3 = true;
        if (var1 == null) {
            throw new NullPointerException();
        } else {
            try {
                if (!this.readingByteArray) {
                    byte var2;
                    if ((var2 = this.readType()) != 11) {
                        if (var2 == 12) {
                            return -1;
                        }

                        this.bdis.unput();
                        throw new MessageFormatException(this.streamConversionError(this.typeCodeToString(var2), this.typeCodeToString(11)));
                    }

                    this.available_bytes = this.bdis.readInt();
                    if (this.available_bytes == 0) {
                        return 0;
                    }

                    this.readingByteArray = true;
                }

                if (this.available_bytes == 0) {
                    this.readingByteArray = false;
                    return -1;
                } else {
                    int var9;
                    if (var1.length > this.available_bytes) {
                        var9 = this.bdis.read(var1, 0, this.available_bytes);
                        this.readingByteArray = false;
                    } else {
                        var9 = this.bdis.read(var1, 0, var1.length);
                        this.available_bytes -= var1.length;
                    }

                    return var9;
                }
            } catch (EOFException var5) {
                throw new weblogic.jms.common.MessageEOFException(this.readPastEnd(), var5);
            } catch (IOException var6) {
                throw new weblogic.jms.common.JMSException(this.streamReadError(), var6);
            } catch (ArrayIndexOutOfBoundsException var7) {
                throw new weblogic.jms.common.JMSException(JMSClientExceptionLogger.logStreamReadErrorIndexLoggable().getMessage(), var7);
            } catch (ArrayStoreException var8) {
                throw new weblogic.jms.common.JMSException(JMSClientExceptionLogger.logStreamReadErrorStoreLoggable().getMessage(), var8);
            }
        }
    }

    public Object readObject() throws JMSException {
        byte var1 = this.readType();

        try {
            switch (var1) {
                case 1:
                    return new Boolean(this.bdis.readBoolean());
                case 2:
                    return new Byte(this.bdis.readByte());
                case 3:
                    return new Character(this.bdis.readChar());
                case 4:
                    return new Double(this.bdis.readDouble());
                case 5:
                    return new Float(this.bdis.readFloat());
                case 6:
                    return new Integer(this.bdis.readInt());
                case 7:
                    return new Long(this.bdis.readLong());
                case 8:
                    return new Short(this.bdis.readShort());
                case 9:
                    return this.readStringInternal(var1);
                case 10:
                    return this.readStringInternal(var1);
                case 11:
                    if (this.readingByteArray) {
                        throw new MessageFormatException("Can not read next data. Previous attempt to read bytes from the stream message is not complete. As per the JMS standard, if the readBytes method does not return the value -1, a subsequent readBytes call must be made in order to ensure that there are no more bytes left to be read in. For more information, see the JMS API doc for the method readBytes in interface StreamMessage");
                    } else {
                        int var2 = this.bdis.readInt();
                        byte[] var3 = new byte[var2];
                        int var4 = this.bdis.read(var3, 0, var2);
                        if (var4 != var2) {
                            throw new EOFException("");
                        }

                        return var3;
                    }
                case 12:
                    return null;
                default:
                    this.bdis.unput();
                    throw new MessageFormatException(this.streamConversionError(this.typeCodeToString(var1), "Object"));
            }
        } catch (EOFException var5) {
            throw new weblogic.jms.common.MessageEOFException(this.readPastEnd(), var5);
        } catch (IOException var6) {
            throw new weblogic.jms.common.JMSException(this.streamReadError(), var6);
        }
    }

    public void writeBoolean(boolean var1) throws JMSException {
        this.writeType((byte) 1);

        try {
            this.bdos.writeBoolean(var1);
        } catch (IOException var3) {
            throw new weblogic.jms.common.JMSException(this.streamWriteError(), var3);
        }
    }

    public void writeByte(byte var1) throws JMSException {
        this.writeType((byte) 2);

        try {
            this.bdos.writeByte(var1);
        } catch (IOException var3) {
            throw new weblogic.jms.common.JMSException(this.streamWriteError(), var3);
        }
    }

    public void writeShort(short var1) throws JMSException {
        this.writeType((byte) 8);

        try {
            this.bdos.writeShort(var1);
        } catch (IOException var3) {
            throw new weblogic.jms.common.JMSException(this.streamWriteError(), var3);
        }
    }

    public void writeChar(char var1) throws JMSException {
        this.writeType((byte) 3);

        try {
            this.bdos.writeChar(var1);
        } catch (IOException var3) {
            throw new weblogic.jms.common.JMSException(this.streamWriteError(), var3);
        }
    }

    public void writeInt(int var1) throws JMSException {
        this.writeType((byte) 6);

        try {
            this.bdos.writeInt(var1);
        } catch (IOException var3) {
            throw new weblogic.jms.common.JMSException(this.streamWriteError(), var3);
        }
    }

    public void writeLong(long var1) throws JMSException {
        this.writeType((byte) 7);

        try {
            this.bdos.writeLong(var1);
        } catch (IOException var4) {
            throw new weblogic.jms.common.JMSException(this.streamWriteError(), var4);
        }
    }

    public void writeFloat(float var1) throws JMSException {
        this.writeType((byte) 5);

        try {
            this.bdos.writeFloat(var1);
        } catch (IOException var3) {
            throw new weblogic.jms.common.JMSException(this.streamWriteError(), var3);
        }
    }

    public void writeDouble(double var1) throws JMSException {
        this.writeType((byte) 4);

        try {
            this.bdos.writeDouble(var1);
        } catch (IOException var4) {
            throw new weblogic.jms.common.JMSException(this.streamWriteError(), var4);
        }
    }

    public void writeString(String var1) throws JMSException {
        if (var1 == null) {
            this.writeType((byte) 12);
        } else {
            try {
                this.writeStringInternal(var1);
            } catch (IOException var3) {
                throw new weblogic.jms.common.JMSException(this.streamWriteError(), var3);
            }
        }

    }

    public void writeBytes(byte[] var1) throws JMSException {
        this.writeBytes(var1, 0, var1.length);
    }

    public void writeBytes(byte[] var1, int var2, int var3) throws JMSException {
        if (var1 == null) {
            throw new NullPointerException();
        } else {
            this.writeType((byte) 11);

            try {
                this.bdos.writeInt(var3);
                this.bdos.write(var1, var2, var3);
            } catch (IOException var5) {
                throw new weblogic.jms.common.JMSException(this.streamWriteError(), var5);
            }
        }
    }

    public void writeObject(Object var1) throws JMSException {
        if (var1 instanceof Boolean) {
            this.writeBoolean(((Boolean) var1).booleanValue());
        } else if (var1 instanceof Number) {
            if (var1 instanceof Byte) {
                this.writeByte(((Byte) var1).byteValue());
            } else if (var1 instanceof Double) {
                this.writeDouble(((Double) var1).doubleValue());
            } else if (var1 instanceof Float) {
                this.writeFloat(((Float) var1).floatValue());
            } else if (var1 instanceof Integer) {
                this.writeInt(((Integer) var1).intValue());
            } else if (var1 instanceof Long) {
                this.writeLong(((Long) var1).longValue());
            } else if (var1 instanceof Short) {
                this.writeShort(((Short) var1).shortValue());
            }
        } else if (var1 instanceof Character) {
            this.writeChar(((Character) var1).charValue());
        } else if (var1 instanceof String) {
            this.writeString((String) var1);
        } else if (var1 instanceof byte[]) {
            this.writeBytes((byte[]) ((byte[]) var1));
        } else {
            if (var1 != null) {
                throw new MessageFormatException("Invalid Type: " + var1.getClass().getName());
            }

            this.writeType((byte) 12);
        }

    }

    public void reset() throws JMSException {
        this.setBodyWritable(false);
        if (this.bdis != null) {
            this.bdis.reset();
        } else if (this.bdos != null) {
            this.buffer = this.bdos.getBuffer();
            this.length = this.bdos.size();
            this.bdos = null;
        }

        this.copyOnWrite = false;
    }

    public MessageImpl copy() throws JMSException {
        StreamMessageImpl var1 = new StreamMessageImpl();
        super.copy(var1);
        if (this.bdos != null) {
            var1.buffer = this.bdos.getBuffer();
            var1.length = this.bdos.size();
            this.copyOnWrite = true;
        } else {
            var1.buffer = this.buffer;
            var1.length = this.length;
        }

        var1.setBodyWritable(false);
        var1.setPropertiesWritable(false);
        return var1;
    }

    private void checkWritable() throws JMSException {
        super.writeMode();
        if (this.bdos == null) {
            this.bdos = new BufferDataOutputStream((ObjectIOBypass) null, 256);
        } else if (this.copyOnWrite) {
            this.bdos.copyBuffer();
            this.copyOnWrite = false;
        }

    }

    private void checkReadable() throws JMSException {
        super.readMode();
        if (this.buffer != null && this.length != 0) {
            if (this.bdis == null) {
                this.bdis = new BufferDataInputStream((ObjectIOBypass) null, this.buffer, 0, this.length);
            }

        } else {
            throw new weblogic.jms.common.MessageEOFException(this.readPastEnd());
        }
    }

    public String toString() {
        return "StreamMessage[" + this.getJMSMessageID() + "]";
    }

    public void writeExternal(ObjectOutput paramObjectOutput) throws IOException {

        super.writeExternal(paramObjectOutput);
        paramObjectOutput.writeByte(1);
        paramObjectOutput.writeInt(getDataSize());
        paramObjectOutput.write(getDataBuffer());

//        super.writeExternal(var1);
//        int var3 = 2147483647;
//        ObjectOutput var2;
//        if(var1 instanceof JMSObjectOutputWrapper) {
//            var3 = ((JMSObjectOutputWrapper)var1).getCompressionThreshold();
//            var2 = ((JMSObjectOutputWrapper)var1).getInnerObjectOutput();
//        } else {
//            var2 = var1;
//        }
//
//        byte var4;
//        if(this.getVersion(var2) >= 30) {
//            var4 = (byte)(3 | (this.shouldCompress(var2, var3)?-128:0));
//        } else {
//            var4 = 2;
//        }
//
//        var2.writeByte(var4);
//        byte[] var5;
//        int var6;
//        if(this.bdos != null) {
//            var5 = this.bdos.getBuffer();
//            var6 = this.bdos.size();
//        } else {
//            var5 = this.buffer;
//            var6 = this.length;
//        }
//
//        if(this.isCompressed()) {
//            if(var4 == 2) {
//                byte[] var7 = this.decompress();
//                var2.writeInt(var7.length);
//                var2.write(var7, 0, var7.length);
//            } else {
//                this.flushCompressedMessageBody(var2);
//            }
//        } else if((var4 & -128) != 0) {
//            this.compressByteArray(var2, var5, var6);
//        } else if(var5 != null && var6 != 0) {
//            var2.writeInt(var6);
//            var2.write(var5, 0, var6);
//        } else {
//            var2.writeInt(0);
//        }

    }

    public final void decompressMessageBody() throws JMSException {
        if (this.isCompressed()) {
            try {
                this.buffer = this.decompress();
                this.length = this.buffer.length;
            } catch (IOException var6) {
                throw new weblogic.jms.common.JMSException(JMSClientExceptionLogger.logErrorDecompressMessageBodyLoggable().getMessage(), var6);
            } finally {
                this.cleanupCompressedMessageBody();
            }

        }
    }

    public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
        super.readExternal(var1);
        byte var2 = var1.readByte();
        byte var3 = (byte) (var2 & 127);
        if (var3 >= 1 && var3 <= 3) {
            switch (var3) {
                case 1:
                    this.length = var1.readInt();
                    this.buffer = new byte[this.length];
                    var1.readFully(this.buffer);
                    ByteArrayInputStream var4 = new ByteArrayInputStream(this.buffer);
                    ObjectInputStream var5 = new ObjectInputStream(var4);
                    this.setBodyWritable(true);
                    this.setPropertiesWritable(true);

                    try {
                        while (true) {
                            this.writeObject(var5.readObject());
                        }
                    } catch (EOFException var9) {
                        try {
                            this.reset();
                            this.setPropertiesWritable(false);
                            byte[] var7 = new byte[this.length];
                            System.arraycopy(this.buffer, 0, var7, 0, this.length);
                            this.buffer = var7;
                        } catch (JMSException var8) {
                            JMSClientExceptionLogger.logStackTrace(var8);
                        }
                    } catch (MessageNotWriteableException var10) {
                        JMSClientExceptionLogger.logStackTrace(var10);
                    } catch (javax.jms.MessageFormatException var11) {
                        JMSClientExceptionLogger.logStackTrace(var11);
                    } catch (JMSException var12) {
                        JMSClientExceptionLogger.logStackTrace(var12);
                    }
                    break;
                case 3:
                    if ((var2 & -128) != 0) {
                        this.saveCompressedMessageBody(var1);
                        break;
                    }
                case 2:
                    if ((this.length = var1.readInt()) > 0) {
                        this.buffer = new byte[this.length];
                        var1.readFully(this.buffer);
                    }
            }

        } else {
            throw JMSUtilities.versionIOException(var3, 1, 3);
        }
    }

    public long getPayloadSize() {
        return this.isCompressed() ? (long) this.getCompressedMessageBodySize() : (super.bodySize != -1L ? super.bodySize : (this.buffer != null ? (super.bodySize = (long) this.length) : (this.bdos != null ? (long) this.bdos.size() : (super.bodySize = 0L))));
    }

    private String typeCodeToString(int var1) {
        try {
            return TYPE_CODE_STRINGS[var1];
        } catch (Throwable var3) {
            return TYPE_CODE_STRINGS[0];
        }
    }

    private void writeStringInternal(String var1) throws IOException, JMSException {
        if (var1.length() > 20000) {
            this.writeType((byte) 10);
            this.bdos.writeUTF32(var1);
        } else {
            this.writeType((byte) 9);
            this.bdos.writeUTF(var1);
        }

    }

    private String readStringInternal(byte var1) throws IOException {
        return var1 == 10 ? this.bdis.readUTF32() : this.bdis.readUTF();
    }

    public final byte[] getDataBuffer() {
        return this.bdos != null ? this.bdos.getBuffer() : this.buffer;
    }

    public final int getDataSize() {
        return this.bdos != null ? this.bdos.size() : this.length;
    }

    public final void setDataBuffer(byte[] var1, int var2) {
        this.buffer = var1;
        this.length = var2;
    }

    public byte[] getMessageBody(int[] var1) throws JMSException {
        if (!this.isCompressed()) {
            var1[0] = this.getDataSize();
            return this.getDataBuffer();
        } else {
            try {
                byte[] var2 = this.decompress();
                var1[0] = var2.length;
                return var2;
            } catch (IOException var3) {
                throw new weblogic.jms.common.JMSException(JMSClientExceptionLogger.logErrorDecompressMessageBodyLoggable().getMessage(), var3);
            }
        }
    }
}
