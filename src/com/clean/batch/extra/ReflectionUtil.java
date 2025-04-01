package com.clean.batch.extra;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

public class ReflectionUtil {

    public static ReflectionUtil getInstance() {
        return ReflectionUtil.Singleton.instance;
    }

    private static class Singleton {
        private static final ReflectionUtil instance = new ReflectionUtil();
    }

    public <T> T generate(Class<T> clazz, String targetClassName, List<Object> params) throws Exception{
        Object[] initArgs = (params==null|| params.isEmpty()) ? new Object[0]:params.toArray();

        Class<?> targetClass = Class.forName(targetClassName);

        Constructor<?> constructor = null;
        for(Constructor con : targetClass.getDeclaredConstructors()){
            Class[] types = con.getParameterTypes();
            if(types.length!=initArgs.length) continue;

            boolean match = true;
            for(int i=0; i<types.length; i++){
                Class<?> c = types[i], a = initArgs[i].getClass();
                if(c.isAssignableFrom(a) == false){
                    if(
                            int.class.equals(c) && Integer.class.equals(a) 		||
                                    long.class.equals(c) && Long.class.equals(a) 		||
                                    char.class.equals(c) && Character.class.equals(a) 	||
                                    short.class.equals(c) && Short.class.equals(a)		||
                                    boolean.class.equals(c) && Boolean.class.equals(a)	||
                                    byte.class.equals(c) && Byte.class.equals(a)
                    ){
                        ;
                    }else{
                        match = false;
                    }
                }
                if(match == false) break;
            }
            if(match){
                constructor = con;
                break;
            }
        }
        if(constructor == null){
            throw new IllegalArgumentException("Cannot find an appropriate constructor for class " + targetClass.getSimpleName() + " and arguments " + Arrays.toString(initArgs));
        }
        return clazz.cast(constructor.newInstance(initArgs));
    }

}
