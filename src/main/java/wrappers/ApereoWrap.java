package wrappers;

import org.apereo.spring.webflow.plugin.EncryptedTranscoder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public class ApereoWrap implements ObjectWrapper<byte[]> {
    public byte[] wrap(Object obj) throws IOException {
        String id = UUID.randomUUID().toString();
        EncryptedTranscoder et = new EncryptedTranscoder();
        byte[] bytecode = et.encode(obj);
        String payload =  Base64.getEncoder().encodeToString(bytecode);
        String data = URLEncoder.encode(id + "_" + payload, "UTF-8");

        return data.getBytes(StandardCharsets.UTF_8);
    }
}
