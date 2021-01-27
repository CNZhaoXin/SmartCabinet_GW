package com.zk.cabinet.net

import android.content.Context
import com.hik.cabinet.net.VolleyRequest
import com.zk.cabinet.helper.MQTTHelper
import com.zk.cabinet.utils.SharedPreferencesUtil
import com.zk.cabinet.utils.SharedPreferencesUtil.Key

class NetworkRequest : VolleyRequest() {
    lateinit var mMQTTUrl: String

    lateinit var mClientLogin: String
    lateinit var mLoginByIdCard: String
    lateinit var mLoginByCardNo: String

    lateinit var mSearchArchivesInfo: String
    lateinit var mGetHumitureByHouseId: String
    lateinit var mLightUp: String
    lateinit var mGetHouseList: String
    lateinit var mGetArchivesInfoByRFID: String
    lateinit var mGetBoxListByPosCode: String
    lateinit var mBind: String
    lateinit var mUnBind: String
    lateinit var mPosBind: String
    lateinit var mGetNoStartInventoryPlan: String
    lateinit var mSubmitInventoryResult: String
    lateinit var mSubmitIntInventoryResult: String
    lateinit var metDoneInventoryPlan: String
    lateinit var mGetToBorrowList: String
    lateinit var mArchivesBorrow: String
    lateinit var mArchivesReturn: String
    lateinit var mGetToReturnList: String
    lateinit var mGetLastInfo: String
    lateinit var mGetCabineMasterByEquipmentId: String
    lateinit var mGetCapital: String
    lateinit var mGetAIOInfoByEquipmentId: String
    lateinit var mGetPosInfoByCabinetEquipmentId: String
    lateinit var mGetAllCodeType: String
    lateinit var mGetInventoryDifferenceByPlanId: String

    companion object {
        // 阿里云外网测试服务器地址
        // http://118.25.102.226:11001/apiLogin
        // 外网测试MQTT地址
        // 地址：47.96.95.4  
        // 端口：1883
        // 用户名密码:test/test
        // 主题：pad
        // "tcp://" + "47.96.95.4" + ":1883"
        // todo 外网测试环境
/*        private const val URL_HEAD = "http://"
        private const val URL_COLON = ":"
        private const val DEFAULT_URL = "118.25.102.226"
        private const val DEFAULT_PORT = 11001
        private const val DEFAULT_MQTT_HEAD = "tcp://"
        private const val DEFAULT_MQTT_IP = "47.96.95.4"
        private const val DEFAULT_MQTT_COLON = ":"
        private const val DEFAULT_MQTT_PORT = 1883*/

        // 国网智芯内网服务器地址,设置为默认地址
        // centos:7.6.1810
        // 10.238.211.2:11001     root ：123!@#qwe
        // redis密码：wbl20201117
        // mysql密码：wbl2020@123
        // 安防ak/sk:
        // host: 10.238.211.1
        // appKey: 29019157
        // appSecret: 5GPKANd87cMqtkWtzKxn
        // http://10.238.211.2:11001/apiLogin
        // 国网智芯内网MQTT服务器地址,设置为默认地址
        // 地址：10.238.211.2
        // 端口：1883
        // 用户名密码:test/test
        // 主题：pad
        // "tcp://" + "10.238.211.2" + ":1883"
        // todo 国网智芯内网环境
        private const val URL_HEAD = "http://"
        private const val URL_COLON = ":"
        private const val DEFAULT_URL = "10.238.211.2"
        private const val DEFAULT_PORT = 11001
        private const val DEFAULT_MQTT_HEAD = "tcp://"
        private const val DEFAULT_MQTT_IP = "10.238.211.2"
        private const val DEFAULT_MQTT_COLON = ":"
        private const val DEFAULT_MQTT_PORT = 1883

        // 登录 post
        private const val CLIENT_LOGIN = "/apiLogin"

        // 21. 根据卡号直接登录
        // 接口地址：  Post    /loginByCardNo
        private const val loginByCardNo = "/loginByCardNo"

        // 21. 根据身份证号码直接登录
        // 接口地址：  Post    /loginByIdCard
        private const val loginByIdCard = "/loginByIdCard"

        // 档案搜索(免登录) get
        private const val searchArchivesInfo = "/api/pad/searchArchivesInfo"

        // 获取所有库房(免登录) get
        private const val GetHouseList = "/api/pad/getHouseList"

        // 根据库房id获取当前温湿度
        // /statistics/getHumitureByHouseId?houserId=
        private const val getHumitureByHouseId = "/statistics/getHumitureByHouseId"

        // 2. 亮灯
        // 接口地址：  Post    /api/pad/lightUp
        // 平台收到请求后，通过MQTT下发亮灯指令；
        private const val lightUp = "/api/pad/lightUp"

        // 14.根据操作屏设备id获取操作屏及其档案柜信息
        // 接口地址：  get /api/pad/getCabineMasterByEquipmentId
        private const val getCabineMasterByEquipmentId = "/api/pad/getCabineMasterByEquipmentId"

        // 17.库存信息
        // 如果equipmentId为空，返回整个库房的库存信息；
        // 如果equipmentId不为空，返回该操作屏对应的所有档案柜的累加库存信息；
        // 接口地址：  get /api/pad/getCapital/{houseCode}/{equipmentId}
        private const val getCapital = "/api/pad/getCapital"

        // 26.根据一体机设备id获取一体机信息
        // 接口地址：  get /api/pad/getAIOInfoByEquipmentId
        private const val getAIOInfoByEquipmentId = "/api/pad/getAIOInfoByEquipmentId"

        // 25.根据档案柜设备id获取库位信息
        // 接口地址：  get /api/pad/getPosInfoByCabinetEquipmentId
        private const val getPosInfoByCabinetEquipmentId = "/api/pad/getPosInfoByCabinetEquipmentId"

        // 根据RFID数组获取档案信息 get
        private const val getArchivesInfoByRFID = "/api/pad/getArchivesInfoByRFID"

        // 档案库位绑定（入库） post
        private const val PosBind = "/api/pad/posBind"

        // 获取未盘库的盘库计划信息 get
        private const val getNoStartInventoryPlan = "/api/pad/getNoStartInventoryPlan"

        // 根据档案柜提交盘库结果（普通柜） post
        private const val submitInventoryResult = "/api/pad/submitInventoryResult"

        // 推送盘库报告（智能柜）
        // 接口地址：Post /api/pad/submitIntInventoryResult
        private const val submitIntInventoryResult = "/api/pad/submitIntInventoryResult"

        // 获取已生成盘库结果的盘库计划信息
        private const val getDoneInventoryPlan = "/api/pad/getDoneInventoryPlan"

        // 获取当前登陆人待借的档案信息
        private const val getToBorrowList = "/api/pad/getToBorrowList"

        // 批量档案取件(借阅)
        private const val archivesBorrow = "/api/pad/archivesBorrow"

        // 批量档案归还
        private const val archivesReturn = "/api/pad/archivesReturn"

        // 获取当前登陆人待还的档案信息
        private const val getToReturnList = "/api/pad/getToReturnList"

        // 获取当前版本信息
        private const val getLastInfo = "/busi/otherAppupdate/getLastInfo"

        // 获取档案类型
        private const val getAllCodeType = "/api/pad/getAllCodeType"

        // 29.根据库位编号获取绑定的档案盒信息
        // 接口地址：  get   /api/pad/getBoxListByPosCode
        // 获取档案类型
        private const val getBoxListByPosCode = "/api/pad/getBoxListByPosCode"

        // 27.档案盒绑定库位
        // 接口地址：  post   /busi/baseArchivesBox/bind
        private const val bind = "/busi/baseArchivesBox/bind"

        // 28.档案盒解绑库位
        // 接口地址：  post   /busi/baseArchivesBox/unBind
        private const val unBind = "/busi/baseArchivesBox/unBind"

        // 18. 根据盘库计划id获取盘库差异信息
        // 接口地址：  get /api/pad/getInventoryDifferenceByPlanId
        private const val getInventoryDifferenceByPlanId = "/api/pad/getInventoryDifferenceByPlanId"

        val instance: NetworkRequest by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            NetworkRequest()
        }
    }

    override fun init(context: Context) {
        super.init(context)

        val url: String =
            SharedPreferencesUtil.instance.getString(Key.WebApiServiceIp, DEFAULT_URL)!!
        if (url == DEFAULT_URL) {
            SharedPreferencesUtil.instance.applyValue(
                SharedPreferencesUtil.Record(
                    Key.WebApiServiceIp,
                    DEFAULT_URL
                )
            )
        }

        val port: Int = SharedPreferencesUtil.instance.getInt(Key.WebApiServicePort, DEFAULT_PORT)
        if (port == DEFAULT_PORT) {
            SharedPreferencesUtil.instance.applyValue(
                SharedPreferencesUtil.Record(
                    Key.WebApiServicePort,
                    DEFAULT_PORT
                )
            )
        }
        configModify(url, port)

        val mqttIP: String =
            SharedPreferencesUtil.instance.getString(Key.MQTTServiceIp, DEFAULT_MQTT_IP)!!
        if (mqttIP == DEFAULT_MQTT_IP) {
            SharedPreferencesUtil.instance.applyValue(
                SharedPreferencesUtil.Record(
                    Key.MQTTServiceIp,
                    DEFAULT_MQTT_IP
                )
            )
        }
        val mqttPort: Int =
            SharedPreferencesUtil.instance.getInt(Key.MQTTServicePort, DEFAULT_MQTT_PORT)
        if (mqttPort == DEFAULT_MQTT_PORT) {
            SharedPreferencesUtil.instance.applyValue(
                SharedPreferencesUtil.Record(
                    Key.MQTTServicePort,
                    DEFAULT_MQTT_PORT
                )
            )
        }
        mMQTTUrl = DEFAULT_MQTT_HEAD + mqttIP + DEFAULT_MQTT_COLON + mqttPort
    }

    fun configModify(url: String, port: Int) {
        mClientLogin = URL_HEAD + url + URL_COLON + port + CLIENT_LOGIN
        mLoginByCardNo = URL_HEAD + url + URL_COLON + port + loginByCardNo
        mLoginByIdCard = URL_HEAD + url + URL_COLON + port + loginByIdCard
        mSearchArchivesInfo = URL_HEAD + url + URL_COLON + port + searchArchivesInfo
        mGetHumitureByHouseId = URL_HEAD + url + URL_COLON + port + getHumitureByHouseId
        mLightUp = URL_HEAD + url + URL_COLON + port + lightUp
        mGetHouseList = URL_HEAD + url + URL_COLON + port + GetHouseList
        mGetArchivesInfoByRFID = URL_HEAD + url + URL_COLON + port + getArchivesInfoByRFID
        mPosBind = URL_HEAD + url + URL_COLON + port + PosBind
        mGetNoStartInventoryPlan = URL_HEAD + url + URL_COLON + port + getNoStartInventoryPlan
        mSubmitInventoryResult = URL_HEAD + url + URL_COLON + port + submitInventoryResult
        mSubmitIntInventoryResult = URL_HEAD + url + URL_COLON + port + submitIntInventoryResult
        metDoneInventoryPlan = URL_HEAD + url + URL_COLON + port + getDoneInventoryPlan
        mGetToBorrowList = URL_HEAD + url + URL_COLON + port + getToBorrowList
        mArchivesBorrow = URL_HEAD + url + URL_COLON + port + archivesBorrow
        mArchivesReturn = URL_HEAD + url + URL_COLON + port + archivesReturn
        mGetToReturnList = URL_HEAD + url + URL_COLON + port + getToReturnList
        mGetLastInfo = URL_HEAD + url + URL_COLON + port + getLastInfo
        mGetCabineMasterByEquipmentId =
            URL_HEAD + url + URL_COLON + port + getCabineMasterByEquipmentId
        mGetCapital = URL_HEAD + url + URL_COLON + port + getCapital
        mGetAIOInfoByEquipmentId = URL_HEAD + url + URL_COLON + port + getAIOInfoByEquipmentId
        mGetPosInfoByCabinetEquipmentId =
            URL_HEAD + url + URL_COLON + port + getPosInfoByCabinetEquipmentId
        mGetAllCodeType = URL_HEAD + url + URL_COLON + port + getAllCodeType
        mGetBoxListByPosCode = URL_HEAD + url + URL_COLON + port + getBoxListByPosCode
        mBind = URL_HEAD + url + URL_COLON + port + bind
        mUnBind = URL_HEAD + url + URL_COLON + port + unBind
        mGetInventoryDifferenceByPlanId =
            URL_HEAD + url + URL_COLON + port + getInventoryDifferenceByPlanId
    }

    fun configModifyMQTT(mqttIP: String, mqttPort: Int) {
        mMQTTUrl = DEFAULT_MQTT_HEAD + mqttIP + DEFAULT_MQTT_COLON + mqttPort
        MQTTHelper.getInstance().closeMQTT()
        MQTTHelper.getInstance().reConnectMQTT()
    }

    interface ResponseListener {
        fun onSuccess()
        fun onError(resultCode: Int, resultMessage: String?)
    }

}