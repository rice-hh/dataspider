package com.nh.dataspider.mytest;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public final class Person implements Serializable{
	private String name;
	private int age;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
	
	public Person() {
		
	}
	
	public Person(String name, int age) {
		this.name = name;
		this.age = age;
	}

	private Object readResolve() {
		return 2;
//		return new Person("2",2);
	}
	
	public static void main(String[] args) {
		final String s = "2";
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("F:\\Resources\\test\\person.txt"));
			Person p = new Person("1",1);
			oos.writeObject(p);
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream("F:\\Resources\\test\\person.txt"));
			Person readObject = (Person)ois.readObject();
			System.out.println(readObject.getName());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
