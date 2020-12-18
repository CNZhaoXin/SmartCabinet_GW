package com.zk.cabinet.helper;


import com.blankj.utilcode.util.LogUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import android_serialport_api.SerialPort;

/**
 * 档案组架灯控串口帮助类
 */
public class LightsSerialPortHelper {

    private static LightsSerialPortHelper instance;
    private SerialPort mSerialPort = null;

//    private InputStream mInputStream;
//    private ReadThread mReadThread;

    // todo 灯控板串口需要配置
    // 本文档是锁孔主板与工控机通信接口规范说明，数据通信采用485方式通信，串口波特率19200、8位数据位、无校验，1位停止位。
    private String sPort = "/dev/ttyS2";
    private int iBaudRate = 19200;
    private OutputStream mOutputStream;
    private SendThread mSendThread;
    private boolean isOpen = false;
    private List<byte[]> sendList;
    private Object addSendSyncLock;
    private boolean sendFlag = true;
    private DataReceivedListener mListener;

    public static LightsSerialPortHelper getInstance() {
        if (instance == null) {
            synchronized (LightsSerialPortHelper.class) {
                if (instance == null)
                    instance = new LightsSerialPortHelper();
            }
        }
        return instance;
    }

    public void open(String port) throws SecurityException, IOException, InvalidParameterException {
        if (!isOpen) {
            mSerialPort = new SerialPort(new File(port), this.iBaudRate, 0);
            addSendSyncLock = new Object();
            sendList = new ArrayList();
            mOutputStream = this.mSerialPort.getOutputStream();
            mSendThread = new SendThread();
            mSendThread.start();
            isOpen = true;
//            this.mInputStream = this.mSerialPort.getInputStream();
//            this.mReadThread = new SerialHelper.ReadThread();
//            this.mReadThread.start();
            LogUtils.e("打开灯控串口-不连接设备也会打开,所以前提是保证灯控设备连在上面了");
        }
    }

    public void close() {
//        if (mReadThread != null)
//            mReadThread.interrupt();
        if (isOpen) {
            LogUtils.e("关闭灯控串口");
            if (mSendThread != null)
                mSendThread.interrupt();

            if (mSerialPort != null) {
                mSerialPort.close();
                mSerialPort = null;
            }

            isOpen = false;
        }
    }

    public void addSendTask(byte[] sendDate) {
        sendList.add(sendDate);
        if (sendList.size() >= 1) {
            synchronized (addSendSyncLock) {
                addSendSyncLock.notify();
            }
        }
    }

    public void send(byte[] bOutArray) {
        try {
            mOutputStream.write(bOutArray);
            LogUtils.e("灯控串口发送：", bOutArray.length, bOutArray, bytesToHex(bOutArray));
        } catch (IOException var3) {
            var3.printStackTrace();
        }
    }

    public void stopSend() {
        if (this.mSendThread != null) {
            this.mSendThread.interrupt();
        }

        this.sendFlag = false;
        if (this.addSendSyncLock != null) {
            synchronized (this.addSendSyncLock) {
                this.addSendSyncLock.notify();
            }
        }

    }

    private class SendThread extends Thread {
        private SendThread() {
        }

        public void run() {
            super.run();
            while (!this.isInterrupted()) {
                while (true) {
                    while (true) {
                        if (sendFlag) {
                            if (sendList != null && sendList.size() != 0) {
                                byte[] send = (byte[]) sendList.remove(0);
                                if (send != null) {
                                    // LogUtils.e("串口通信Android发送：", send, send.length);
                                    send(send);
                                }
                                try {
                                    sleep(100L);
                                } catch (InterruptedException var3) {
                                    var3.printStackTrace();
                                }
                            } else {
                                try {
                                    synchronized (addSendSyncLock) {
                                        addSendSyncLock.wait();
                                    }
                                } catch (InterruptedException var5) {
                                    var5.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // 帧头2byte 目标地址1byte(灯控板id 1-10) 源地址1byte 帧长度2byte 通信类2byte 内容2byte(灯状态1byte + 灯号1byte)     校验和1byte
    // 0xA6A8	0x01	                   0x00	        0x0009	 0x0001    灯状态：1 亮灯 0：灭灯 灯号：序号从1开始     需要计算
    // 例如(01设备1号灯亮灯):A6A8 01 00 0009 0001 0101  5B (校验和需要计算前面的长度,再转成16进制)

    /**
     * 开档案组架灯
     * 先把16进制转化成十进制,然后转化成byte,再拼接成byte[]数组,最后发送byte[]数组数据
     *
     * @param deviceId 灯控板id: 数值范围:0-10
     *                 灯状态：亮灯:1 灭灯:0
     * @param floor    层数: 数值范围:1-5
     * @param lightNum 灯号： 数值范围0-80 ,大灯:0 小灯:1-80
     */
    public void openLight(int deviceId, int floor, int lightNum) {
        byte[] data = new byte[11];
        data[0] = (byte) 166; // A6 = 166
        data[1] = (byte) 168; // A8 = 168
        data[2] = (byte) deviceId;
        data[3] = (byte) 0;
        data[4] = (byte) 0;
        data[5] = (byte) 9;
        data[6] = (byte) 0;
        data[7] = (byte) 1;
        data[8] = (byte) 1; // status = 1 , 亮灯
        // todo 柜子灯号需要计算,硬件是1-16一层,但是只有15个格子,需根据层数计算正确的灯号
        // 一层灯带(8+8=16)灯,柜子15个格子15灯,空一个灯
        if (deviceId <= 5) { // deviceId 1-5
            // 灯带 从左到右 开始蛇形绕线
            if (floor % 2 == 0) {
                // 2层,4层
                lightNum = floor * 16 - lightNum;
            } else {
                // 1层,3层,5层
                lightNum = (floor - 1) * 16 + lightNum;
            }
        } else { // deviceId 6-10
            // 灯带 从右到左 开始盘
            if (floor % 2 == 0) {
                // 2层,4层
                lightNum = (floor - 1) * 16 + lightNum;
            } else {
                // 1层,3层,5层
                lightNum = floor * 16 - lightNum;
            }
        }

        data[9] = (byte) lightNum;
        data[10] = calcCheckBit(data); // 计算校验位

        LogUtils.e("档案组架-亮灯发送数据：", "数据长度:" + data.length, "byte数组原数据:", data, "byte数组转16进制数据:" + bytesToHex(data));
        addSendTask(data);
    }

    /**
     * 灭档案组架灯
     */
    public void closeLight(int deviceId, int floor, int lightNum) {
        byte[] data = new byte[11];
        data[0] = (byte) 166; // A6 = 166
        data[1] = (byte) 168; // A8 = 168
        data[2] = (byte) deviceId;
        data[3] = (byte) 0;
        data[4] = (byte) 0;
        data[5] = (byte) 9;
        data[6] = (byte) 0;
        data[7] = (byte) 1;
        data[8] = (byte) 0; // status = 0 , 灭灯
        // todo 柜子灯号需要计算,硬件是1-16一层,但是只有15个格子,需根据层数计算正确的灯号
        // 一层灯带(8+8=16)灯,柜子15个格子15灯,空一个灯
        if (deviceId <= 5) { // deviceId 1-5
            // 灯带 从左到右 开始蛇形绕线
            if (floor % 2 == 0) {
                // 2层,4层
                lightNum = floor * 16 - lightNum;
            } else {
                // 1层,3层,5层
                lightNum = (floor - 1) * 16 + lightNum;
            }
        } else { // deviceId 6-10
            // 灯带 从右到左 开始盘
            if (floor % 2 == 0) {
                // 2层,4层
                lightNum = (floor - 1) * 16 + lightNum;
            } else {
                // 1层,3层,5层
                lightNum = floor * 16 - lightNum;
            }
        }

        data[9] = (byte) lightNum;
        data[10] = calcCheckBit(data); // 计算校验位

        LogUtils.e("档案组架-亮灯发送数据：", "数据长度:" + data.length, "byte数组原数据:", data, "byte数组转16进制数据:" + bytesToHex(data));
        addSendTask(data);
    }

    /**
     * 开大灯
     *
     * @param deviceId
     */
    public void openBigLight(int deviceId) {
        byte[] data = new byte[11];
        data[0] = (byte) 166; // A6 = 166
        data[1] = (byte) 168; // A8 = 168
        data[2] = (byte) deviceId;
        data[3] = (byte) 0;
        data[4] = (byte) 0;
        data[5] = (byte) 9;
        data[6] = (byte) 0;
        data[7] = (byte) 1;
        data[8] = (byte) 1; // status = 1 , 亮灯
        data[9] = (byte) 0; // lightNum=0,大灯灯号
        data[10] = calcCheckBit(data); // 计算校验位

        LogUtils.e("档案组架-亮灯发送数据：", "数据长度:" + data.length, "byte数组原数据:", data, "byte数组转16进制数据:" + bytesToHex(data));
        addSendTask(data);
    }

    /**
     * 灭大灯
     *
     * @param deviceId
     */
    public void closeBigLight(int deviceId) {
        byte[] data = new byte[11];
        data[0] = (byte) 166; // A6 = 166
        data[1] = (byte) 168; // A8 = 168
        data[2] = (byte) deviceId;
        data[3] = (byte) 0;
        data[4] = (byte) 0;
        data[5] = (byte) 9;
        data[6] = (byte) 0;
        data[7] = (byte) 1;
        data[8] = (byte) 0; // status = 0 , 灭灯
        data[9] = (byte) 0; // lightNum=0,大灯灯号
        data[10] = calcCheckBit(data); // 计算校验位

        LogUtils.e("档案组架-亮灯发送数据：", "数据长度:" + data.length, "byte数组原数据:", data, "byte数组转16进制数据:" + bytesToHex(data));
        addSendTask(data);
    }

    /**
     * 计算校验位
     * 亮灯的这个协议的校验位计算规则是:校验位前的数据的总加和
     *
     * @param protocol
     * @return
     */
    public static byte calcCheckBit(byte[] protocol) {
        byte checkBit = protocol[0];

        for (int i = 1; i < protocol.length - 1; ++i) {
            checkBit += protocol[i];
        }

        return checkBit;
    }

    public interface DataReceivedListener {
        void onDataReceived(String data);
    }

    public void setDataReceivedListener(DataReceivedListener dataReceivedListener) {
        mListener = dataReceivedListener;
    }

    /**
     * hex字符串转byte数组
     *
     * @param inHex 待转换的Hex字符串
     * @return 转换后的byte数组结果
     */
    public static byte[] hexToByteArray(String inHex) {
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1) {
            //奇数
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {
            //偶数
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = hexToByte(inHex.substring(i, i + 2));
            j++;
        }
        return result;
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

}
