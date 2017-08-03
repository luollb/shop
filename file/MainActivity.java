package com.xincai.retrofitdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "luo";
    private ImageView iv;
    private Button btn_create;
    private Button btn_writer;
    private Button btn_reader;
    private EditText et;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
      
        btn_create = (Button) findViewById(R.id.btn_create);
        btn_writer = (Button) findViewById(R.id.btn_writer);
        btn_reader = (Button) findViewById(R.id.btn_reader);
        et = (EditText) findViewById(R.id.et);
        tv = (TextView) findViewById(R.id.tv);

        btn_create.setOnClickListener(this);
        btn_reader.setOnClickListener(this);
        btn_writer.setOnClickListener(this);
    }

    String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test/luo/";
    String fileName = "luo.txt";

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_create: {
                FileUtils.makeFilePath(filePath, fileName);
            }
            break;
            case R.id.btn_writer: {
                FileUtils.writerFile(filePath, fileName, et.getText().toString(), true);
            }
            break;
            case R.id.btn_reader: {
                tv.setText(FileUtils.readerFile(filePath, fileName));
            }
            break;
        }
    }
}
