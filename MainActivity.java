package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Context context = this;
    public List<String> lItem = null; //파일이나 폴더명
    public List<String> lPath = null; //현재 경로에 있는 아이템의 경로
    public ArrayAdapter<String> arrayAdapter;
    public TextView tvPath;
    public ListView lvItem;

    public Button btnAddLink;
    public Button btnAddFolder;
    public Button btnUpdate;
    public Button btnDelete;

    public String appName = "허허실실";
    public String homePath;
    public static String currentPath ;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.GREEN)); // 액션바 배경색 변경
        getSupportActionBar().setTitle("허허실실");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home_black_24dp); // 홈 아이콘


        tvPath = (TextView) findViewById(R.id.tvPath);
        lvItem = (ListView)findViewById(R.id.lvFileControl);
        btnAddLink = (Button)findViewById(R.id.btn_add_link);
        btnAddFolder = (Button)findViewById(R.id.btn_add_folder);
        btnUpdate = (Button)findViewById(R.id.btn_update);
        btnDelete = (Button)findViewById(R.id.btn_delete);

        String Root = Environment.getExternalStorageDirectory().getAbsolutePath();
        homePath = init(Root, appName);

        openFolder(currentPath = homePath);

        btnAddLink.setOnClickListener(this);
        btnAddFolder.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);

        lvItem.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (AppStatus.selectMode == true) return;

                File file = new File(lPath.get(position));

                if (file.isDirectory()) { //파일이 폴더이면
                    if (file.canRead())
                        openFolder(currentPath = lPath.get(position));
                    else
                        Toast.makeText(context, "폴더를 열 수 없음", Toast.LENGTH_SHORT).show();

                } else { //파일이 폴더가 아니면
                    if (file.canRead())
                        openLink(currentPath = lPath.get(position));
                    else
                        Toast.makeText(context, "파일을 열 수 없음", Toast.LENGTH_SHORT).show();
                }
            }
        });

    } // end of onCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                currentPath = homePath;
                openFolder(currentPath);
                return true;

            case R.id.btn_select_item:
                AppStatus.selectMode = true;
                SparseBooleanArray checkedItems = lvItem.getCheckedItemPositions();
                arrayAdapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_multiple_choice, lItem);
                lvItem.setAdapter(arrayAdapter);

                return true;
        }

        return (super.onOptionsItemSelected(item));
    }

    @Override
    public void onClick(View view){
        Intent intent;

        switch (view.getId()){

            case R.id.btn_add_link:
                intent = new Intent(context, AddLinkActivity.class);
                startActivityForResult(intent, 1);

                break;

            case R.id.btn_add_folder:
                intent = new Intent(context, AddFolderActivity.class);
                startActivityForResult(intent, 2);

                break;

            case R.id.btn_delete:
                if (AppStatus.selectMode == true){
                    intent = new Intent(context, DeleteFileActivity.class);
                    startActivityForResult(intent, 3);
                }

                break;

            case R.id.btn_update:

                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK){
                Toast.makeText(this,"링크 추가",Toast.LENGTH_SHORT).show();
                openFolder(currentPath);
            }
            return;
        }else if (requestCode == 2){
            if (resultCode == RESULT_OK){
                Toast.makeText(this,"폴더 추가",Toast.LENGTH_SHORT).show();
                openFolder(currentPath);
            }
            return;
        }else if (requestCode == 3){
            if (resultCode == RESULT_OK){
                Toast.makeText(this,"링크/폴더 삭제",Toast.LENGTH_SHORT).show();
                deleteCheckedFile(currentPath);
                openFolder(currentPath);
            }
            AppStatus.selectMode = false;
            return;

        }
    }

    public void openLink(String Path){
        File f = new File(Path) ;
        FileReader fr = null ;
        StringBuilder sb = new StringBuilder();
        int data ;
        char ch ;

        try {
            fr = new FileReader(f) ;
            while ((data = fr.read()) != -1) {
                ch = (char) data ;
                sb.append(ch);
            }
            fr.close() ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        String url = null;

        if(sb.substring(0,4) != "http")
            url = "http://" + sb.toString();
        else
            url = sb.toString();

        Toast.makeText(context,url,Toast.LENGTH_SHORT).show();

        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        startActivity(intent);
        //if (intent.resolveActivity(getPackageManager()) != null) {
         //   startActivity(intent);
        //}
    } //end of openLink

    public void openFolder(String Path) {
        tvPath.setText("Location: " + Path);

        lItem = new ArrayList<String>();
        lPath = new ArrayList<String>();

        File f = new File(Path);
        File[] files = f.listFiles();

        if (!homePath.equals(Path)) {
            lItem.add("이전 폴더로 가기.."); //to parent folder
            lPath.add(f.getParent());
        }

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            lPath.add(file.getAbsolutePath());

            if (file.isDirectory())
                lItem.add(file.getName() + "/");
            else
                lItem.add(file.getName());
        }

        arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, lItem);
        lvItem.setAdapter(arrayAdapter);
    } // end of openFolder

    public void deleteCheckedFile(String Path) {
        SparseBooleanArray checkedItems = lvItem.getCheckedItemPositions();

        int count = arrayAdapter.getCount();
        for (int i = 0; i < count; i++) {
            if (checkedItems.get(i)) {

                File f = new File(lPath.get(i).toString());
                if(f.exists()){
                    f.delete();
                }
                //lPath.remove(i);
                //lItem.remove(i);
            }
        }
        // 모든 선택 상태 초기화
        lvItem.clearChoices() ;
        //arrayAdapter.notifyDataSetChanged();
    }

    public String init (String path, String appName){
        File f = new File(path, appName);
        f.mkdir();
        return path +"/"+ appName;
    }

    //뒤로가기 버튼
    @Override
    public void onBackPressed() {

        if (AppStatus.selectMode == true) {
            AppStatus.selectMode = false;
            openFolder(currentPath);
        }
        else if(!(currentPath.equals(homePath))) {
            currentPath = new File(currentPath).getParent();
            openFolder(currentPath);
        }
        else {
            super.onBackPressed();
        }


  }

} // end

