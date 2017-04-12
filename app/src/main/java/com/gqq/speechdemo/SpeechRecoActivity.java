package com.gqq.speechdemo;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SpeechRecoActivity extends AppCompatActivity {


    @BindView(R.id.tvShow)
    TextView mTvShow;
    @BindView(R.id.btnSpeech)
    Button mBtnSpeech;
    @BindView(R.id.btnOver)
    Button mBtnOver;

    // 语音听写对象
    private SpeechRecognizer mSpeechRecognizer;
    // 语音听写UI
    private RecognizerDialog mIatDialog;

    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_reco);
        ButterKnife.bind(this);

        //1.创建SpeechRecognizer对象，第二个参数：本地听写时传InitListener
        mSpeechRecognizer = SpeechRecognizer.createRecognizer(SpeechRecoActivity.this,initListener);

        // 语音听写对象
        mIatDialog = new RecognizerDialog(this,initListener);

        //2.设置听写参数，详见《科大讯飞MSC API手册(Android)》SpeechConstant类
        initParameter();

        //3.开始听写
//听写监听器
    }

    private InitListener initListener = new InitListener() {
        @Override
        public void onInit(int i) {

        }
    };

    private RecognizerListener mRecoListener = new RecognizerListener() {
        //听写结果回调接口(返回Json格式结果，用户可参见附录12.1)；
//一般情况下会通过onResults接口多次返回结果，完整的识别内容是多次结果的累加；
//关于解析Json的代码可参见MscDemo中JsonParser类；
//isLast等于true时会话结束。
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d("Result:", results.getResultString());
            printResult(results);
//           if (isLast){
//               String resultString = results.getResultString();
//               mTvShow.setText(resultString);
//           }
        }

        //会话发生错误回调接口
        public void onError(SpeechError error) {
            error.getPlainDescription(true);//获取错误码描述
        }

        // 声音变化
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {
//            Toast.makeText(SpeechRecoActivity.this, "当前正在说话，音量大小："+i, Toast.LENGTH_SHORT).show();
        }

        //开始录音
        public void onBeginOfSpeech() {
            Toast.makeText(SpeechRecoActivity.this, "开始说话", Toast.LENGTH_SHORT).show();
        }
        //音量值0~30

        //结束录音
        public void onEndOfSpeech() {
            mIatDialog.dismiss();
            Toast.makeText(SpeechRecoActivity.this, "录音结束", Toast.LENGTH_SHORT).show();
        }

        //扩展用接口
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

    public static String parseIatResult(String json) {
        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);

            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                // 转写结果词，默认使用第一个结果
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                JSONObject obj = items.getJSONObject(0);
                ret.append(obj.getString("w"));
//				如果需要多候选结果，解析数组其他字段
//				for(int j = 0; j < items.length(); j++)
//				{
//					JSONObject obj = items.getJSONObject(j);
//					ret.append(obj.getString("w"));
//				}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret.toString();
    }

    // 处理结果
    private void printResult(RecognizerResult results) {
        String text = parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }

        mTvShow.setText(resultBuffer.toString());
    }


    @OnClick({R.id.btnSpeech, R.id.btnOver})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSpeech:

//                mTvShow.setText("正在识别中~");
                initParameter();

                mIatDialog.show();

                // 开始听写：
                int ret = mSpeechRecognizer.startListening(mRecoListener);
                if (ret!=ErrorCode.SUCCESS){
                    Toast.makeText(this, "语音识别失败："+ret, Toast.LENGTH_SHORT).show();
            }

                break;
            case R.id.btnOver:

                mSpeechRecognizer.stopListening();

                break;
        }
    }

    // 设置听写参数
    private void initParameter() {
        // 清空参数
        mSpeechRecognizer.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mSpeechRecognizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置返回结果格式
        mSpeechRecognizer.setParameter(SpeechConstant.RESULT_TYPE, "json");

            // 设置语言
            mSpeechRecognizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mSpeechRecognizer.setParameter(SpeechConstant.VAD_BOS, "4000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mSpeechRecognizer.setParameter(SpeechConstant.VAD_EOS, "2000");

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mSpeechRecognizer.setParameter(SpeechConstant.ASR_PTT, "1");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
//        mSpeechRecognizer.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
//        mSpeechRecognizer.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/iat.wav");
    }
}
