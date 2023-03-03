package wrappers;

import java.io.IOException;

public class JbossRemotingWrap implements BytesWrap<byte[]> {
    public byte[] wrap(byte[] bytes) throws IOException {
        byte[] MagicHead = {119,1,22,121};
        byte[] res = new byte[MagicHead.length+bytes.length];
        System.arraycopy(MagicHead, 0, res, 0, MagicHead.length);
        System.arraycopy(bytes, MagicHead.length, res, MagicHead.length, bytes.length-MagicHead.length);

        return res;
    }
}
