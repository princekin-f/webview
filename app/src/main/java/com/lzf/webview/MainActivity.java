package com.lzf.webview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * @author liuzhenfeng
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "--->";
    private ProgressWebView mWebView;

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = findViewById(R.id.webview);

        // 加载本地asset下面的test.html文件
        mWebView.loadUrl("file:///android_asset/test.html");
        // 加载普通网页
//        mWebView.loadUrl("http://image.baidu.com/search/index?tn=baiduimage&ct=201326592&lm=-1&cl=2&ie=gbk&word=%CD%BC%C6%AC&fr=ala&ala=1&alatpl=others&pos=0");

        WebSettings webSettings = mWebView.getSettings();
        // 打开js支持
        webSettings.setJavaScriptEnabled(true);

        // 打开js接口給H5调用，参数1为本地类名，参数2为别名；h5用window.别名.类名里的方法名才能调用方法里面的内容，例如：window.android.back()
        mWebView.addJavascriptInterface(new JsInteration(), "android");
        // 帮助WebView处理各种通知、请求事件，不写html页面里的链接会跳到外部浏览器哦
        mWebView.setWebViewClient(new WebViewClient());
        // 辅助WebView处理js的对话框，网站图标，网站title，加载进度等，非必须（由于ProgressWebView重写了该方法，这里应该注销，否则自定义无效）
//        mWebView.setWebChromeClient(new WebChromeClient());

        // 缓存模式（方便测试加载进度条）
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
    }

    /**
     * 自己写一个类，里面是提供给H5访问的方法
     */
    public class JsInteration {
        /**
         * 一定要写，不然H5调不到这个方法
         */
        @JavascriptInterface
        public String back() {
            return "我是java方法的返回值";
        }

        @JavascriptInterface
        public void goNewAct(String str) {
            // 可接收js中的参数
            Log.e(TAG, "goNewAct: " + str);
            Toast.makeText(MainActivity.this, str, Toast.LENGTH_LONG).show();
            startActivity(new Intent(MainActivity.this, NewActivity.class));
        }
    }

    /**
     * 点击按钮，访问H5里带返回值的方法
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void onClick(View v) {
        Log.e(TAG, "onClick: ");

//        mWebView.loadUrl("http://image.baidu.com/search/index?tn=baiduimage&ct=201326592&lm=-1&cl=2&ie=gbk&word=%CD%BC%C6%AC&fr=ala&ala=1&alatpl=others&pos=0");

        // 直接访问H5里不带返回值的方法，show()为H5里的方法
        mWebView.loadUrl("JavaScript:show()");

        // 传固定字符串可以直接用单引号括起来
        // 访问H5里带参数的方法，alertMessage(message)为H5里的方法
        mWebView.loadUrl("javascript:alertMessage('哈哈')");

        // 当出入变量名时，需要用转义符隔开
        String content = "2333";
        mWebView.loadUrl("javascript:alertMessage(\"" + content + "\")");

        // Android调用有返回值js方法，安卓4.4以上才能用这个方法
        mWebView.evaluateJavascript("sum(1,2)", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                Log.e(TAG, "js返回的结果为 = " + value);
                Toast.makeText(MainActivity.this, "js返回的结果为 = " + value, Toast.LENGTH_LONG).show();
            }
        });
    }
}
