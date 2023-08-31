package com.example.android;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

//Main_Fragment: 일반카메라 화면 구현
public class Main_Fragment extends Fragment {
    WebView webView;
    WebSettings webSettings;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fragment_main_,container,false);
        webView=(WebView)view.findViewById(R.id.general);
        webSettings=webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //일반 카메라 웹뷰를 load한다.
        webView.loadData("<html><head><style type='text/css'>body{margin:auto auto;text-align:center;}"+
                        "img{width:100%25;}div{overflow:hidden;}</style></head>"+
                        "<body><div><img src='http://192.168.0.207:8091/?action=stream_0/'/></div></body></html>",
                "text/html","UTF-8");
        return view;
    }
}