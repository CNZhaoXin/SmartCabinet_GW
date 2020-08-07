package com.zk.cabinet.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import androidx.databinding.DataBindingUtil;

import com.zk.cabinet.R;
import com.zk.cabinet.adapter.UserAdapter;
import com.zk.cabinet.base.TimeOffAppCompatActivity;
import com.zk.cabinet.bean.User;
import com.zk.cabinet.callback.FingerprintListener;
import com.zk.cabinet.databinding.ActivityPersonnelManagementBinding;
import com.zk.cabinet.db.UserService;
import com.zk.cabinet.utils.FingerprintParsingLibrary;
import com.zk.common.utils.TimeUtil;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;

public class PersonnelManagementActivity extends TimeOffAppCompatActivity {
    private final static int FINGERPRINT = 0x00;
    private final static int UP_FINGERPRINT_SUCCESS = 0x01;
    private final static int UP_FINGERPRINT_ERROR = 0x02;
    private ActivityPersonnelManagementBinding binding;

    private List<User> list;
    private UserAdapter mAdapter;

    private View mDialogView;
    private ProgressDialog fingerDialog;
    private int mPosition;
    private ProgressDialog progressDialog;

    private MHandler mHandler;
    private void handleMessage(Message msg) {
        switch (msg.what) {
            case FINGERPRINT:
                if (mPosition != -1) {
                    list.get(mPosition).setFingerPrint((byte[]) msg.obj);
                    list.get(mPosition).setModifyTime(TimeUtil.INSTANCE.nowTimeOfSeconds());
                    UserService.getInstance().update(list.get(mPosition));
                    FingerprintParsingLibrary.getInstance().upUserList();
                    showToast(list.get(mPosition).getUserName() + "您的指纹已录入！");
                    mPosition = -1;
                    mAdapter.notifyDataSetChanged();
                }
                break;
            case UP_FINGERPRINT_SUCCESS:
            case UP_FINGERPRINT_ERROR:
                progressDialog.dismiss();
                showToast(msg.obj.toString());
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_personnel_management);
        setSupportActionBar(binding.personalManagementToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        mHandler = new MHandler(this);
        init();
    }

    private void init(){
        progressDialog = new ProgressDialog(this);
        list = UserService.getInstance().loadAll();
        mAdapter = new UserAdapter(this, list);
        binding.personalManagementQueryLv.setAdapter(mAdapter);
        binding.personalManagementQueryLv.setOnItemClickListener(onItemClickListener);
        FingerprintParsingLibrary.getInstance().onFingerprintListener(fingerprintListener);
    }


    protected void countDownTimerOnTick(long millisUntilFinished){
        binding.personalManagementCountdownTv.setText(String.valueOf(millisUntilFinished));
    }

    @Override
    protected void onDestroy() {
        FingerprintParsingLibrary.getInstance().onFingerprintListener(null);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mPosition = position;
            if (fingerDialog == null){
                fingerDialog = new ProgressDialog(PersonnelManagementActivity.this);
                fingerDialog.setTitle("指纹录入");
                fingerDialog.setCancelable(false);
                fingerDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPosition = -1;
                        dialog.dismiss();
                    }
                });
            }
            fingerDialog.setMessage(list.get(position).getUserName() + "请把手指放在指纹传感器上");
            fingerDialog.show();
        }
    };

    private FingerprintListener fingerprintListener = new FingerprintListener() {
        @Override
        public void fingerprint(byte[] fingerprint) {
            if (mPosition != -1) {
                Message msg = Message.obtain();
                msg.what = FINGERPRINT;
                msg.obj = fingerprint;
                mHandler.sendMessage(msg);
            }
        }
    };

    private static class MHandler extends Handler {
        private final WeakReference<PersonnelManagementActivity> personnelManagementActivityWeakReference;

        MHandler(PersonnelManagementActivity personnelManagementActivity) {
            super();
            personnelManagementActivityWeakReference = new WeakReference<>(personnelManagementActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (personnelManagementActivityWeakReference.get() != null) {
                personnelManagementActivityWeakReference.get().handleMessage(msg);
            }
        }
    }
}
