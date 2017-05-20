package com.braindroid.nervecenter.transformation;

import java.util.HashMap;
import java.util.Map;

public class DataUtils {

    // clever idea from {http://stackoverflow.com/users/662728/aerthel}
    public static Map<String, String> build(String... data){
        HashMap<String, String> result = new HashMap<>();

        if(data.length % 2 != 0)
            throw new IllegalArgumentException("Odd number of arguments");

        String key = null;
        Integer step = -1;

        for(String value : data){
            step++;
            switch(step % 2){
                case 0:
                    if(value == null)
                        throw new IllegalArgumentException("Null key value");
                    key = value;
                    continue;
                case 1:
                    result.put(key, value);
                    break;
            }
        }

        return result;
    }
}
