package testb.hello.pic;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements PasswordInputListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GesTurePwdView view = (GesTurePwdView) findViewById(R.id.pwdview);
        view.setPwdListener(this);
        findViewById(R.id.nini).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,LockAppActivity.class));
            }
        });
    }

    @Override
    public void setPassword(ArrayList<Integer> pwdContainer) {
        if(pwdContainer == null) return;
        for(int i=0;i<pwdContainer.size();i++){
            Log.d("Gesturepwd","----:"+pwdContainer.get(i));
        }
    }
}
