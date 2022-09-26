package wrappers;

import com.thoughtworks.xstream.XStream;

import java.nio.charset.StandardCharsets;

public class XstreamWrap implements ObjectWrapper<byte[]> {
    public byte[] wrap(Object obj){
        XStream xStream = new XStream();
        String xml = xStream.toXML(obj);

        return xml.getBytes(StandardCharsets.UTF_8);
    }
}
