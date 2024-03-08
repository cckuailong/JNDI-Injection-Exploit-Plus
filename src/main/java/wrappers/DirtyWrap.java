package wrappers;

import common.Serializerable;

import java.io.IOException;
import java.util.*;

public class DirtyWrap implements ObjectWrapper<byte[]> {
    public byte[] wrap(Object obj) throws IOException {
        Object wrapper = null;

        String dirtyData = getLongString(100000);
        int type = (int)(Math.random() * 5) % 5 + 1;
        switch (type){
            case 0:
                List<Object> arrayList = new ArrayList<Object>();
                arrayList.add(dirtyData);
                arrayList.add(obj);
                wrapper = arrayList;
                break;
            case 1:
                List<Object> linkedList = new LinkedList<Object>();
                linkedList.add(dirtyData);
                linkedList.add(obj);
                wrapper = linkedList;
                break;
            case 2:
                HashMap<String,Object> map = new HashMap<String, Object>();
                map.put("a", dirtyData);
                map.put("b", obj);
                wrapper = map;
                break;
            case 3:
                LinkedHashMap<String,Object> linkedHashMap = new LinkedHashMap<String,Object>();
                linkedHashMap.put("a", dirtyData);
                linkedHashMap.put("b", obj);
                wrapper = linkedHashMap;
                break;
            default:
            case 4:
                TreeMap<String,Object> treeMap = new TreeMap<String, Object>();
                treeMap.put("a", dirtyData);
                treeMap.put("b", obj);
                wrapper = treeMap;
                break;
        }

        return Serializerable.serialize(wrapper);
    }

    public static String getLongString(int length){
        StringBuilder str = new StringBuilder();
        for (int i=0;i<length;i++){
            str.append("x");
        }
        return str.toString();
    }
}
