package com.kevinrothenberger.lab5;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;


public class MyActivity extends Activity {

    EditText web_url;
    Button load_button;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String address = msg.getData().getString("address");
                String page = msg.getData().getString("page");
                //webView.loadData(page, "text/html", "UTF-8");
                webView.loadDataWithBaseURL(address, page, "text/html", "UTF-8", null);
            }
        };

        web_url = (EditText) findViewById(R.id.web_address);
        load_button = (Button) findViewById(R.id.load_button);
        webView = (WebView) findViewById(R.id.web_page);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        load_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String url_text = web_url.getText().toString();
                            if(!url_text.contains("http://")) {
                                url_text = "http://" + url_text;
                            }
                            URL url = new URL(url_text);
                            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
                            String line;
                            StringBuilder page = new StringBuilder();
                            while ((line = in.readLine()) != null)
                            {
                                page.append(line);
                                page.append("\n");
                            }
                            in.close();

                            Message msg = handler.obtainMessage();
                            Bundle bundle = new Bundle();
                            bundle.putString("address", url_text);
                            bundle.putString("page", page.toString());
                            msg.setData(bundle);
                            handler.sendMessage(msg);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                Thread thread = new Thread(runnable);
                thread.start();
            }
        });
    }

}
