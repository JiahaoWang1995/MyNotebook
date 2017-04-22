package au.edu.anu.u5833429.mynotebook;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;
import com.facebook.FacebookSdk;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity{
    private NotesDB notesDB;//数据库导入
    private SQLiteDatabase dbWriter;//数据库添加权限
    private ListViewForScrollView lv;
    private Intent i;
    private long lastClickTime = 0;//返回计时器
    private MyAdapter adapter;
    private SQLiteDatabase dbReader;//数据库读取权限
    private String title;
    private Cursor cursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initView();
        selectDB();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText editText = new EditText(MainActivity.this);
                new AlertDialog.Builder(MainActivity.this)//新建弹窗
                        .setTitle("Title of new note: ")
                        .setView(editText)//获取Title的输入框
                        .setPositiveButton("Text only",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        title = editText.getText().toString();
                                        //addDB();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("title", title);
                                        i = new Intent(MainActivity.this, AddContent.class);
                                        i.putExtras(bundle);
                                        i.putExtra("flag", "1");
                                        startActivity(i);
                                    }
                                })
                        .setNegativeButton("Text & Photo", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                title = editText.getText().toString();
                                //addDB();
                                Bundle bundle = new Bundle();
                                bundle.putString("title", title);
                                i = new Intent(MainActivity.this, AddContent.class);
                                i.putExtras(bundle);
                                i.putExtra("flag", "2");
                                startActivity(i);
                            }
                        })
                        .setNeutralButton("Text & Video", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                title = editText.getText().toString();
                                //addDB();
                                Bundle bundle = new Bundle();
                                bundle.putString("title", title);
                                i = new Intent(MainActivity.this, AddContent.class);
                                i.putExtras(bundle);
                                i.putExtra("flag", "3");
                                startActivity(i);
                            }
                        })
                        .show();
                Snackbar.make(view, "New Dialog created!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_about:
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("ABOUT")
                        .setMessage("This is an application developed by Jiahao Wang(Frank) as the assignment project of COMP2500 in first semester 2016. \n\n"+
                        "Any bugs found please send to: \n\n"+"u5833429@anu.edu.au")
                        .setNegativeButton("I know", null)
                        .show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //返回按钮的处理
    @Override
    public void onBackPressed() {
        if (lastClickTime == 0){
            Toast.makeText(this, "Press back button one more time to exit.", Toast.LENGTH_SHORT).show();
            lastClickTime = System.currentTimeMillis();
        }
        else {
            if (System.currentTimeMillis() - lastClickTime <= 1000){
                supportFinishAfterTransition();
            }
            else {
                Toast.makeText(this, "Press back button one more time to exit.", Toast.LENGTH_SHORT).show();
                lastClickTime = System.currentTimeMillis();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectDB();
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    //获取时间方法
    public String getTime(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_SS");
        Date curDate = new Date();
        String str = format.format(curDate);
        return str;
    }


    //初始化列表方法
    public void initView(){
        lv = (ListViewForScrollView) findViewById(R.id.card_list);
        lv.setDivider(null);
        notesDB = new NotesDB(this);
        dbReader = notesDB.getReadableDatabase();
        dbWriter = notesDB.getWritableDatabase();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            cursor.moveToPosition(position);
            Intent i = new Intent(MainActivity.this, SelectAct.class);
            i.putExtra(NotesDB.ID, cursor.getInt(cursor.getColumnIndex(NotesDB.ID)));
            i.putExtra(NotesDB.TITLE, cursor.getString(cursor.getColumnIndex(NotesDB.TITLE)));
            i.putExtra(NotesDB.CONTENT, cursor.getString(cursor.getColumnIndex(NotesDB.CONTENT)));
            i.putExtra(NotesDB.TIME, cursor.getString(cursor.getColumnIndex(NotesDB.TITLE)));
            i.putExtra(NotesDB.PATH, cursor.getString(cursor.getColumnIndex(NotesDB.PATH)));
            i.putExtra(NotesDB.VIDEO, cursor.getString(cursor.getColumnIndex(NotesDB.VIDEO)));
            startActivity(i);
            }
        });
    }

    //获取数据方法
    public void selectDB(){
        cursor = dbReader.query(NotesDB.TABLE_NAME, null, null, null, null, null, null, null);
        adapter = new MyAdapter(this ,cursor);
        lv.setAdapter(adapter);
    }
}
