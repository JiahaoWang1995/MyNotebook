package au.edu.anu.u5833429.mynotebook;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.VideoView;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;

import static android.net.Uri.fromFile;

/**
 * Created by lenovo on 2016/3/3.
 */
//添加新笔记
public class AddContent extends AppCompatActivity {
    private String val;
    private CharSequence title;
    private EditText editText;
    private ImageView imageView;
    private VideoView videoView;
    private NotesDB notesDB;
    private SQLiteDatabase dbWriter;
    private ArrayAdapter<String> adapter;
    private CoordinatorLayout coordinatorLayout;
    private File phoneFile, videoFile;
    ShareDialog shareDialog;

    static final int REQUEST_IMAGE_CAPTURE = 1;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addcontent);
        CoordinatorLayout coordinatorLayoutNew = (CoordinatorLayout) findViewById(R.id.notebook);
        coordinatorLayout = coordinatorLayoutNew;
        FacebookSdk.sdkInitialize(getApplicationContext());
        shareDialog = new ShareDialog(this);
        Bundle bundle=getIntent().getExtras();
        title=bundle.getString("title");
        setTitle(title);
        val = getIntent().getStringExtra("flag");
        editText = (EditText) findViewById(R.id.et_text);
        imageView = (ImageView) findViewById(R.id.c_img);
        videoView = (VideoView) findViewById(R.id.c_video);
        notesDB = new NotesDB(this);
        dbWriter = notesDB.getWritableDatabase();
        initView();
    }

    public void initView() {
        if (val.equals("1")) {
            imageView.setVisibility(View.GONE);
            videoView.setVisibility(View.GONE);
        }
        if (val.equals("2")){
            imageView.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);
            Intent iimg = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            phoneFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+getTime()+".jpg");
            iimg.putExtra(MediaStore.EXTRA_OUTPUT, fromFile(phoneFile));
            startActivityForResult(iimg, 1);
        }
        if (val.equals("3")){
            imageView.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);
            Intent video = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            videoFile = new File(Environment.getExternalStorageDirectory()
                    .getAbsolutePath()+"/"+getTime()+".mp4");
            video.putExtra(MediaStore.EXTRA_OUTPUT, fromFile(videoFile));
            startActivityForResult(video, 2);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note, menu);
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
            case R.id.background_note_lab:
                String color = editText.getText().toString();
                Log.d("test", color);
                if(color.equals("red")){
                    coordinatorLayout.setBackgroundColor(Color.RED);
                }else{
                    if(color.equals("blue")){
                        coordinatorLayout.setBackgroundColor(Color.BLUE);
                    }else {
                        if(color.equals("yello")){
                            coordinatorLayout.setBackgroundColor(Color.YELLOW);
                        }else {
                            if (color.equals("green")){
                                coordinatorLayout.setBackgroundColor(Color.GREEN);
                            }
                            else {
                                coordinatorLayout.setBackgroundColor(Color.WHITE);
                            }
                        }
                    }
                }
                return true;
            case R.id.background_note_brown_paper:
                coordinatorLayout.setBackgroundResource(R.drawable.brown_paper);
                return true;
            case R.id.background_note_blank:
                coordinatorLayout.setBackgroundResource(R.drawable.white);
                return true;
            case R.id.background_note_cream:
                coordinatorLayout.setBackgroundResource(R.drawable.cream_wove);
                return true;
            case R.id.background_note_share:
                if (val.equals("1")){
                    if (ShareDialog.canShow(ShareLinkContent.class)) {
                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                .setContentTitle(title.toString())
                                .setContentDescription(editText.getText().toString())
                                .setContentUrl(Uri.parse("http://anu.edu.au"))
                                .build();
                        shareDialog.show(linkContent);
                    }
                }else {
                    if (val.equals("2")){
                        if (ShareDialog.canShow(ShareLinkContent.class)) {
                            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                    .setContentTitle(title.toString())
                                    .setContentDescription(editText.getText().toString())
                                    .setContentUrl(Uri.parse("http://anu.edu.au"))
                                    .build();
                            shareDialog.show(linkContent);
                        }
                    }else {
                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                .setContentTitle(title.toString())
                                .setContentDescription(editText.getText().toString())
                                .setContentUrl(Uri.parse("http://anu.edu.au"))
                                .build();
                        shareDialog.show(linkContent);
                    }
                }
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    //返回时弹窗
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(AddContent.this)//新建弹窗
                .setTitle("Save before leave?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addDB();
                        AddContent.this.finish();
                    }
                })
                .setNeutralButton("Cancel", null)
                .setNegativeButton("Don't save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AddContent.this.finish();
                    }
                })
                .show();
    }

    //数据库添加权限方法
    public void addDB(){
        ContentValues cv = new ContentValues();
        cv.put(NotesDB.TITLE, title.toString());
        cv.put(NotesDB.CONTENT, editText.getText().toString());
        cv.put(NotesDB.TIME, getTime());
        cv.put(NotesDB.PATH, phoneFile+"");
        cv.put(NotesDB.VIDEO, videoFile+"");
        dbWriter.insert(NotesDB.TABLE_NAME, null, cv);
    }

    //本机时间获取方法
    public String getTime(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_SS");
        Date curDate = new Date();
        String str = format.format(curDate);
        return str;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            Bitmap bitmap = BitmapFactory.decodeFile(phoneFile
                    .getAbsolutePath());
            imageView.setImageBitmap(bitmap);
        }
        if (requestCode == 2){
            videoView.setVideoURI(Uri.fromFile(videoFile));
            videoView.start();
        }
    }
}