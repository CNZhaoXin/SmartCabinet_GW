package com.zk.cabinet.faceServer;

import com.blankj.utilcode.util.LogUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * 人脸识别服务
 * 设备本身作为服务器接收人脸设备客户端识别到的信息
 */
public class FaceRecognitionHttpServer extends NanoHTTPD {

    private static final String TAG = "人脸识别服务";

    private FaceRecognitionListener faceRecognitionListener;

    /**
     * 此参数随便定义，最好定义1024-65535；1-1024是系统常用端口,1024-65535是非系统端口
     *
     * @param faceServerPort
     */
    public FaceRecognitionHttpServer(int faceServerPort) {
        super(faceServerPort); // 初始化端口
    }

    public FaceRecognitionHttpServer(int faceServerPort, FaceRecognitionListener listener) {
        super(faceServerPort); // 初始化端口
        this.faceRecognitionListener = listener;
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        LogUtils.e(TAG, "uri:" + uri);
        Map<String, String> headers = session.getHeaders();
        LogUtils.e(TAG, "headers:" + headers.toString());
        Method method = session.getMethod();
        LogUtils.e(TAG, "method:" + method.toString());

        // 接收不到post参数的问题，https://blog.csdn.net/wan_ing/article/details/80028894?utm_medium=distribute.pc_relevant.none-task-blog-BlogCommendFromBaidu-2.channel_param&depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromBaidu-2.channel_param

        switch (uri) {
            case "/Subscribe/heartbeat":  // 心跳接口
            /* {
                "operator": "HeartBeat",
                    "info": {
                "DeviceID": 1434281,
                        "Time": "2020-09-15T17:16:01"
            }*/
                try {
                    Map<String, String> files = new HashMap<String, String>();
                    session.parseBody(files);
                    String param = files.get("postData");
                    LogUtils.e(TAG, "/Subscribe/heartbeat-心跳接口-param:" + param);

                    if (faceRecognitionListener != null)
                        faceRecognitionListener.heart(param);
                } catch (IOException | ResponseException e) {
                    e.printStackTrace();
                }
                break;

            case "/Subscribe/Verify":  // 人脸认证成功接口
/*            {
                "operator": "VerifyPush",
                        "info": {
                        "PersonID":3,
                        "CreateTime":"2020-09-15T17:19:41",
                        "Similarity1": 94.279579,
                        "Similarity2": 0.000000,
                        "VerifyStatus":1,
                        "VerfyType":1,
                        "PersonType": 0,
                        "Name":"赵鑫",
                        "Gender":0,
                        "Nation":1,
                        "CardType":0,
                        "IdCard":"123456",
                        "Birthday":"2000-01-01",
                        "Telnum":" ",
                        "Native":" ",
                        "Address":" ",
                        "Notes":" ",
                        "MjCardFrom": 0,
                        "DeviceID":1434281,
                        "MjCardNo": 1,
                        "Tempvalid": 0,
                        "CustomizeID": 0,
                        "PersonUUID": " ",
                        "ValidBegin":"0000-00-00T00:00:00",
                        "ValidEnd":"0000-00-00T00:00:00",
                        "Sendintime":1
            } }*/

                try {
                    Map<String, String> files = new HashMap<String, String>();
                    session.parseBody(files);
                    String param = files.get("postData");
                    LogUtils.e(TAG, "/Subscribe/Verify-人脸认证成功接口-param:" + param);

                    if (faceRecognitionListener != null)
                        faceRecognitionListener.success(param);
                } catch (IOException | ResponseException e) {
                    e.printStackTrace();
                }
                break;

            case "/Subscribe/Snap":  // 陌生人人脸接口-未注册的人脸
            /* {
                "operator":"SnapPush",
                    "info":{
                "DeviceID":1434281,
                        "CreateTime":"2020-09-15T17:16:23",
                        "PictureType":0,
                        "Sendintime":1
            },
                "SanpPic":"data:image/jpeg;base64,Qk3m5QAAAAAAADYAAAAoAAAAjAAAAIwAAAABABgAAAAAAAAAAAASCwAAEgs......."
            }
            */
                try {
                    Map<String, String> files = new HashMap<String, String>();
                    session.parseBody(files);
                    String param = files.get("postData");
                    LogUtils.e(TAG, "/Subscribe/Snap-陌生人人脸接口-param:" + param);

                    if (faceRecognitionListener != null)
                        faceRecognitionListener.noRegister(param);
                } catch (IOException | ResponseException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }

        // 返回给客户端空就行了
        return newFixedLengthResponse("");
    }

}