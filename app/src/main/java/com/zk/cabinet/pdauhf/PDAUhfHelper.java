package com.zk.cabinet.pdauhf;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

import com.blankj.utilcode.util.LogUtils;
import com.senter.support.openapi.StUhf;
import com.senter.support.openapi.StUhf.InterrogatorModel;
import com.zk.cabinet.R;

import java.util.HashMap;

public class PDAUhfHelper {
    private static PDAUhfHelper instance;
    private static StUhf uhf;
    private static Accompaniment mAccompaniment;
    private static Handler accompainimentsHandler;

    private final Runnable accompainimentRunnable = new Runnable() {
        @Override
        public void run() {
            mAccompaniment.start();
            accompainimentsHandler.removeCallbacks(this);
        }
    };

    public PDAUhfHelper() {
        mReceiveListenerList = new HashMap<>();
    }

    public static PDAUhfHelper getInstance() {
        if (instance == null)
            synchronized (PDAUhfHelper.class) {
                if (instance == null) {
                    instance = new PDAUhfHelper();
                }
            }

        return instance;
    }

    /**
     * 初始化音频
     *
     * @param context
     */
    public void initVoice(Context context) {
        mAccompaniment = Accompaniment.newInstanceOfResource(context, R.raw.tag_inventoried);
        HandlerThread htHandlerThread = new HandlerThread("");
        htHandlerThread.start();
        accompainimentsHandler = new Handler(htHandlerThread.getLooper());
    }

    public void getUhf(InterrogatorModel interrogatorModel) {
        if (uhf == null) {
            uhf = StUhf.getUhfInstance(interrogatorModel);
            uhfInterfaceAsModel = interrogatorModel;
        }
    }

    public HashMap<String, ReceiveListener> mReceiveListenerList;

    private ReceiveListener mReceiveListener;

    public interface ReceiveListener {
        void receiveData(String epc);
    }

    public void setReceiveListener( ReceiveListener receiveListener) {
        mReceiveListener= receiveListener;
    }

    public void setReceiveListener(String fragmentTag, ReceiveListener receiveListener) {
        mReceiveListenerList.put(fragmentTag, receiveListener);
    }

    public boolean isInit = false;

    public Boolean uhfInit() {
        if (isInit) {
            return true;
        } else {
            // ModelD2
            getUhf(InterrogatorModel.InterrogatorModelD2);

            if (uhf == null) {
                LogUtils.e("PDA:UHF初始化失败");
            }
            boolean inited = uhf.init();

            if (!inited) {
                LogUtils.e("PDA:UHF初始化失败");
                isInit = false;
                return false;
            }

            LogUtils.e("PDA:UHF初始化成功");
            isInit = true;
            return true;
        }
    }

    public static void uhfClear() {
        uhf = null;
        uhfInterfaceAsModel = null;
    }

    private static InterrogatorModel uhfInterfaceAsModel;

    public static StUhf.InterrogatorModel uhfInterfaceAsModel() {
        if (uhf == null || uhfInterfaceAsModel == null) {
            throw new IllegalStateException();
        }
        return uhfInterfaceAsModel;
    }

    public static StUhf.InterrogatorModelDs.InterrogatorModelD2 uhfInterfaceAsModelD2() {
        assetUhfInstanceObtained();
        assert (uhfInterfaceAsModel() == InterrogatorModel.InterrogatorModelD2);
        return uhf.getInterrogatorAs(StUhf.InterrogatorModelDs.InterrogatorModelD2.class);
    }

    private static void assetUhfInstanceObtained() {
        if (uhf == null || uhfInterfaceAsModel == null) {
            throw new IllegalStateException();
        }
    }

    /**
     * stop the operation excuting by module,three times if need.
     */
    public boolean stop() {
        if (uhf != null) {
            if (uhf.isFunctionSupported(com.senter.support.openapi.StUhf.Function.StopOperation)) {
                for (int i = 0; i < 3; i++) {
                    if (uhf.stopOperation()) {
                        return true;
                    }
                }
                return false;
            }
        }
        return true;
    }

    public void uhfUninit() {
        LogUtils.e("PDA:" + "uhfUninit()");
        if (uhf == null) {
            return;
        }
        LogUtils.e("PDA:" + "uhfUninit().uninit");
        uhf.uninit();
    }

    /**
     * 开启一次盘点
     */
    public void startInventoryOne() {
        PDAUhfHelper.uhfInterfaceAsModelD2().iso18k6cRealTimeInventory(1, new StUhf.InterrogatorModelDs.UmdOnIso18k6cRealTimeInventory() {
            @Override
            public void onFinishedWithError(StUhf.InterrogatorModelDs.UmdErrorCode error) {
                stop();
                if (error != null) {
                    LogUtils.e("盘点error:" + error);
                }
            }

            @Override
            public void onFinishedSuccessfully(Integer antennaId, int readRate, int totalRead) {
                LogUtils.e("盘点结束");
            }

            @Override
            public void onTagInventory(StUhf.UII uii, StUhf.InterrogatorModelDs.UmdFrequencyPoint frequencyPoint, Integer antennaId, StUhf.InterrogatorModelDs.UmdRssi rssi) {
                if (uii != null) {
                    callbackDataOne(uii);
                }
            }
        });
    }

    public boolean isStopInventory = false;

    /**
     * 开始一个循环盘点
     */
    public void startInventoryRepeat(String fragmentTag) {
        curFragmentTag = fragmentTag;

        PDAUhfHelper.uhfInterfaceAsModelD2().iso18k6cRealTimeInventory(1, new StUhf.InterrogatorModelDs.UmdOnIso18k6cRealTimeInventory() {
            @Override
            public void onFinishedWithError(StUhf.InterrogatorModelDs.UmdErrorCode error) {
                stop();
                if (error != null) {
                    LogUtils.e("盘点error:" + error);
                }
            }

            @Override
            public void onFinishedSuccessfully(Integer antennaId, int readRate, int totalRead) {
                LogUtils.e("盘点结束,接着盘点");
                if (!isStopInventory)
                    startInventoryRepeat(curFragmentTag);
            }

            @Override
            public void onTagInventory(StUhf.UII uii, StUhf.InterrogatorModelDs.UmdFrequencyPoint frequencyPoint, Integer antennaId, StUhf.InterrogatorModelDs.UmdRssi rssi) {
                if (uii != null) {
                    callbackData(uii);
                }
            }
        });
    }

    private void trigTagAccompainiment() {
        accompainimentsHandler.post(accompainimentRunnable);
    }

    public boolean isStopInventory() {
        return isStopInventory;
    }

    public void setStopInventory(boolean stopInventory) {
        isStopInventory = stopInventory;
    }

    private String curFragmentTag;

    /**
     * 回调数据
     *
     * @param uii
     */
    protected final void callbackData(StUhf.UII uii) {
        String data = DataTransfer.xGetString(uii.getBytes());
        StUhf.UII.EPC epc = uii.getEpc();
        String epcStr = DataTransfer.xGetString(epc.getBytes());
        LogUtils.e("RFID：Uii" + data + ", epc: " + epcStr);

        // 播放声音
        trigTagAccompainiment();

        mReceiveListenerList.get(curFragmentTag).receiveData(epcStr);
    }

    /**
     * 回调数据
     *
     * @param uii
     */
    protected final void callbackDataOne(StUhf.UII uii) {
        String data = DataTransfer.xGetString(uii.getBytes());
        StUhf.UII.EPC epc = uii.getEpc();
        String epcStr = DataTransfer.xGetString(epc.getBytes());
        LogUtils.e("RFID：Uii" + data + ", epc: " + epcStr);

        // 播放声音
        trigTagAccompainiment();

        if (mReceiveListener != null)
            mReceiveListener.receiveData(epcStr);
    }


    // 停止扫描+断电,音频播放停止
    public void release() {
        if (mReceiveListenerList != null) {
            mReceiveListenerList.clear();
        }

        isInit = false;

        boolean flag = stop();
        int i = flag ? 1 : 0;
        LogUtils.e("RFID：Stop" + i);
        uhfUninit();

        if (accompainimentsHandler != null) {
            accompainimentsHandler.getLooper().quit();
        }
        if (mAccompaniment != null)
            mAccompaniment.release();
    }
}
