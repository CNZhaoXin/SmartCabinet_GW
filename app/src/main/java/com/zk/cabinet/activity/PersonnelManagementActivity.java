package com.zk.cabinet.activity;

import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import androidx.databinding.DataBindingUtil;

import com.zk.cabinet.R;
import com.zk.cabinet.adapter.UserAdapter;
import com.zk.cabinet.base.TimeOffAppCompatActivity;
import com.zk.cabinet.bean.User;
import com.zk.cabinet.callback.FingerprintListener;
import com.zk.cabinet.databinding.ActivityPersonnelManagementBinding;
import com.zk.cabinet.databinding.DialogFingerBinding;
import com.zk.cabinet.db.UserService;
import com.zk.cabinet.utils.FingerprintParsingLibrary;
import com.zk.cabinet.utils.SharedPreferencesUtil;
import com.zk.common.utils.TimeUtil;

import java.lang.ref.WeakReference;
import java.util.List;

public class PersonnelManagementActivity extends TimeOffAppCompatActivity {
    private final static int FINGERPRINT = 0x00;
    private ActivityPersonnelManagementBinding binding;

    private List<User> list;
    private UserAdapter mAdapter;
    private int mPosition;
    private AlertDialog mFingerDialog;
    private MHandler mHandler;
    private User curUser;
    boolean isCanInputFingerprint;

    private void handleMessage(Message msg) {
        switch (msg.what) {
            case FINGERPRINT:
//                if (mPosition != -1) {
//                    list.get(mPosition).setFingerPrint((byte[]) msg.obj);
//                    list.get(mPosition).setModifyTime(TimeUtil.INSTANCE.nowTimeOfSeconds());
//                    UserService.getInstance().update(list.get(mPosition));
//                    showToast(list.get(mPosition).getUserName() + " ,您的指纹已录入！");
//                    mPosition = -1;
//                    if (mFingerDialog != null && mFingerDialog.isShowing()) mFingerDialog.dismiss();
//                    mAdapter.notifyDataSetChanged();
//                    FingerprintParsingLibrary.getInstance().upUserList();
//                }

                if (isCanInputFingerprint) {
                    curUser.setFingerPrint((byte[]) msg.obj);
                    curUser.setModifyTime(TimeUtil.INSTANCE.nowTimeOfSeconds());
                    UserService.getInstance().update(curUser);
                    isCanInputFingerprint = false;
                    if (mFingerDialog != null && mFingerDialog.isShowing()) mFingerDialog.dismiss();
                    FingerprintParsingLibrary.getInstance().upUserList();

                    binding.tvFingerprint.setText("已录入");
                    binding.tvFingerprint.setTextColor(getResources().getColor(R.color.md_teal_A400));
                    showToast(getMSpUtil().getString(SharedPreferencesUtil.Key.NameTemp, "") + " , 您的指纹已成功录入！");
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_personnel_management);
        mHandler = new MHandler(this);

        String name = getMSpUtil().getString(SharedPreferencesUtil.Key.NameTemp, "xxx");
        binding.tvOperator.setText(name);

        init();
    }

//    private void init() {
//        list = UserService.getInstance().loadAll();
//        mAdapter = new UserAdapter(this, list);
//        binding.listView.setAdapter(mAdapter);
//        binding.listView.setOnItemClickListener(onItemClickListener);
//        FingerprintParsingLibrary.getInstance().onFingerprintListener(fingerprintListener);
//    }

    private void init() {
        binding.tvLoginCode.setText(getMSpUtil().getString(SharedPreferencesUtil.Key.LoginCodeTemp, ""));
        binding.tvUserName.setText(getMSpUtil().getString(SharedPreferencesUtil.Key.NameTemp, ""));
        binding.tvRoleName.setText(getMSpUtil().getString(SharedPreferencesUtil.Key.RoleNameTemp, ""));
        binding.tvOrgName.setText(getMSpUtil().getString(SharedPreferencesUtil.Key.OrgNameTemp, ""));

//        recordList.add(Record(Key.LoginCodeTemp, user.userCode)) // zx
//        recordList.add(Record(Key.NameTemp, user.userName)) // 赵鑫
//        recordList.add(Record(Key.RoleNameTemp, user.modifyTime)) // 普通员工

        String loginCodeTemp = getMSpUtil().getString(SharedPreferencesUtil.Key.LoginCodeTemp, "");
        curUser = UserService.getInstance().queryByUserCode(loginCodeTemp);

        if (curUser.getFingerPrint() == null) {
            binding.tvFingerprint.setText("未录入");
            binding.tvFingerprint.setTextColor(getResources().getColor(R.color.md_red_A200));
            binding.btnInputFingerprint.setText("录 入");
        } else {
            binding.tvFingerprint.setText("已录入");
            binding.tvFingerprint.setTextColor(getResources().getColor(R.color.md_teal_A400));
            binding.btnInputFingerprint.setText("重 录");
        }

        binding.btnInputFingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFingerDialog == null) {
                    DialogFingerBinding dialogFingerBinding = DataBindingUtil.inflate(LayoutInflater.from(PersonnelManagementActivity.this), R.layout.dialog_finger, null, false);
                    dialogFingerBinding.btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mFingerDialog.dismiss();
                            isCanInputFingerprint = false;
                        }
                    });

                    mFingerDialog = new AlertDialog.Builder(PersonnelManagementActivity.this)
                            .setCancelable(false)
                            .setView(dialogFingerBinding.getRoot())
                            .create();

                    Window window = mFingerDialog.getWindow();
                    assert window != null;
                    window.setBackgroundDrawable(new ColorDrawable(0));
                }
                mFingerDialog.show();

                isCanInputFingerprint = true;
                FingerprintParsingLibrary.getInstance().onFingerprintListener(fingerprintListener);
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    protected void countDownTimerOnTick(long millisUntilFinished) {
        binding.tvCountdown.setText(String.valueOf(millisUntilFinished));
    }

    @Override
    protected void onDestroy() {
        FingerprintParsingLibrary.getInstance().onFingerprintListener(null);
        super.onDestroy();
    }

//    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            mPosition = position;
//
//            if (mFingerDialog == null) {
//                DialogFingerBinding dialogFingerBinding = DataBindingUtil.inflate(LayoutInflater.from(PersonnelManagementActivity.this), R.layout.dialog_finger, null, false);
//                dialogFingerBinding.btnCancel.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        mPosition = -1;
//                        mFingerDialog.dismiss();
//                    }
//                });
//
//                mFingerDialog = new AlertDialog.Builder(PersonnelManagementActivity.this)
//                        .setCancelable(false)
//                        .setView(dialogFingerBinding.getRoot())
//                        .create();
//
//                Window window = mFingerDialog.getWindow();
//                assert window != null;
//                window.setBackgroundDrawable(new ColorDrawable(0));
//            }
//            mFingerDialog.show();
//        }
//    };

    private FingerprintListener fingerprintListener = new FingerprintListener() {
        @Override
        public void fingerprint(byte[] fingerprint) {
            if (isCanInputFingerprint) {
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
