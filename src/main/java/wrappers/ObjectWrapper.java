package wrappers;


import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Set;


@SuppressWarnings ( "rawtypes" )
public interface ObjectWrapper<T> {

    public T wrap(Object obj) throws Exception;

    public static class Utils {

        // get payload classes by classpath scanning
        public static Set<Class<? extends ObjectWrapper>> getPayloadClasses () {
            final Reflections reflections = new Reflections(ObjectWrapper.class.getPackage().getName());
            final Set<Class<? extends ObjectWrapper>> payloadTypes = reflections.getSubTypesOf(ObjectWrapper.class);
            for (Iterator<Class<? extends ObjectWrapper>> iterator = payloadTypes.iterator(); iterator.hasNext(); ) {
                Class<? extends ObjectWrapper> pc = iterator.next();
                if ( pc.isInterface() || Modifier.isAbstract(pc.getModifiers()) ) {
                    iterator.remove();
                }
            }
            return payloadTypes;
        }


        @SuppressWarnings ( "unchecked" )
        public static Class<? extends ObjectWrapper> getWrapperClass (final String className ) {
            Class<? extends ObjectWrapper> clazz = null;
            try {
                clazz = (Class<? extends ObjectWrapper>) Class.forName(className);
            }
            catch ( Exception e1 ) {}
            if ( clazz == null ) {
                try {
//                    System.out.println("wrappers." + util.Utils.Title(className) + "Wrap");
                    return clazz = (Class<? extends ObjectWrapper>) Class
                            .forName("wrappers." + util.Utils.Title(className) + "Wrap");
                }
                catch ( Exception e2 ) {}
            }
            if ( clazz != null && !ObjectWrapper.class.isAssignableFrom(clazz) ) {
                clazz = null;
            }
            return clazz;
        }

    }
}
