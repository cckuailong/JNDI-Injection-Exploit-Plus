package util;

import org.apache.commons.lang.ArrayUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

public class UTF8FusionObjectOutputStream extends ObjectOutputStream {

    public UTF8FusionObjectOutputStream(OutputStream out) throws IOException {
        super(out);
    }

    public static byte[] convertChar(char s, int n) {
        if (n == 3) {
            int i = s & 0xFFF;
            byte b1 = (byte)(((i >> 12) & 0b1111) | 0b11100000);
            byte b2 = (byte)(((i >> 6) & 0b111111) | 0b10000000);
            byte b3 = (byte)((i & 0b111111) | 0b10000000);

            return new byte[] {b1, b2, b3};
        }else if (n == 2){
            int i = s & 0xFF;
            byte b1 = (byte)(((i >> 6) & 0b11111) | 0b11000000);
            byte b2 = (byte)((i & 0b111111) | 0b10000000);

            return new byte[] {b1, b2};
        }else {
            return new byte[] {(byte) s};
        }
    }

    @Override
    @SuppressWarnings("all")
    protected void writeClassDescriptor(ObjectStreamClass desc) throws IOException {
        String name = desc.getName();
        byte fusion_bytes[] = new byte[]{};
        for (int i = 0; i < name.length(); i++) {
            char s = name.charAt(i);
            Random rand = new Random();
            int randomNum = rand.nextInt(3) + 1; // 2 or 3
            byte[] both = ArrayUtils.addAll(fusion_bytes, convertChar(s, randomNum));
            fusion_bytes = both;
        }
        writeShort(fusion_bytes.length);
        write(fusion_bytes);
        writeLong(desc.getSerialVersionUID());
        try {
            byte flags = 0;
            if ((boolean) getFieldValue(desc, "externalizable")) {
                flags |= ObjectStreamConstants.SC_EXTERNALIZABLE;
                Field protocolField = ObjectOutputStream.class.getDeclaredField("protocol");
                protocolField.setAccessible(true);
                int protocol = (int) protocolField.get(this);
                if (protocol != ObjectStreamConstants.PROTOCOL_VERSION_1) {
                    flags |= ObjectStreamConstants.SC_BLOCK_DATA;
                }
            } else if ((boolean) getFieldValue(desc, "serializable")) {
                flags |= ObjectStreamConstants.SC_SERIALIZABLE;
            }
            if ((boolean) getFieldValue(desc, "hasWriteObjectData")) {
                flags |= ObjectStreamConstants.SC_WRITE_METHOD;
            }
            if ((boolean) getFieldValue(desc, "isEnum")) {
                flags |= ObjectStreamConstants.SC_ENUM;
            }
            writeByte(flags);
            ObjectStreamField[] fields = (ObjectStreamField[]) getFieldValue(desc, "fields");
            writeShort(fields.length);
            for (int i = 0; i < fields.length; i++) {
                ObjectStreamField f = fields[i];
                writeByte(f.getTypeCode());
                byte f_fusion_bytes[] = new byte[]{};
                for (int ii = 0; ii < f.getName().length(); ii++) {
                    char ss = f.getName().charAt(ii);
                    Random rand = new Random();
                    int f_randomNum = rand.nextInt(3) + 1; // 2 or 3
                    byte[] f_both = ArrayUtils.addAll(f_fusion_bytes, convertChar(ss, f_randomNum));
                    f_fusion_bytes = f_both;
                }
                writeShort(f_fusion_bytes.length);
                write(f_fusion_bytes);
                if (!f.isPrimitive()) {
                    Method writeTypeString = ObjectOutputStream.class.getDeclaredMethod(
                        "writeTypeString", String.class);
                    writeTypeString.setAccessible(true);
                    writeTypeString.invoke(this, f.getTypeString());
                }
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getFieldValue(Object object, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Class<?> clazz = object.getClass();
        Field field = clazz.getDeclaredField(fieldName); field.setAccessible(true);
        Object value = field.get(object);
        return value;
    }
}
