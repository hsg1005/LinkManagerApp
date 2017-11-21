<<<<<<< HEAD:java/com/example/myapplication/MainActivity.java
package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
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
import java.io.FileWriter;
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
    public String homePath = "";
    public static String currentPath = "";

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
        homePath = init(Root, appName).toString();

        currentPath = homePath;
        openFolder(currentPath);

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
                    if (file.canRead()){
                        currentPath = lPath.get(position);
                        openFolder(currentPath);
                    }

                    else
                        Toast.makeText(context, "폴더를 열 수 없음", Toast.LENGTH_SHORT).show();

                } else { //파일이 폴더가 아니면
                    if (file.canRead())
                        openLink(lPath.get(position));
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
                AppStatus.selectMode = false;
                openFolder(currentPath);
                return true;

            case R.id.btn_select_item:
                if (AppStatus.selectMode == false){
                    AppStatus.selectMode = true;
                    arrayAdapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_multiple_choice, lItem);
                    lvItem.setAdapter(arrayAdapter);
                }else {
                    AppStatus.selectMode = false;
                    openFolder(currentPath);
                }
                return true;

            case R.id.btn_share_item:
                if (AppStatus.selectMode == true) {
                    String path = currentPath;
                    String output = "";

                    SparseBooleanArray checkedItems = lvItem.getCheckedItemPositions();
                    int count = arrayAdapter.getCount();
                    for (int i = 0; i < count; i++) {
                        if (checkedItems.get(i)) {

                            File f = new File(lPath.get(i).toString());
                            //파일이 존재하면
                            if(f.exists()){
                                //파일이 폴더이면
                               if (f.isDirectory())
                                   output += searchFolder(f);
                               //파일이 링크이면
                               else{
                                   output += f.getName().toString() + "\n";
                                   output += readLink(f.getAbsolutePath()).toString() + "\n";
                               }
                            }
                        }
                    }
                    Intent msg = new Intent(Intent.ACTION_SEND);

                    msg.addCategory(Intent.CATEGORY_DEFAULT);
                    msg.putExtra(Intent.EXTRA_SUBJECT, "");
                    msg.putExtra(Intent.EXTRA_TEXT, output);
                    msg.putExtra(Intent.EXTRA_TITLE, "공유");
                    msg.setType("text/plain");
                    startActivity(Intent.createChooser(msg, "공유하기"));

                }
                AppStatus.selectMode = false;
                openFolder(currentPath);

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
                if (AppStatus.selectMode == true){
                    int count = 0;
                    int key = 0;

                    for (int i = 0; i < lvItem.getCount(); i++){
                        if (lvItem.isItemChecked(i)){
                            count++;
                            key = i;
                        }
                    }


                    // 1개 이상을 선택한 경우
                    if(count > 1){
                        Toast.makeText(context,"한 개의 파일만 선택하세요.",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        int position = lvItem.getCheckedItemPosition();
                        File f = new File(lPath.get(key).toString());

                        if (f.isDirectory()){

                        }
                        else{
                            intent = new Intent(context, UpdateLinkActivity.class);

                            intent.putExtra("name",f.getName().toString());
                            intent.putExtra("url", readLink(f.getAbsolutePath()).toString());
                            startActivityForResult(intent,4);
                        }
                    }

                }

                break;
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK){
                Toast.makeText(context,"링크 추가",Toast.LENGTH_SHORT).show();
                openFolder(currentPath);
                return;
            }

        }else if (requestCode == 2){
            if (resultCode == Activity.RESULT_OK){
                Toast.makeText(context,"폴더 추가",Toast.LENGTH_SHORT).show();
                openFolder(currentPath);
                return;
            }

        }else if (requestCode == 3) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(context, "링크/폴더 삭제", Toast.LENGTH_SHORT).show();
                deleteCheckedFile(currentPath);
                lvItem.clearChoices();
                AppStatus.selectMode = false;
                openFolder(currentPath);
                return;
            }


        }else if (requestCode == 4){
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(context,"링크 편집",Toast.LENGTH_SHORT).show();
                deleteCheckedFile(currentPath);
                writeLink(currentPath, AppStatus.name, AppStatus.url);
                AppStatus.name = null;
                AppStatus.url = null;
                lvItem.clearChoices();
                AppStatus.selectMode = false;
                openFolder(currentPath);
                return;
            }

        }
    }

    public String searchFolder(File folder){
        String output = "";
        File[] files = folder.listFiles();

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            lPath.add(file.getAbsolutePath());

            if (file.isDirectory())
                output += searchFolder(file);
            else{
                output += file.getName().toString() + "\n";
                output += readLink(file.getAbsolutePath()).toString() + "\n";
            }
        }
        return output;
    }

    public String readLink(String path){
        File f = new File(path) ;
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

        String url = sb.toString();

        if(!(url.substring(0,4).equals("http")))
            url = "http://" + url;

        return url;
    } //end of readLink

    public void writeLink(String path, String name, String url){
        File f = new File(path + "/" + name + "") ;
        FileWriter fw = null ;
        String text = url;

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
    }

    public void openLink(String path){
        String url = readLink(path);

        Toast.makeText(context,url,Toast.LENGTH_SHORT).show();

        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        startActivity(intent);
        //if (intent.resolveActivity(getPackageManager()) != null) {
         //   startActivity(intent);
        //}
    } //end of openLink

    public void openFolder(String path) {
        tvPath.setText("Location: " + path);

        lItem = new ArrayList<String>();
        lPath = new ArrayList<String>();

        File f = new File(path);
        File[] files = f.listFiles();

        if (!homePath.equals(path)) {
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

    public void deleteCheckedFile(String path) {
        SparseBooleanArray checkedItems = lvItem.getCheckedItemPositions();

        int count = arrayAdapter.getCount();
        for (int i = 0; i < count; i++) {
            if (checkedItems.get(i)) {

                File f = new File(lPath.get(i).toString());
                if(f.exists()){
                    f.delete();
                }

            }
        }
    }

    public String init (String path, String appName){
        File f = new File(path, appName);
        if (!f.exists())
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

=======
package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
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
import java.io.FileWriter;
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
    public String homePath = null;
    public static String currentPath = null;

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
                        openLink(lPath.get(position));
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
                AppStatus.selectMode = false;
                openFolder(currentPath);
                return true;

            case R.id.btn_select_item:
                if (AppStatus.selectMode == false){
                    AppStatus.selectMode = true;
                    arrayAdapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_multiple_choice, lItem);
                    lvItem.setAdapter(arrayAdapter);
                }else {
                    AppStatus.selectMode = false;
                    openFolder(currentPath);
                }


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
                if (AppStatus.selectMode == true){
                    int count = 0;
                    int key = 0;

                    for (int i = 0; i < lvItem.getCount(); i++){
                        if (lvItem.isItemChecked(i)){
                            count++;
                            key = i;
                        }
                    }


                    // 1개 이상을 선택한 경우
                    if(count > 1){
                        Toast.makeText(context,"한 개의 파일만 선택하세요.",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        int position = lvItem.getCheckedItemPosition();
                        File f = new File(lPath.get(key).toString());

                        if (f.isDirectory()){

                        }
                        else{
                            intent = new Intent(context, UpdateLinkActivity.class);
                            intent.putExtra("name",f.getName().toString());
                            intent.putExtra("url", readLink(f.getAbsolutePath()).toString());
                            startActivityForResult(intent,4);
                        }
                    }

                }

                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK){
                Toast.makeText(context,"링크 추가",Toast.LENGTH_SHORT).show();
                openFolder(currentPath);
            }
            return;
        }else if (requestCode == 2){
            if (resultCode == RESULT_OK){
                Toast.makeText(context,"폴더 추가",Toast.LENGTH_SHORT).show();
                openFolder(currentPath);
            }
            return;
        }else if (requestCode == 3) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(context, "링크/폴더 삭제", Toast.LENGTH_SHORT).show();
                deleteCheckedFile(currentPath);
            }
            lvItem.clearChoices();
            AppStatus.selectMode = false;
            openFolder(currentPath);
            return;

        }else if (requestCode == 4){
            if (resultCode == RESULT_OK) {
                Toast.makeText(context,"링크 편집",Toast.LENGTH_SHORT).show();
                deleteCheckedFile(currentPath);
                writeLink(currentPath, AppStatus.name, AppStatus.url);
                AppStatus.name = null;
                AppStatus.url = null;
            }
            lvItem.clearChoices();
            AppStatus.selectMode = false;
            openFolder(currentPath);
            return;
        }
    }

    public String readLink(String path){
        File f = new File(path) ;
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

        String url = sb.toString();

        if(!(url.substring(0,4).equals("http")))
            url = "http://" + url;

        return url;
    } //end of readLink

    public void writeLink(String path, String name, String url){
        File f = new File(path + "/" + name + "") ;
        FileWriter fw = null ;
        String text = url;

        try {
            fw = new FileWriter(f) ;
            fw.write(text) ;

        } catch (Exception e) {
            e.printStackTrace() ;
        }

        if (fw != null) {
            try {
                fw.close() ;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void openLink(String path){
        String url = readLink(path);

        Toast.makeText(context,url,Toast.LENGTH_SHORT).show();

        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        startActivity(intent);
        //if (intent.resolveActivity(getPackageManager()) != null) {
         //   startActivity(intent);
        //}
    } //end of openLink

    public void openFolder(String path) {
        tvPath.setText("Location: " + path);

        lItem = new ArrayList<String>();
        lPath = new ArrayList<String>();

        File f = new File(path);
        File[] files = f.listFiles();

        if (!homePath.equals(path)) {
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

    /*public void updateCheckedFile(String path){
        int count = arrayAdapter.getCount();
        if(count > 1){
            Toast.makeText(context,"한 개의 파일만 선택하세요.",Toast.LENGTH_SHORT).show();
        }
        else{
            int checkedItem = lvItem.getCheckedItemPosition();
            File f = new File(lPath.get(checkedItem).toString());
            if(f.exists()){
                f.get
            }
        }
        lvItem.clearChoices();
    }*/

    public void deleteCheckedFile(String path) {
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
        //lvItem.clearChoices() ;
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

>>>>>>> f6e4c878409b50643663fb66060ffa7bc7d7d787:MainActivity.java
