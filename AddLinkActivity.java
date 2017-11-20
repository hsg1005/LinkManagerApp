package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by 성욱 on 2017-11-05.
 */

public class AddLinkActivity extends Activity {
    Button btnAddLinkOk;
    Button btnAddLinkCancel;
    EditText editText1;
    EditText editText2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        setContentView(R.layout.activity_add_link);

        editText1 = (EditText)findViewById(R.id.edit_add_link_name);
        editText2 = (EditText)findViewById(R.id.edit_add_link_url);
        btnAddLinkOk = (Button)findViewById(R.id.btn_add_link_ok);
        btnAddLinkCancel = (Button)findViewById(R.id.btn_add_link_cancel);

        btnAddLinkCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = getIntent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });

        btnAddLinkOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File f = new File(MainActivity.currentPath + "/" + editText1.getText().toString() + "") ;
                FileWriter fw = null ;
                String text = editText2.getText().toString();

                try {
                    fw = new FileWriter(f) ;
                    fw.write(text) ;

                } catch (Exception e) {
                    e.printStackTrace() ;
                }

                if (fw != null) {
                    // catch Exception here or throw.
                    try {
                        fw.close() ;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                Intent returnIntent = getIntent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    } // end of onCreate
}
