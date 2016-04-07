package testb.hello.pic;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LockAppActivity extends Activity implements OnClickListener, OnTouchListener {

    private ImageView ivLockAppIcon;
    private TextView tvLockAppName;
    private EditText etInputPwd;
    private Button btnConfirm;
    private String packageName;
    private String passWord;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.input_pwd);
        ivLockAppIcon = (ImageView) findViewById(R.id.iv_lock_app_icon);
        tvLockAppName = (TextView) findViewById(R.id.tv_lock_app_name);
        etInputPwd = (EditText) findViewById(R.id.et_lock_pwd);
        etInputPwd.setOnTouchListener(this);
        btnConfirm = (Button) findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(this);
        packageName = getIntent().getStringExtra("packageName");

        try {
            ApplicationInfo appInfo = getPackageManager().getPackageInfo(packageName, 0).applicationInfo;
            Drawable app_icon = appInfo.loadIcon(getPackageManager());
            String app_name = appInfo.loadLabel(getPackageManager()).toString();
            ivLockAppIcon.setImageDrawable(app_icon);
            tvLockAppName.setText(app_name);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //阻止事件继续向下分发
        if (KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        String input = etInputPwd.getText().toString().trim();
        preferences = getSharedPreferences("passWord", MODE_PRIVATE);
        passWord = preferences.getString("pwd", "");
        if (TextUtils.isEmpty(input)) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
        } else if (passWord.equals(input)) {
            finish();
        } else {
            Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show();
            etInputPwd.setText("");//置空
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        //这样是在触摸到控件时，软键盘才会显示出来
        int inputback = etInputPwd.getInputType();
        etInputPwd.setInputType(InputType.TYPE_NULL);
        new KeyboardUtil(this, this, etInputPwd).showKeyboard();
        etInputPwd.setInputType(inputback);
        return false;
    }
}