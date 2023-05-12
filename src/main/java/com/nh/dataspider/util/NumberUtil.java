package com.nh.dataspider.util;

import java.util.regex.Pattern;

public class NumberUtil {
	
	static char[] cnArr = new char [] {'一','二','三','四','五','六','七','八','九'};
	static char[] chArr = new char [] {'十','百','千','万','亿'};
	static char[] capitalCnArr = new char [] {'壹','贰','叁','肆','伍','陆','柒','捌','玖'};
	static char[] capitalChArr = new char [] {'拾','佰','仟','万','亿'};
	static String allChineseNum = "零一二三四五六七八九十百千万亿";
	static String allCapitalChineseNum = "零壹贰叁肆伍陆柒捌玖拾佰仟万亿";

	/**
	 * 将汉字中的数字转换为阿拉伯数字
	 * @param chineseNum
	 * @return
	 */
	public static int chineseNumToArabicNum(String chineseNum) {
        int result = 0;
        int temp = 1;//存放一个单位的数字如：十万
        int count = 0;//判断是否有chArr
        for (int i = 0; i < chineseNum.length(); i++) {
            boolean b = true;//判断是否是chArr
            char c = chineseNum.charAt(i);
            for (int j = 0; j < cnArr.length; j++) {//非单位，即数字
                if (c == cnArr[j]) {
                    if(0 != count){//添加下一个单位之前，先把上一个单位值添加到结果中
                        result += temp;
                        temp = 1;
                        count = 0;
                    }
                    // 下标+1，就是对应的值
                    temp = j + 1;
                    b = false;
                    break;
                }
            }
            if(b){//单位{'十','百','千','万','亿'}
                for (int j = 0; j < chArr.length; j++) {
                    if (c == chArr[j]) {
                        switch (j) {
                        case 0:
                            temp *= 10;
                            break;
                        case 1:
                            temp *= 100;
                            break;
                        case 2:
                            temp *= 1000;
                            break;
                        case 3:
                            temp *= 10000;
                            break;
                        case 4:
                            temp *= 100000000;
                            break;
                        default:
                            break;
                        }
                        count++;
                    }
                }
            }
            if (i == chineseNum.length() - 1) {//遍历到最后一个字符
                result += temp;
            }
        }
        return result;
	}
	
	/**
	 * 将汉字中的数字转换为阿拉伯数字
	 * @param chineseNum
	 * @return
	 */
	public static int capitalChineseNumToArabicNum(String chineseNum) {
        int result = 0;
        int temp = 1;//存放一个单位的数字如：十万
        int count = 0;//判断是否有chArr
        for (int i = 0; i < chineseNum.length(); i++) {
            boolean b = true;//判断是否是chArr
            char c = chineseNum.charAt(i);
            for (int j = 0; j < capitalCnArr.length; j++) {//非单位，即数字
                if (c == capitalCnArr[j]) {
                    if(0 != count){//添加下一个单位之前，先把上一个单位值添加到结果中
                        result += temp;
                        temp = 1;
                        count = 0;
                    }
                    // 下标+1，就是对应的值
                    temp = j + 1;
                    b = false;
                    break;
                }
            }
            if(b){//单位{'拾','佰','仟','万','亿'}
                for (int j = 0; j < capitalChArr.length; j++) {
                    if (c == capitalChArr[j]) {
                        switch (j) {
                        case 0:
                            temp *= 10;
                            break;
                        case 1:
                            temp *= 100;
                            break;
                        case 2:
                            temp *= 1000;
                            break;
                        case 3:
                            temp *= 10000;
                            break;
                        case 4:
                            temp *= 100000000;
                            break;
                        default:
                            break;
                        }
                        count++;
                    }
                }
            }
            if (i == chineseNum.length() - 1) {//遍历到最后一个字符
                result += temp;
            }
        }
        return result;
	}

	/**
	 * 将数字转换为中文数字， 这里只写到了万
	 * @param intInput
	 * @return
	 */
	public static String arabicNumToChineseNum(int intInput) {
		String si = String.valueOf(intInput);
		String sd = "";
		if (si.length() == 1) {
			if (intInput == 0) {
				return sd;
			}
			sd += cnArr[intInput - 1];
			return sd;
		} else if (si.length() == 2) {
			if (si.substring(0, 1).equals("1")) {
				sd += "十";
				if (intInput % 10 == 0) {
					return sd;
				}
			}
			else
				sd += (cnArr[intInput / 10 - 1] + "十");
			sd += arabicNumToChineseNum(intInput % 10);
		} else if (si.length() == 3) {
			sd += (cnArr[intInput / 100 - 1] + "百");
			if (String.valueOf(intInput % 100).length() < 2) {
				if (intInput % 100 == 0) {
					return sd;
				}
				sd += "零";
			}
			sd += arabicNumToChineseNum(intInput % 100);
		} else if (si.length() == 4) {
			sd += (cnArr[intInput / 1000 - 1] + "千");
			if (String.valueOf(intInput % 1000).length() < 3) {
				if (intInput % 1000 == 0) {
					return sd;
				}			
				sd += "零";
			}
			sd += arabicNumToChineseNum(intInput % 1000);
		} else if (si.length() == 5) {
			sd += (cnArr[intInput / 10000 - 1] + "万");
			if (String.valueOf(intInput % 10000).length() < 4) {
				if (intInput % 10000 == 0) {
					return sd;
				}
				sd += "零";
			}
			sd += arabicNumToChineseNum(intInput % 10000);
		}
 
		return sd;
	}

	/**
	 * 判断传入的字符串是否全是汉字数字
	 * @param chineseStr
	 * @return
	 */
	public static boolean isChineseNum(String chineseStr) {
		char [] ch = chineseStr.toCharArray();
		for (char c : ch) {
			if (!allChineseNum.contains(String.valueOf(c))) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 判断传入的字符串是否全是汉字大写数字
	 * @param chineseStr
	 * @return
	 */
	public static boolean isCapitalChineseNum(String chineseStr) {
		char [] ch = chineseStr.toCharArray();
		for (char c : ch) {
			if (!allCapitalChineseNum.contains(String.valueOf(c))) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 判断数字字符串是否是整数字符串
	 * @param str
	 * @return
	 */
	public static boolean isNum(String str) {
		String reg = "[0-9]+";
		return str.matches(reg);
	}

	/**
     * 验证整数和浮点数（正负整数和正负浮点数）
     * @param decimals 一位或多位0-9之间的浮点数，如：1.23，233.30
     * @return 验证成功返回true，验证失败返回false
     */
	public static boolean checkDecimals(String decimals) {
        String regex = "\\-?[1-9]\\d+(\\.\\d+)?";
        return Pattern.matches(regex,decimals);
    }
	
	/**
     * 判断字符串是否为数字(含负数)
     * @param
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkNumeric(String str) {
	    String regex = "^-?\\d+(\\.\\d+)?$";
	    return Pattern.matches(regex,str);
    }
}
