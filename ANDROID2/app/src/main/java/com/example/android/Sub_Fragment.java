package com.example.android;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

//Sub_Fragment: 적외선 카메라 화면 구현
public class Sub_Fragment extends Fragment {
    WebView webView;
    WebSettings webSettings;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fragment_sub,container,false);
        webView=(WebView)view.findViewById(R.id.light);
        webSettings=webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //적외선 카메라 웹뷰를 load한다.
        webView.loadData("<html><head><style type='text/css'>body{margin:auto auto;text-align:center;}"+
                        "img{width:100%25;}div{overflow:hidden;}</style></head>"+
                        "<body><div><img src='http://192.168.0.207:8092/?action=stream_1/'/></div></body></html>",
                "text/html","UTF-8");
        return view;
    }
}
