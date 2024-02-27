package wrappers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

public class GzipWrap implements BytesWrap<byte[]> {
    public byte[] wrap(byte[] bytes) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(bytes.length);
        GZIPOutputStream gzipOS = new GZIPOutputStream(bos);
        gzipOS.write(bytes);
        gzipOS.close();

        return bos.toByteArray();
    }
}
