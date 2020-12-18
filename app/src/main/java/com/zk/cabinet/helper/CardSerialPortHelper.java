package com.zk.cabinet.helper;


import com.blankj.utilcode.util.LogUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;

import android_serialport_api.SerialPort;

/**
 * 刷卡设备串口帮助类
 */
public class CardSerialPortHelper {

    private static CardSerialPortHelper instance;
    private SerialPort mSerialPort = null;

    private InputStream mInputStream;
    private ReadThread mReadThread;
    private DataReceivedListener mListener;

    public static CardSerialPortHelper getInstance() {
        if (instance == null) {
            synchronized (CardSerialPortHelper.class) {
                if (instance == null)
                    instance = new CardSerialPortHelper();
            }
        }
        return instance;
    }

    public void open(String port) throws SecurityException, IOException, InvalidParameterException {
        SerialPort serialPort = new SerialPort(new File(port), 9600, 0);
        if (serialPort != null) {
            mInputStream = serialPort.getInputStream();
            mReadThread = new ReadThread();
            mReadThread.start();
        }
    }

    public void close() {
        if (mReadThread != null) {
            mReadThread.interrupt();
            mReadThread = null;
        }

        try {
            if (mInputStream != null) {
                mInputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mListener != null)
            mListener = null;

        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }

    private StringBuffer sb = new StringBuffer();

    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                try {
                    byte[] buffer = new byte[4096];
                    if (mInputStream == null) return;
                    int size = mInputStream.read(buffer);
                    if (size > 0) {
                        byte[] readBuffer = new byte[size];
                        System.arraycopy(buffer, 0, readBuffer, 0, size);

                        // 刷卡器数据处理
                        // 02 帧头 ----- 0d0a03 帧尾, 取中间值 3 567094 572
                        String hex = bytesToHex(readBuffer);
                        String result = new String(readBuffer, 0, size).trim();
                        LogUtils.e("刷卡器-接收到数据:", readBuffer, hex, result);

                        sb.append(hex);

                        String sbStr = sb.toString();
                        if (sbStr.endsWith("0d0a03")) {
                            // 数据包发送结束,截取之后再回传结果
                            LogUtils.e("刷卡器-数据接收结束:", sbStr);

                            if (mListener != null) {
                                String idCardHex = sbStr.substring(2, sbStr.length() - 6);
                                LogUtils.e("刷卡器-数据处理结果:", idCardHex);
                                byte[] bytesByHexString = getBytesByHexString(idCardHex);
                                if (bytesByHexString != null && bytesByHexString.length > 0) {
                                    String idCard = new String(bytesByHexString, 0, bytesByHexString.length).trim();
                                    LogUtils.e("刷卡器-数据处理结果(卡号):", "位数:" + idCard.length(), idCard);
                                    mListener.onDataReceived(idCard);
                                }
                            }

                            sb.setLength(0);
                        }

                        try {
                            sleep(100L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    public interface DataReceivedListener {
        void onDataReceived(String data);
    }

    public void setDataReceivedListener(DataReceivedListener dataReceivedListener) {
        mListener = dataReceivedListener;
    }


    /**
     * Hex字符串转byte
     *
     * @param inHex 待转换的Hex字符串
     * @return 转换后的byte
     */
    public static byte hexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }

    /**
     * 16进制字符串转10进制int再转成String
     *
     * @param HexString
     * @return
     */
    public static int hexStringToInt(String HexString) {
        int num = Integer.valueOf(HexString, 16);
        return num;
    }

    /**
     * 字节转十六进制
     *
     * @param b 需要进行转换的byte字节
     * @return 转换后的Hex字符串
     */
    public static String byteToHex(byte b) {
        String hex = Integer.toHexString(b & 0xFF);
        if (hex.length() < 2) {
            hex = "0" + hex;
        }
        return hex;
    }

    /**
     * 字节数组转16进制
     *
     * @param bytes 需要转换的byte数组
     * @return 转换后的Hex字符串
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * hexString转byte
     *
     * @param string
     * @return
     */
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
