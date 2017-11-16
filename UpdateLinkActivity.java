package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.FileWriter;

/**
 * Created by 성욱 on 2017-11-05.
 */

public class UpdateLinkActivity extends Activity {

    Button btnUpdateLinkOk;
    Button btnUpdateLinkCancel;
    EditText editText1;
    EditText editText2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        setContentView(R.layout.activity_update_link);

        editText1 = (EditText)findViewById(R.id.edit_update_link_name);
        editText2 = (EditText)findViewById(R.id.edit_update_link_url);
        btnUpdateLinkOk = (Button)findViewById(R.id.btn_update_link_ok);
        btnUpdateLinkCancel = (Button)findViewById(R.id.btn_update_link_cancel);

        Intent intent = new Intent();

        editText1.setText(intent.getStringExtra("name"));
        editText2.setText(intent.getStringExtra("path"));

        btnUpdateLinkCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });

        btnUpdateLinkOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File f = new File(MainActivity.currentPath + "/" + editText1.getText().toString() + ".txt") ;
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

                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    } // end of onCreate
}
