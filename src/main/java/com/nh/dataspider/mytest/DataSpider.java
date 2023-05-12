package com.nh.dataspider.mytest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import com.nh.dataspider.audio.ConvertingAnyAudioToMp3_Example1;
import com.nh.dataspider.manbo.Manbo;
import com.nh.dataspider.missevan.MissEvan;
import com.nh.dataspider.util.FileUtil;
import com.nh.dataspider.util.NumberUtil;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;

public class DataSpider {

	public static void main(String[] args) {
		boolean isLrc = false;
		if(isLrc) {
			String soundId = "7456765";
			int start = getLrcNum(soundId, -1);
			System.out.println("start="+start);
			
			if(soundId.length()<10) {
				MissEvan m = new MissEvan();
				m.downLrc(soundId, start);
//				m.downLrc(soundId);
			}else {
				Manbo m = new Manbo();
				m.downLrc(soundId, start);
//				m.downLrc(soundId);
			}
		}else {
			transMedia();
		}
		
//		List<Map<String, Object>> list = Lists.newArrayList();
//		readFile("H:\\广播剧\\商剧", list);
//		writeexcel(list);
	}
	
	private static int getLrcNum(String soundId, int appoint) {
		if(appoint != -1) {
			return appoint;
		}
		int num = 1;
		try {
			Workbook workbook = FileUtil.getUploadExcelWorkbook("H:\\广播剧\\商剧\\config.xlsx");
			FormulaEvaluator evaluator=workbook.getCreationHelper().createFormulaEvaluator();
			Sheet sheet = workbook.getSheetAt(0);
			
			int rows = sheet.getLastRowNum();
			if(rows>0) {
				for(int i=1; i<=rows; i++) {
					Row row = sheet.getRow(i);
					if(row != null) {
						Cell cell = row.getCell(0);
						String excelId = FileUtil.readCellValue(cell, evaluator);
						if(excelId.equals(soundId)) {
							cell = row.getCell(1);
							String filePath = FileUtil.readCellValue(cell, evaluator);
							File file = new File(filePath);
							File[] fileArray = file.listFiles();
							num = fileArray.length;
							for(File f : fileArray) {
								if(f.getName().indexOf("重置")>0) {
									num--;
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return num;
	}
	
	private static void transMedia() {
		String filePath = "C:\\Users\\CES\\Downloads\\test";
		String transPath = "C:\\Users\\CES\\Downloads\\test\\trans";
		String name = "暗恋翻车后";
		String season = " 全一季";
		String album = name+season;
//		String titlePrefix = "寒远";
		String titlePrefix = album.replace(" 全一季", "");
		String albumArtist = "晋江文学城 壶鱼辣椒原著，猫耳FM出品，风音工作室制作，广播剧《我在无限游戏里封神》";
		String artist = "顺子&倒霉死勒";
		String comment = "『祝你好运，新人玩家』";
		fillProperty(filePath, transPath, album, titlePrefix, albumArtist, artist, comment);
		System.out.println("trans success");
	}
	
	private static void fillProperty(String filePath, String transPath, String album, String titlePrefix, String albumArtist, String artist, String comment) {
		try {
			File file = new File(filePath);
			File[] fileArray = file.listFiles();
	        if (fileArray == null) {
	            System.out.println("no file");
	        } else {
	            for (File f : fileArray) {
	            	if(f.isFile()&&(f.getName().indexOf("mp3")>0 || f.getName().indexOf("m4a")>0 || f.getName().indexOf("flac")>0 || f.getName().indexOf("wav")>0)) {
	            		String name = f.getName();
	            		String targetPath = transPath + "\\" +name.substring(0, name.lastIndexOf("."))+".mp3";
	            		ConvertingAnyAudioToMp3_Example1.convertingAnyAudioToMp3(f.getAbsolutePath(), targetPath);
	            		if (f.exists() && f.isFile()) {
	                        f.delete();
	                    }
	            	} else if(f.isFile()&&f.getName().indexOf("mp4")>0) {
	            		reName(filePath);
	            	}
	            }
	        }
	        
	        file = new File(transPath);
			fileArray = file.listFiles();
	        if (fileArray == null) {
	            System.out.println("no file");
	        } else {
	        	Mp3File mp3file = null;
	            for (File f : fileArray) {

	            	if(f.isFile()&&f.getName().indexOf("mp3")>0) {
	            		String name = f.getName();
	            		mp3file = new Mp3File(f);
	                    ID3v2 id3v2Tag = mp3file.getId3v2Tag();
	                    if(id3v2Tag != null) {
	                    	String title = titlePrefix+" "+ name.substring(0, name.lastIndexOf("."))+"";
	                    	if(NumberUtil.checkNumeric(name.substring(0, name.lastIndexOf("."))+"")) {
	                    		title = titlePrefix+" "+ "第"+name.substring(0, name.lastIndexOf("."))+""+"集";
	                    	}
	                    	id3v2Tag.setTitle(title);
		                	id3v2Tag.setAlbum(album);
		                	id3v2Tag.setAlbumArtist(albumArtist);
		                	id3v2Tag.setArtist(artist);
		                	id3v2Tag.setComment(comment);
		                	
		                	String targetPath = filePath+filePath.substring(filePath.lastIndexOf(File.separator), filePath.length());
			            	//检查目标路径下，name是否已经存在
			            	if(FileUtil.fileIsFile(targetPath+"\\"+name)) {
			            		name = album+"-"+name;
			            	}
		                	mp3file.save(targetPath+"\\"+name);
		                	
		                	File targetF = new File(targetPath);
		                    if (!targetF.exists()) {
		                    	targetF.mkdir();
		                    }
		                	mp3file.save(targetPath+"\\"+name);
		                	
		                	if (f.exists() && f.isFile()) {
		                        f.delete();
		                    }
	                    }

	            	} else if(f.isFile()&&f.getName().indexOf("mp4")>0) {
	            		reName(filePath);
	            	}
	            }
	        }
	        System.out.println("rename success");
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	                	File rf = new File(filePath+"\\"+f.getName());
		            	f.renameTo(rf);
	            	}
	            }
	        }
	        System.out.println("rename success");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void writeexcel(List<Map<String, Object>> list) {
		try {
            List<ExcelExportEntity> entitys = new ArrayList<>();
            entitys.add(new ExcelExportEntity("id", "id"));
            entitys.add(new ExcelExportEntity("path", "path"));
            ExportParams exportParams = new ExportParams(null, "info");
            Workbook workbook = ExcelExportUtil.exportExcel(exportParams, entitys, list);
            String fileName = "config.xls";
            File file = new File("H:\\广播剧\\商剧"+ File.separator + fileName);
            file.createNewFile();//创建文件
            FileOutputStream fos = new FileOutputStream(file);
            workbook.write(fos);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public static void readFile(String path, List<Map<String, Object>> list) {
		Map<String, Object> map = null;
		File file = new File(path);
		File[] fileArray = file.listFiles();
		for(File f : fileArray) {
			if(f.isDirectory()) {
				System.out.println(f.getPath());
				readFile(f.getPath(), list);
				if(f.getName().startsWith("lrc")) {
					map = new HashMap<String, Object>();
					map.put("id", f.getName().replace("lrc", ""));
					map.put("path", f.getPath());
					list.add(map);
				}
			}
		}
	}
}
