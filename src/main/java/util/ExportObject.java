package util;

import javax.naming.Context;
import javax.naming.Name;

import java.util.Hashtable;

public class ExportObject implements javax.naming.spi.ObjectFactory {
    public ExportObject() {
        try {
            // something
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) {
        return null;
    }
}
