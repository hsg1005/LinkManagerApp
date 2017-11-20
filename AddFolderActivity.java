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

/**
 * Created by 성욱 on 2017-11-05.
 */

public class AddFolderActivity extends Activity {
    Button btnAddFolderOk;
    Button btnAddFolderCancel;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        setContentView(R.layout.activity_add_folder);

        editText = (EditText)findViewById(R.id.edit_add_folder);
        btnAddFolderOk = (Button)findViewById(R.id.btn_add_folder_ok);
        btnAddFolderCancel = (Button)findViewById(R.id.btn_add_folder_cancel);

        btnAddFolderCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = getIntent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });

        btnAddFolderOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File f = new File(MainActivity.currentPath, editText.getText().toString());
                f.mkdir();

                Intent returnIntent = getIntent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    } // end of onCreate
}
