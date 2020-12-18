package com.zk.cabinet.pdauhf;

/**
 * tool class
 */
public class DataTransfer {
    public static String xGetString(byte[] bs) {
        if (bs != null) {
            StringBuffer sBuffer = new StringBuffer();
            for (int i = 0; i < bs.length; i++) {
                sBuffer.append(String.format("%02x ", bs[i]));
            }
            String str = sBuffer.toString();
            String result = str.replace(" ", "").toUpperCase();
//			if(result.length()>28) {
//				System.out.println(result);
//				return result.substring(17,29);
//			} else {
//				System.out.println(result);
//				return result.substring(result.length()-12,result.length());
//			}


//            if (result.length() > 15) // 标签写了16位以上,取4-16,共12位数据
//                return result.substring(4, 16);
//            else
//                return result; // 返回EPC

            return result; // 返回EPC
        }
        return "null";
    }

    public static byte[] getBytesByHexString(String string) {
        string = string.replaceAll(" ", "");// delete spaces
        int len = string.length();
        if (len % 2 == 1) {
            return null;
        }
        byte[] ret = new byte[len / 2];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = (byte) (Integer.valueOf(string.substring((i * 2), (i * 2 + 2)), 16) & 0xff);
        }
        return ret;
    }

}
