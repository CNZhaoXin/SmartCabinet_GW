package com.zk.cabinet.faceServer;

public interface FaceRecognitionListener {
    /**
     * 识别成功人员信息
     *
     * @param result
     */
    void success(String result);

    /**
     * 未注册人脸信息
     *
     * @param result
     */
    void noRegister(String result);

    /**
     * 心跳信息
     *
     * @param result
     */
    void heart(String result);
}
