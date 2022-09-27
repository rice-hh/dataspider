package com.nh.dataspider.file;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Writer;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;

/**
 * @author ：nh
 * @date ：2021/6/18
 * @description：
 */
public class MediaFileDeal {
	
	public static void main(String[] args) {
		String filePath = "C:\\Users\\nh\\Downloads\\test";
//		String filePath = "C:\\Users\\nh\\Downloads\\第2季";
		String album = "将进酒 第二季";
		String albumArtist = "晋江文学城唐酒卿原著，斟酌文创出品制作，光合积木联合出品录制，广播剧《将进酒》";
		String artist = "袁铭喆&姜广涛";
		String comment = "『我要翻越那座山。』『我将为自己一战。』 ";
		String coverType = "jpg";
		reName(filePath);
//		modifyProperty(filePath, album, albumArtist, artist, comment, coverType);
//		reSetProperty(filePath, album, albumArtist, artist, comment, coverType);
		
//		try {
//			testOut();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	private static void reName(String filePath) {
		try {
			File file = new File(filePath);
			File[] fileArray = file.listFiles();
	        if (fileArray == null) {
	            System.out.println("no file");
	        } else {
	            for (File f : fileArray) {

	            	if(f.isFile()) {
	            		String name = f.getName();
		            	if(name.indexOf("【")>0) {
		             		name = name.substring(0, name.indexOf("【"))+name.substring(name.lastIndexOf("."),name.length());
		            	}
		            	
		            	if(name.indexOf("[")>0) {
		            		name = name.substring(0, name.indexOf("["))+name.substring(name.lastIndexOf("."),name.length());
		            	}
		            	
		            	if(name.indexOf("公众号")>0) {
		            		name = name.replace("微信公众号FM小屋", "");
		            		name = name.replace("微信公众号  FM小屋", "");
		            		name = name.replace("微信公众号 FM小屋", "");
		            		name = name.replace("微信 公众号 FM小屋", "");
		            		name = name.replace("第三季", "");
		            	}
		            	
	                	File rf = new File(filePath+"\\"+name);
		            	f.renameTo(rf);
	                    
	            	}
	            }
	        }
	        System.out.println("rename success");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void modifyProperty(String filePath, String album, String albumArtist, String artist, String comment, String coverType) {
		try {
			File file = new File(filePath);
			File[] fileArray = file.listFiles();
	        if (fileArray == null) {
	            System.out.println("no file");
	        } else {
	        	Mp3File mp3file = null;
	            for (File f : fileArray) {

	            	if(f.isFile()&&f.getName().indexOf("mp3")>0) {
	            		String name = f.getName();
		            	if(name.indexOf("【")>0) {
		            		name = name.substring(0, name.indexOf("【"))+name.substring(name.lastIndexOf("."),name.length());
		            	}
		            	
		            	if(name.indexOf("[")>0) {
		            		name = name.substring(0, name.indexOf("["))+name.substring(name.lastIndexOf("."),name.length());
		            	}
		            	
	                    mp3file = new Mp3File(f);
	                    ID3v2 id3v2Tag = mp3file.getId3v2Tag();
	                    if(id3v2Tag != null) {
//	                    	String title = album+" "+ name.substring(0, name.lastIndexOf("."));
//	                    	id3v2Tag.setTitle(title);
//	                    	id3v2Tag.setAlbum(album);
	                    	String title = id3v2Tag.getTitle();
		                    if(title == null) {
		                    	title = album;
		                    	id3v2Tag.setTitle(album+" "+ name.substring(0, name.lastIndexOf("."))+"");
			                	id3v2Tag.setAlbum(album);
		                    }else {
		                    	if(title.indexOf("【")>0) {
			                    	title = title.substring(0, title.indexOf("【")).trim();
			                    }
			                    
			                    if(title.indexOf("[")>0) {
			                    	title = title.substring(0, title.indexOf("[")).trim();
			                    }
			                    
			                    id3v2Tag.setTitle(title);
			                    id3v2Tag.setAlbum(album);
//			                    if(title.lastIndexOf(" ")>0) {
//			                    	id3v2Tag.setAlbum(title.substring(0, title.lastIndexOf(" ")));
//			                    }else{
//			                    	id3v2Tag.setAlbum(album);
//			                    }
			                	
		                    }
		                    
		                	id3v2Tag.setAlbumArtist(albumArtist);
		                	id3v2Tag.setArtist(artist);
		                	id3v2Tag.setComment(comment);
		                	RandomAccessFile cover = new RandomAccessFile(filePath+File.separator+"cover"+File.separator+"cover."+coverType, "r");
		                	byte[] bytes = new byte[(int) file.length()];
		                	cover.read(bytes);
		                	cover.close();
		                	id3v2Tag.setAlbumImage(bytes, "image/"+coverType);
		                	String targetPath = filePath+filePath.substring(filePath.lastIndexOf(File.separator), filePath.length());
		                	File targetF = new File(targetPath);
		                    if (!targetF.exists()) {
		                    	targetF.mkdir();
		                    }
		                	mp3file.save(targetPath+"\\"+name);
		                    
//		                	File rf = new File(filePath+"\\"+name);
//			            	rf.renameTo(nf);
		                    
		                	if (f.exists() && f.isFile()) {
		                        f.delete();
		                    }
	                    }else {
		            		reName(filePath);
		            	}
	                    
	            	} else if(f.isFile()&&f.getName().indexOf("mp4")>0) {
	            		reName(filePath);
	            	}
	            		
	            }
	        }
	        System.out.println("rename success");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void reSetProperty(String filePath, String album, String albumArtist, String artist, String comment, String coverType) {
		try {
			File file = new File(filePath);
			File[] fileArray = file.listFiles();
	        if (fileArray == null) {
	            System.out.println("no file");
	        } else {
	        	Mp3File mp3file = null;
	            for (File f : fileArray) {

	            	if(f.isFile()) {
	            		String name = f.getName();
		            	if(name.indexOf("【")>0) {
		            		name = name.substring(0, name.indexOf("【"))+name.substring(name.lastIndexOf("."),name.length());
		            	}
		            	
		            	if(name.indexOf("[")>0) {
		            		name = name.substring(0, name.indexOf("["))+name.substring(name.lastIndexOf("."),name.length());
		            	}
		            	
		            	if(name.indexOf("微信公众号")>0) {
		            		name = name.replace("微信公众号FM小屋", "");
		            		name = name.replace("微信公众号  FM小屋", "");
		            		name = name.replace("微信公众号 FM小屋", "");
		            	}
		            	
	                    mp3file = new Mp3File(f);
	                    ID3v2 id3v2Tag = mp3file.getId3v2Tag();
	                    if(id3v2Tag != null) {
		                    id3v2Tag.setTitle(album + " 第"+ name.substring(0, name.lastIndexOf("."))+"集");
//		                    id3v2Tag.setTitle(album + " "+ name.substring(0, name.lastIndexOf("."))+"");
		                	id3v2Tag.setAlbum(album);
		                	id3v2Tag.setAlbumArtist(albumArtist);
		                	id3v2Tag.setArtist(artist);
		                	id3v2Tag.setComment(comment);
		                	RandomAccessFile cover = new RandomAccessFile(filePath+File.separator+"cover"+File.separator+"cover."+coverType, "r");
		                	byte[] bytes = new byte[(int) file.length()];
		                	cover.read(bytes);
		                	cover.close();
		                	id3v2Tag.setAlbumImage(bytes, "image/"+coverType);
		                	String targetPath = filePath+filePath.substring(filePath.lastIndexOf(File.separator), filePath.length());
		                	File targetF = new File(targetPath);
		                    if (!targetF.exists()) {
		                    	targetF.mkdir();
		                    }
		                	mp3file.save(targetPath+"\\"+name);
		                    
//		                	File rf = new File(filePath+"\\"+name);
//			            	rf.renameTo(nf);
		                    
		                	if (f.exists() && f.isFile()) {
		                		f.delete();
		                    }
	                    }
	            	}
	            }
	        }
	        System.out.println("rename success");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void testOut() throws IOException {
		
		//1
//		OutputStream out = new FileOutputStream("G:\\nh\\test\\r.txt");
//		OutputStreamWriter osw = new OutputStreamWriter(out);
//		osw.write("hello");
//		osw.write("world");
//		osw.close();
		
		//2
		FileWriter fw = new FileWriter("G:\\nh\\test\\r.txt");
		fw.write("hello");
		fw.write("world");
		fw.close();
		
		//3
		FileWriter fw1 = new FileWriter("G:\\nh\\test\\r.txt");
		BufferedWriter bfw = new BufferedWriter(fw1);
		bfw.write("hello");
		//写入一个换行符。BufferedWriter比FileWriter多出的函数
		bfw.newLine();
		bfw.write("world");
		bfw.close();
		fw1.close();
		
		Writer out1 = new BufferedWriter(new OutputStreamWriter(System.out));
		
		System.out.println("write success");
	}
}
