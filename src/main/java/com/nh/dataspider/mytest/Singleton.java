package com.nh.dataspider.mytest;

import java.io.Serializable;
import java.lang.reflect.Constructor;

import org.apache.commons.lang3.SerializationUtils;

public class Singleton implements Serializable {
	private static class SingletonHolder {
        private static Singleton instance = new Singleton();
    }

    private Singleton() {
        
    }

    public static Singleton getInstance() {
        return SingletonHolder.instance;
    }
    
//    public static void main(String[] args) throws Exception {
//        Singleton singleton = Singleton.getInstance();
//        Constructor<Singleton> constructor = Singleton.class.getDeclaredConstructor();
//        constructor.setAccessible(true);
//        Singleton newSingleton = constructor.newInstance();
//        System.out.println(singleton == newSingleton);
//    }
    
    public static void main(String[] args) {
    	Singleton singleton = Singleton.getInstance();
    	byte[] serialize = SerializationUtils.serialize(singleton);
    	Object deserialize = SerializationUtils.deserialize(serialize);
    	System.out.println(singleton == deserialize);
	}
}
