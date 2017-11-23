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

public class UpdateFolderActivity extends Activity {
    Button btnUpdateFolderOk;
    Button btnUpdateFolderCancel;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        setContentView(R.layout.activity_update_folder);

        editText = (EditText)findViewById(R.id.edit_update_folder);
        btnUpdateFolderOk = (Button)findViewById(R.id.btn_update_folder_ok);
        btnUpdateFolderCancel = (Button)findViewById(R.id.btn_update_folder_cancel);

        Intent intent = getIntent();
        final String beforeName = intent.getStringExtra("name");
        editText.setText(beforeName);

        btnUpdateFolderCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = getIntent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });

        btnUpdateFolderOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = getIntent();

                File f = new File(MainActivity.currentPath, beforeName);
                //파일 이름 변경
                f.renameTo(new File(MainActivity.currentPath, editText.getText().toString()));

                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    } // end of onCreate
}
