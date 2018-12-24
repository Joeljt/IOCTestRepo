package com.ljt.ioctest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.ljt.ioc.IOCUtil;
import com.ljt.ioc.Unbinder;
import com.ljt.ioc_annotatation.BindView;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv_test1)
    TextView textView1;

    @BindView(R.id.tv_test2)
    TextView textView2;

    @BindView(R.id.tv_test3)
    TextView textView3;

    private Unbinder mUnbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUnbinder = IOCUtil.bind(this);

        textView1.setText("this is a textView1");
        textView2.setText("this is a textView2");
        textView3.setText("this is a textView3");

    }

    @Override
    protected void onDestroy() {
        if (mUnbinder != Unbinder.EMPTY) {
            mUnbinder.unbind();
        }
        super.onDestroy();
    }
}
