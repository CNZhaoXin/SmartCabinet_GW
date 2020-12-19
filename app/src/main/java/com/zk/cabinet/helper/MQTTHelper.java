package com.zk.cabinet.helper;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.zk.cabinet.R;
import com.zk.cabinet.activity.ZNGAutoInventoryActivity;
import com.zk.cabinet.bean.Device;
import com.zk.cabinet.bean.LightControlRecord;
import com.zk.cabinet.constant.SelfComm;
import com.zk.cabinet.db.DeviceService;
import com.zk.cabinet.db.LightControlRecordService;
import com.zk.cabinet.entity.ResultMQTTMessageInventory;
import com.zk.cabinet.entity.ResultMQTTMessageLight;
import com.zk.cabinet.net.NetworkRequest;
import com.zk.cabinet.utils.SharedPreferencesUtil;
import com.zk.rfid.bean.UR880SendInfo;
import com.zk.rfid.ur880.UR880Entrance;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.List;

import static com.blankj.utilcode.util.ActivityUtils.startActivity;

public class MQTTHelper {

    //    外网测试MQTT地址:
//    地址：47.96.95.4  
//    端口：1883
//    用户名密码:test/test
//    主题：pad
    private MqttAndroidClient mqttAndroidClient;
    // public final String serverUri = "tcp://" + "47.96.95.4" + ":1883";
    public final String subscriptionTopic = "pad"; // 订阅主题
    // 要保证客户端id不一样,不然服务器往订阅主题群发消息,相同客户端id就只会发一次
    // 连接MQTT的设备ID(就用操作屏设备ID)
    private String clientId;
    private static MQTTHelper mqHelper = null;
    private Context mContext;

    public static MQTTHelper getInstance() {
        if (mqHelper == null) {
            mqHelper = new MQTTHelper();
        }
        return mqHelper;
    }

    public void connectMQTT(Context context) {
        clientId = SharedPreferencesUtil.Companion.getInstance().getString(SharedPreferencesUtil.Key.EquipmentId, "");
        if (TextUtils.isEmpty(clientId)) {
            ToastUtils.showLong("MQTT服务器-请先配置操作屏设备ID");
            return;
        }
        mContext = context;
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setUserName("test");
        mqttConnectOptions.setPassword("test".toCharArray());
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);

        try {
            LogUtils.e("MQTT服务器-待连接的MQTT客户端ID(配置的设备ID):" + clientId);
            String mMQTTUrl = NetworkRequest.Companion.getInstance().mMQTTUrl;
            LogUtils.e("MQTT服务器-待连接的MQTT服务器地址:" + mMQTTUrl);
            mqttAndroidClient = new MqttAndroidClient(context, mMQTTUrl, clientId);
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    LogUtils.e("MQTT服务器-连接成功");
                    ToastUtils.setBgColor(context.getResources().getColor(R.color.green_primary));
                    ToastUtils.setMsgColor(context.getResources().getColor(R.color.white));
                    ToastUtils.showLong("MQTT服务器-连接成功");
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    LogUtils.e("MQTT服务器-连接失败: " + mMQTTUrl);
                    ToastUtils.setBgColor(context.getResources().getColor(R.color.red_primary));
                    ToastUtils.setMsgColor(context.getResources().getColor(R.color.white));
                    ToastUtils.showLong("MQTT服务器-连接失败");
                }
            });

        } catch (MqttException ex) {
            ex.printStackTrace();
        }

        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                if (reconnect) {
                    LogUtils.e("MQTT服务器-断开需要重连:" + serverURI);
                    // Because Clean Session is true, we need to re-subscribe
                    subscribeToTopic();
                } else {
                    LogUtils.e("MQTT服务器-已连接无需重连:" + serverURI);
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                if (cause != null) {
                    LogUtils.e("MQTT服务器-The Connection was lost:" + cause.getMessage());
                } else {
                    LogUtils.e("MQTT服务器-The Connection was lost");
                }
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                LogUtils.e("MQTT服务器-Incoming message:" + "主题: " + topic + ",消息: " + new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

    }

    /**
     * 设置界面改变了MQTT地址和端口之后,重新调用连接
     */
    public void reConnectMQTT() {
        if (mContext != null) {
            MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setUserName("test");
            mqttConnectOptions.setPassword("test".toCharArray());
            mqttConnectOptions.setAutomaticReconnect(true);
            mqttConnectOptions.setCleanSession(false);

            try {
                clientId = clientId + System.currentTimeMillis();
                String mMQTTUrl = NetworkRequest.Companion.getInstance().mMQTTUrl;
                mqttAndroidClient = new MqttAndroidClient(mContext, mMQTTUrl, clientId);
                mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                        disconnectedBufferOptions.setBufferEnabled(true);
                        disconnectedBufferOptions.setBufferSize(100);
                        disconnectedBufferOptions.setPersistBuffer(false);
                        disconnectedBufferOptions.setDeleteOldestMessages(false);
                        mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                        LogUtils.e("MQTT服务器-连接成功");
                        ToastUtils.setBgColor(mContext.getResources().getColor(R.color.green_primary));
                        ToastUtils.setMsgColor(mContext.getResources().getColor(R.color.white));
                        ToastUtils.showLong("MQTT服务器-连接成功");
                        subscribeToTopic();
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        LogUtils.e("MQTT服务器-连接失败: " + mMQTTUrl);
                        ToastUtils.setBgColor(mContext.getResources().getColor(R.color.red_primary));
                        ToastUtils.setMsgColor(mContext.getResources().getColor(R.color.white));
                        ToastUtils.showLong("MQTT服务器-连接失败");
                    }
                });

            } catch (MqttException ex) {
                ex.printStackTrace();
            }

            mqttAndroidClient.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    if (reconnect) {
                        LogUtils.e("MQTT服务器-断开连接-即将重连:" + serverURI);
                        // Because Clean Session is true, we need to re-subscribe
                        // subscribeToTopic();
                        connectMQTT(mContext);
                    } else {
                        LogUtils.e("MQTT服务器-已连接:" + serverURI);
                    }
                }

                @Override
                public void connectionLost(Throwable cause) {
                    LogUtils.e("MQTT服务器-The Connection was lost:" + cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    LogUtils.e("MQTT服务器-Incoming message:" + "主题: " + topic + ",消息: " + new String(message.getPayload()));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
        }
    }

    /**
     * 订阅主题
     */
    public void subscribeToTopic() {
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    LogUtils.e("MQTT服务器-Subscribed-主题订阅成功");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    LogUtils.e("MQTT服务器-Failed to subscribe-主题订阅失败");
                }
            });

            mqttAndroidClient.subscribe(subscriptionTopic, 0, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    // 接收到消息,处理消息
                    LogUtils.e("MQTT服务器-messageArrived:" + "主题: " + topic + ",消息: " + new String(message.getPayload()));
                    handleMQTTMessage(message);
                }
            });

        } catch (MqttException ex) {
            LogUtils.e("MQTT服务器-Exception whilst subscribing");
            ex.printStackTrace();
        }
    }

    public void closeMQTT() {
        if (mqttAndroidClient != null) {
            mqttAndroidClient.close();
        }
    }

    /**
     * MQTT消息处理(亮灯,灭灯,自动盘库)
     */
    private void handleMQTTMessage(MqttMessage message) {
        // 1.已保证只有这三种设备才会连接MQTT(档案组架1,档案组柜2,档案单柜3)
        // 因为现在的设计是MQTT消息会推送给所有的操作屏,所以需要自行过滤属于当前操作屏的MQTT消息
        String msg = new String(message.getPayload());
        JSONObject msgJsonObject = JSON.parseObject(msg);
        String msgType = msgJsonObject.getString("msgType");

        // DEVICE_NAME[1] = "档案组架"
        // DEVICE_NAME[2] = "档案组柜"
        // DEVICE_NAME[3] = "档案单柜"
        // DEVICE_NAME[4] = "一体机"
        // DEVICE_NAME[5] = "PDA"

        // 所选设备类型
        String deviceName = SharedPreferencesUtil.Companion.getInstance().getString(SharedPreferencesUtil.Key.DeviceName, "");
        LogUtils.e("MQTT服务器-所选设备类型:" + deviceName);
        // 操作屏ID
        String mEquipmentId = SharedPreferencesUtil.Companion.getInstance().getString(SharedPreferencesUtil.Key.EquipmentId, "");

        switch (msgType) {
            case "2": // 亮灯消息
                LogUtils.e("MQTT服务器-MQTT处理消息-消息类型-" + msgType + "-亮灯消息");
                // 何时会调用亮灯接口?
                // 1.档案室只有一个人且他有借阅单时,平台调用亮灯,只亮借阅档案,灭灯消息平台10分钟后调用 (档案组架.档案组柜.档案单柜 都需支持)(此版本无法判断档案室是否只有一个人,暂不考虑该情况)
                // 2.一体机主动调用亮灯(只有档案组架的档案),灭灯消息平台10分钟后调用

                // 判断是否是发给当前操作屏设备ID的,是才处理,不是不处理
                // 档案组架要亮大灯,再亮对应的灯
                // todo 档案组柜直接亮灯,因需登录后进行操作,这个亮灯其实只是提醒作用(主要也是为了亮大灯(组柜自动亮大灯))
                // todo 档案单柜,亮灯不开柜门,开柜还是要登录之后操作借阅也会亮灯,登录之后借阅会开门再进行借阅操作,这个亮灯其实只是提醒作用(主要也是为了亮大灯(单柜自动亮大灯))

                // 亮灯消息实体类
                ResultMQTTMessageLight resultMQTTMessageLight = JSON.parseObject(msg, ResultMQTTMessageLight.class);
                List<ResultMQTTMessageLight.DataBean> equipmentList = resultMQTTMessageLight.getData();
                if (equipmentList != null && equipmentList.size() > 0) {
                    for (int i = 0; i < equipmentList.size(); i++) {
                        // 属于当前操作屏设备的消息才进行处理
                        ResultMQTTMessageLight.DataBean equipmentData = equipmentList.get(i);
                        if (mEquipmentId.equals(equipmentData.getEquipmentId())) {
                            // 多个档案架/档案柜亮灯处理(档案组架的操作屏下有多个档案组架,档案组柜的操作屏下有多个档案组柜,档案单柜的操作屏只有一个档案组柜)
                            List<ResultMQTTMessageLight.DataBean.CabinetBeanListBean> cabinetBeanList = equipmentData.getCabinetBeanList();
                            if (cabinetBeanList != null && cabinetBeanList.size() > 0) {
                                for (int j = 0; j < cabinetBeanList.size(); j++) {
                                    // 单个档案架/档案柜的亮灯处理
                                    ResultMQTTMessageLight.DataBean.CabinetBeanListBean cabinetBeanListBean = cabinetBeanList.get(i);
                                    // 单个档案架/档案柜的多份档案的处理
                                    List<ResultMQTTMessageLight.DataBean.CabinetBeanListBean.DataListBean> lightDossierList = cabinetBeanListBean.getDataList();
                                    if (lightDossierList != null && lightDossierList.size() > 0) {

                                        // 1档案组架,亮灯消息处理
                                        if (deviceName.equals(SelfComm.INSTANCE.getDEVICE_NAME().get(1))) {
                                            // 1档案组架: 获取需亮灯档案所属的档案架的设备ID(操作屏ID-灯控板ID),判断本地是否已添加该档案架设备ID(操作屏ID-灯控板ID),如果有,那么就拿到灯控板ID进行亮灯操作
                                            // 单个档案组架,若有多份需要亮灯的档案,也只需要亮一次大灯
                                            String equipmentId = cabinetBeanListBean.getEquipmentId();
                                            // 档案组架设备ID: 01-01-001-1 (档案室编号-01代表档案组架操作屏-档案组架操作屏序号-灯控板ID)
                                            if (equipmentId.contains("-")) {
                                                String[] split = equipmentId.split("-");
                                                // 灯控板ID
                                                // 这个地方如果平台配置的不是数字会导致程序崩溃
                                                int lightControlBoardID = Integer.parseInt(split[split.length - 1]);
                                                LogUtils.e("MQTT服务器-需亮灯的灯控板ID:" + lightControlBoardID);

                                                for (int k = 0; k < lightDossierList.size(); k++) {
                                                    // 拿到档案,进行库位亮灯,1库位可能存在多个灯
                                                    ResultMQTTMessageLight.DataBean.CabinetBeanListBean.DataListBean dataListBean = lightDossierList.get(k);
                                                    List<Integer> lampList = dataListBean.getLampList();
                                                    for (int l = 0; l < lampList.size(); l++) {
                                                        // 添加亮灯记录,亮灯记录存在,亮灯次数+1,不存在,添加亮灯记录
                                                        String record = lightControlBoardID + "-" + dataListBean.getRowNo() + "-" + dataListBean.getNumNo();
                                                        List<LightControlRecord> lightControlRecords = LightControlRecordService.getInstance().queryListByRecord(record);
                                                        if (lightControlRecords != null && lightControlRecords.size() > 0) { // 存在亮灯次数+1
                                                            LightControlRecord lightControlRecord = lightControlRecords.get(0);
                                                            lightControlRecord.setNum(lightControlRecord.getNum() + 1);
                                                            LightControlRecordService.getInstance().update(lightControlRecord);
                                                            LogUtils.e("MQTT服务器-灯控-添加-亮灯记录次数更新:", lightControlRecord.getDeviceID(), lightControlRecord.getRecord(), lightControlRecord.getNum());
                                                        } else { // 不存在,添加亮灯记录
                                                            LightControlRecord lightControlRecord = new LightControlRecord(null, String.valueOf(lightControlBoardID), record, 1);
                                                            LightControlRecordService.getInstance().insert(lightControlRecord);
                                                            LogUtils.e("MQTT服务器-灯控-添加-亮灯记录:", lightControlRecord.getDeviceID(), lightControlRecord.getRecord(), lightControlRecord.getNum());
                                                        }

                                                        LightsSerialPortHelper.getInstance().openLight(lightControlBoardID, dataListBean.getRowNo(), dataListBean.getNumNo());
                                                    }
                                                }

                                                // 亮大灯
                                                LightsSerialPortHelper.getInstance().openBigLight(lightControlBoardID);
                                                LogUtils.e("MQTT服务器-灯控-亮大灯:", lightControlBoardID);

                                                // 亮灯记录打印
                                                List<LightControlRecord> allRecords = LightControlRecordService.getInstance().loadAll();
                                                if (allRecords != null && allRecords.size() > 0) {
                                                    LogUtils.e("MQTT服务器-灯控-亮灯记录:", allRecords.size(), JSON.toJSONString(allRecords));
                                                } else {
                                                    LogUtils.e("MQTT服务器-灯控-亮灯记录: null");
                                                }
                                            }

                                        }

                                        // 档案组柜2 / 档案单柜3, 亮灯消息处理
                                        if (deviceName.equals(SelfComm.INSTANCE.getDEVICE_NAME().get(2))
                                                || deviceName.equals(SelfComm.INSTANCE.getDEVICE_NAME().get(3))) {
                                            // 某个档案柜id
                                            String equipmentId = cabinetBeanListBean.getEquipmentId();
                                            // 这里说明了该档案柜有需要亮灯的档案,那么就亮大灯提醒是哪个柜子
                                            ArrayList<Integer> lights = new ArrayList<>();
                                            lights.add(1);
                                            UR880Entrance.getInstance().send(new UR880SendInfo.Builder().turnOnLight(equipmentId, 6, lights).build());
                                            LogUtils.e("MQTT服务器-灯控-亮大灯:", equipmentId);

                                            // 保存该柜亮大灯记录
                                            List<LightControlRecord> lightControlRecords = LightControlRecordService.getInstance().queryListByDeviceID(equipmentId);
                                            if (lightControlRecords != null && lightControlRecords.size() > 0) {
                                                // 存在该柜亮大灯记录,记录数+1
                                                LightControlRecord lightControlRecord = lightControlRecords.get(0);
                                                lightControlRecord.setNum(lightControlRecord.getNum() + 1);
                                                LightControlRecordService.getInstance().update(lightControlRecord);

                                                LogUtils.e("MQTT服务器-该柜亮灯亮大灯记录已存在,无需再添加", JSON.toJSONString(lightControlRecord));
                                            } else {
                                                // 不存在,新增一条某柜亮大灯记录
                                                LightControlRecord bigLightRecord = new LightControlRecord(null, equipmentId, "", 1);
                                                LightControlRecordService.getInstance().insert(bigLightRecord);

                                                LogUtils.e("MQTT服务器-新增该柜亮大亮记录", JSON.toJSONString(bigLightRecord));
                                            }

                                            // 亮灯记录打印
                                            List<LightControlRecord> allRecords = LightControlRecordService.getInstance().loadAll();
                                            if (allRecords != null && allRecords.size() > 0) {
                                                LogUtils.e("MQTT服务器-灯控-亮灯记录:", allRecords.size(), JSON.toJSONString(allRecords));
                                            } else {
                                                LogUtils.e("MQTT服务器-灯控-亮灯记录: null");
                                            }

                                        }

                                    }
                                }
                            }
                        }
                    }
                }
                break;
            case "3": // 灭灯消息
                LogUtils.e("MQTT服务器-MQTT处理消息-消息类型-" + msgType + "-灭灯消息");
                // 灭灯消息实体类
                ResultMQTTMessageLight resultMQTTMessageLight1 = JSON.parseObject(msg, ResultMQTTMessageLight.class);
                List<ResultMQTTMessageLight.DataBean> equipmentList1 = resultMQTTMessageLight1.getData();
                if (equipmentList1 != null && equipmentList1.size() > 0) {
                    for (int i = 0; i < equipmentList1.size(); i++) {
                        // 属于当前操作屏设备的消息才进行处理
                        ResultMQTTMessageLight.DataBean equipmentData = equipmentList1.get(i);
                        if (mEquipmentId.equals(equipmentData.getEquipmentId())) {
                            // 多个档案架/档案柜灭灯处理(档案组架的操作屏下有多个档案组架,档案组柜的操作屏下有多个档案组柜,档案单柜的操作屏只有一个档案组柜)
                            List<ResultMQTTMessageLight.DataBean.CabinetBeanListBean> cabinetBeanList = equipmentData.getCabinetBeanList();
                            if (cabinetBeanList != null && cabinetBeanList.size() > 0) {
                                for (int j = 0; j < cabinetBeanList.size(); j++) {
                                    // 单个档案架/档案柜的灭灯处理
                                    ResultMQTTMessageLight.DataBean.CabinetBeanListBean cabinetBeanListBean = cabinetBeanList.get(i);
                                    // 单个档案架/档案柜的多份档案的处理
                                    List<ResultMQTTMessageLight.DataBean.CabinetBeanListBean.DataListBean> lightDossierList = cabinetBeanListBean.getDataList();
                                    if (lightDossierList != null && lightDossierList.size() > 0) {

                                        // 1档案组架,灭灯消息处理
                                        if (deviceName.equals(SelfComm.INSTANCE.getDEVICE_NAME().get(1))) {
                                            // 1档案组架: 获取需亮灯档案所属的档案架的设备ID(操作屏ID-灯控板ID),判断本地是否已添加该档案架设备ID(操作屏ID-灯控板ID),如果有,那么就拿到灯控板ID进行亮灯操作
                                            // 单个档案组架,若有多份需要灭灯的档案,先灭灯,再看亮灯记录里面是否还有记录,没有任何亮灯记录才灭灯
                                            String equipmentId = cabinetBeanListBean.getEquipmentId();
                                            if (equipmentId.contains("-")) {
                                                String[] split = equipmentId.split("-");
                                                // 灯控板ID
                                                // 这个地方如果平台配置的不是数字会导致程序崩溃
                                                int lightControlBoardID = Integer.parseInt(split[split.length - 1]);
                                                LogUtils.e("MQTT服务器-需灭灯的灯控板ID:" + lightControlBoardID);

                                                for (int k = 0; k < lightDossierList.size(); k++) {
                                                    // 拿到档案,进行库位灭灯,1库位可能存在多个灯
                                                    ResultMQTTMessageLight.DataBean.CabinetBeanListBean.DataListBean dataListBean = lightDossierList.get(k);
                                                    List<Integer> lampList = dataListBean.getLampList();
                                                    for (int l = 0; l < lampList.size(); l++) {
                                                        // 删除亮灯记录,亮灯记录存在,亮灯次数-1,-1后等于0删除记录,不等于0更新记录,记录不存在,记录不处理
                                                        String record = lightControlBoardID + "-" + dataListBean.getRowNo() + "-" + dataListBean.getNumNo();
                                                        List<LightControlRecord> lightControlRecords = LightControlRecordService.getInstance().queryListByRecord(record);
                                                        if (lightControlRecords != null && lightControlRecords.size() > 0) {
                                                            LightControlRecord lightControlRecord = lightControlRecords.get(0);
                                                            int num = lightControlRecord.getNum() - 1;
                                                            if (num == 0) {
                                                                LogUtils.e("MQTT服务器-灯控-删除:", lightControlRecord.getDeviceID(), lightControlRecord.getRecord());
                                                                LightControlRecordService.getInstance().delete(lightControlRecord);
                                                            } else {
                                                                lightControlRecord.setNum(num);
                                                                LogUtils.e("MQTT服务器-灯控-删除-亮灯记录次数更新:", lightControlRecord.getDeviceID(), lightControlRecord.getRecord(), lightControlRecord.getNum());
                                                                LightControlRecordService.getInstance().update(lightControlRecord);
                                                            }
                                                        }

                                                        LightsSerialPortHelper.getInstance().closeLight(lightControlBoardID, dataListBean.getRowNo(), dataListBean.getNumNo());
                                                    }
                                                }

                                                // 灭大灯,当当前柜子不存在任何亮灯记录了,就灭当前柜的大灯
                                                List<LightControlRecord> lightControlRecords = LightControlRecordService.getInstance().queryListByDeviceID(String.valueOf(lightControlBoardID));
                                                if (lightControlRecords == null || lightControlRecords.size() == 0) {
                                                    LogUtils.e("MQTT服务器-灯控-灭大灯:", lightControlBoardID);
                                                    LightsSerialPortHelper.getInstance().closeBigLight(lightControlBoardID);
                                                } else {
                                                    LogUtils.e("MQTT服务器-灯控-无需灭大灯-还存在亮灯记录", JSON.toJSONString(lightControlRecords));
                                                }

                                                // 亮灯记录打印
                                                List<LightControlRecord> allRecords = LightControlRecordService.getInstance().loadAll();
                                                if (allRecords != null && allRecords.size() > 0) {
                                                    LogUtils.e("MQTT服务器-灯控-亮灯记录:", allRecords.size(), JSON.toJSONString(allRecords));
                                                } else {
                                                    LogUtils.e("MQTT服务器-灯控-亮灯记录: null");
                                                }
                                            }
                                        }

                                        // 档案组柜2 / 档案单柜3 灭灯消息处理
                                        if (deviceName.equals(SelfComm.INSTANCE.getDEVICE_NAME().get(2))
                                                || deviceName.equals(SelfComm.INSTANCE.getDEVICE_NAME().get(3))) {
                                            // 某个档案柜id
                                            String equipmentId = cabinetBeanListBean.getEquipmentId();
                                            // 灭大灯
                                            List<LightControlRecord> lightControlRecords = LightControlRecordService.getInstance().queryListByDeviceID(equipmentId);
                                            if (lightControlRecords != null && lightControlRecords.size() > 0) {
                                                LightControlRecord lightControlRecord = lightControlRecords.get(0);
                                                int num = lightControlRecord.getNum() - 1;
                                                if (num == 0) {
                                                    // 次数变为0,说明档案组柜无人亮过该大灯了,或者已经在操作界面退出时被灭过了,可以灭大灯,并删除记录
                                                    ArrayList<Integer> lights = new ArrayList<>();
                                                    lights.add(2);
                                                    UR880Entrance.getInstance().send(new UR880SendInfo.Builder().turnOnLight(equipmentId, 6, lights).build());
                                                    LogUtils.e("MQTT服务器-灯控-灭大灯:", equipmentId);
                                                    // 删除记录
                                                    LightControlRecordService.getInstance().delete(lightControlRecord);
                                                } else {
                                                    // 次数不为0,说明有人亮了灯,更新记录,不能灭大灯
                                                    lightControlRecord.setNum(num);
                                                    LightControlRecordService.getInstance().update(lightControlRecord);
                                                    LogUtils.e("MQTT服务器-灯控-灭大灯-更新记录:", equipmentId);
                                                }
                                            }

                                            // 亮灯记录打印
                                            List<LightControlRecord> allRecords = LightControlRecordService.getInstance().loadAll();
                                            if (allRecords != null && allRecords.size() > 0) {
                                                LogUtils.e("MQTT服务器-灯控-亮灯记录:", allRecords.size(), JSON.toJSONString(allRecords));
                                            } else {
                                                LogUtils.e("MQTT服务器-灯控-亮灯记录: null");
                                            }

                                        }

                                    }
                                }
                            }
                        }
                    }
                }
                break;

            case "5": // 自动盘库计划消息
                LogUtils.e("MQTT服务器-MQTT处理消息-消息类型-" + msgType + "-自动盘库计划消息");
                // 1.判断是否是 档案组柜2/档案单柜3(档案组柜,档案单柜 才能自动盘点)
                if (SelfComm.INSTANCE.getDEVICE_NAME().get(2).equals(deviceName)
                        || SelfComm.INSTANCE.getDEVICE_NAME().get(3).equals(deviceName)) {
                    // 自动盘库计划消息实体类
                    ResultMQTTMessageInventory resultMQTTMessageInventory = JSON.parseObject(msg, ResultMQTTMessageInventory.class);
                    ResultMQTTMessageInventory.DataBean data = resultMQTTMessageInventory.getData();
                    if (data != null) {
                        List<ResultMQTTMessageInventory.DataBean.CabineBeanListBean> cabineBeanList = data.getCabineBeanList();
                        if (cabineBeanList != null && cabineBeanList.size() > 0) {
                            // 封装需要盘点的档案柜设备实体集合
                            ArrayList<Device> devicesList = new ArrayList<>();

                            for (int i = 0; i < cabineBeanList.size(); i++) {
                                ResultMQTTMessageInventory.DataBean.CabineBeanListBean cabineBeanListBean = cabineBeanList.get(i);
                                // 2.再判断需自动盘库的档案组柜/档案单柜 是不是属于当前操作屏的, 是不是档案组柜/档案单柜
                                if (mEquipmentId.equals(cabineBeanListBean.getMasterEquipmentId())
                                        && ("2".equals(cabineBeanListBean.getCabinetType()) || "3".equals(cabineBeanListBean.getCabinetType()))) {
                                    // 3.最后判断这个需要自动盘库的柜子ID是否在当前操作屏已经配置,并直接封装成需要盘点的档案柜设备实体
                                    Device deviceSetting = DeviceService.getInstance().queryByDeviceID(cabineBeanListBean.getEquipmentId());
                                    if (deviceSetting != null) {
                                        devicesList.add(deviceSetting);
                                    }
                                }
                            }

                            if (devicesList.size() > 0) {
                                LogUtils.e("MQTT服务器-MQTT处理消息-消息类型-自动盘库计划消息-需要盘点的本操作屏的柜子:", JSON.toJSONString(devicesList));
                                // 开启盘点,先测1个柜子的盘点,再测2个柜子的切换盘点,再测分开两个设备屏发的盘点
                                // 1.判断当前是否在首页(判断当前是否是GuideActivity)
                                if (ActivityUtils.getTopActivity().getComponentName().getClassName().equals("com.zk.cabinet.activity.GuideActivity")) {
                                    // 当前是首页,就直接进行盘点了
                                    // 打开档案柜自动盘点界面,传递需要盘库的柜子
                                    LogUtils.e("MQTT服务器-MQTT处理消息-消息类型-自动盘库计划消息-当前是主页,打开自动盘库界面进行盘库");
                                    Intent intent = new Intent(mContext, ZNGAutoInventoryActivity.class);
                                    intent.putExtra(ZNGAutoInventoryActivity.PLAN_ID, data.getId());
                                    intent.putExtra(ZNGAutoInventoryActivity.HOUSE_CODE, data.getHouseCode());
                                    intent.putExtra(ZNGAutoInventoryActivity.EQUIPMENT_ID_LIST, JSON.toJSONString(devicesList));
                                    startActivity(intent);
                                } else {
                                    // todo 当前不是首页,需要将计划单保存到数据库中,等待界面回到主页面立刻进行盘点,那要时刻监听数据库表中的数据,一回到主页面就直接盘点盘掉
                                    LogUtils.e("MQTT服务器-MQTT处理消息-消息类型-自动盘库计划消息-当前不是主页");

                                    // 测试Github提交
                                }
                            }
                        }
                    }
                }

                break;
        }

    }

//  final String publishTopic = "exampleAndroidPublishTopic"; // 推送主题
//  final String publishMessage = "Hello World!";// 推送信息

//   推送消息
//    public void publishMessage() {
//        try {
//            MqttMessage message = new MqttMessage();
//            message.setPayload(publishMessage.getBytes());
//            mqttAndroidClient.publish(publishTopic, message);
//            // addToHistory("Message Published");
//            if (!mqttAndroidClient.isConnected()) {
//                // addToHistory(mqttAndroidClient.getBufferedMessageCount() + " messages in buffer.");
//            }
//        } catch (MqttException e) {
//            System.err.println("Error Publishing: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }

}
