package com.gqq.speechdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechSynthesizer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

// 语音合成
public class SpeechTranActivity extends AppCompatActivity {

    @BindView(R.id.tvDocum)
    TextView mTvDocum;

    // 语音合成对象
    private SpeechSynthesizer mTts;

    // 默认发音人
    private String voicer = "xiaoyan";

    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_tran);
        ButterKnife.bind(this);

        // 初始化合成对象
        mTts = SpeechSynthesizer.createSynthesizer(this, mTtsInitListener);


    }

    /**
     * 初始化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d("TAG", "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                Toast.makeText(SpeechTranActivity.this, "初始化失败,错误码："+code, Toast.LENGTH_SHORT).show();;
            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };

    @OnClick({R.id.btnTran, R.id.btnStop})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnTran:

                break;
            case R.id.btnStop:
                break;
        }
    }
}
