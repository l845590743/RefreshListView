package com.lzm.refreshlistview;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by lzm
 * 2017/12/1
 */
public class WebViewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        final RefreshWebView refreshWebView = (RefreshWebView) findViewById(R.id.refresh_web);
        WebView webView = refreshWebView.getWebView();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://www.baidu.com");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        refreshWebView.setOnRefreshListener(new RefreshWebView.OnRefreshListener() {
            @Override
            public void LoadingData() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("正在刷新");
                        refreshWebView.onFinsh();
                    }
                }, 2000);
            }
        });
    }
}
