package au.edu.anu.u5833429.mynotebook;


import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.VideoView;
import android.content.DialogInterface;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareDialog;

/**
 * Created by lenovo on 2016/3/14.
 */
public class SelectAct extends AppCompatActivity{
    private CoordinatorLayout coordinatorLayout;
    private EditText editText;
    private ImageView imageView;
    private VideoView videoView;
    private NotesDB notesDB;
    private SQLiteDatabase dbWriter;
    private Bitmap bitmap;
    ShareDialog shareDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select);
        CoordinatorLayout coordinatorLayoutNew = (CoordinatorLayout) findViewById(R.id.select);
        coordinatorLayout = coordinatorLayoutNew;
        bitmap = null;
        setTitle(getIntent().getStringExtra(NotesDB.TITLE));
//        System.out.println(getIntent().getIntExtra(NotesDB.ID, 0));
        editText = (EditText) findViewById(R.id.s_tv);
        imageView = (ImageView) findViewById(R.id.s_img);
        videoView = (VideoView) findViewById(R.id.s_video);
        notesDB = new NotesDB(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        shareDialog = new ShareDialog(this);
        dbWriter = notesDB.getWritableDatabase();
        if (getIntent().getStringExtra(NotesDB.PATH).equals("null")){
            imageView.setVisibility(View.GONE);
        }else{
            imageView.setVisibility(View.VISIBLE);
        }
        if (getIntent().getStringExtra(NotesDB.VIDEO).equals("null")){
            videoView.setVisibility(View.GONE);
        }else{
            videoView.setVisibility(View.VISIBLE);
        }
        editText.setText(getIntent().getStringExtra(NotesDB.CONTENT));
        bitmap = BitmapFactory.decodeFile(getIntent().getStringExtra(NotesDB.PATH));
        imageView.setImageBitmap(bitmap);
        videoView.setVideoURI(Uri.parse(getIntent().getStringExtra(NotesDB.VIDEO)));
        videoView.start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select, menu);
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
            case R.id.background_select_lab:
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
            case R.id.background_select_brown_paper:
                coordinatorLayout.setBackgroundResource(R.drawable.brown_paper);
                return true;
            case R.id.background_select_blank:
                coordinatorLayout.setBackgroundResource(R.drawable.white);
                return true;
            case R.id.background_select_cream:
                coordinatorLayout.setBackgroundResource(R.drawable.cream_wove);
                return true;
            case R.id.delete:
                new AlertDialog.Builder(SelectAct.this)
                        .setTitle("Are you sure you want to selete this note?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbWriter.delete(NotesDB.TABLE_NAME, "_id="+getIntent().getIntExtra(NotesDB.ID, 0), null);
                                SelectAct.this.finish();
                            }
                        }).setNegativeButton("No", null)
                        .show();
                return true;
            case R.id.background_select_share:
                if (getIntent().getStringExtra(NotesDB.PATH).equals("null")){
                    if (getIntent().getStringExtra(NotesDB.VIDEO).equals("null")){
                        if (ShareDialog.canShow(ShareLinkContent.class)) {
                            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                    .setContentTitle(getIntent().getStringExtra(NotesDB.TITLE))
                                    .setContentDescription(editText.getText().toString())
                                    .setContentUrl(Uri.parse("http://anu.edu.au"))
                                    .build();
                            shareDialog.show(linkContent);
                        }
                    }else {
                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                .setContentTitle(getIntent().getStringExtra(NotesDB.TITLE))
                                .setContentDescription(editText.getText().toString())
                                .setContentUrl(Uri.parse("http://anu.edu.au"))
                                .build();
                        shareDialog.show(linkContent);
                    }
                } else {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle(getIntent().getStringExtra(NotesDB.TITLE))
                            .setContentDescription(editText.getText().toString())
                            .setContentUrl(Uri.parse("http://anu.edu.au"))
                            .build();
                    shareDialog.show(linkContent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //返回时弹窗
    @Override
    public void onBackPressed() {
        if (editText.getText().toString().equals(getIntent().getStringExtra(NotesDB.CONTENT))){
            SelectAct.this.finish();
        }else{
            new AlertDialog.Builder(SelectAct.this)//新建弹窗
                    .setTitle("Save before leave?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ContentValues values = new ContentValues();
                            values.put("content", editText.getText().toString());
                            dbWriter.update(NotesDB.TABLE_NAME, values, "content=?", new String[]{getIntent().getStringExtra(NotesDB.CONTENT)});
                            SelectAct.this.finish();
                        }
                    })
                    .setNeutralButton("Cancel", null)
                    .setNegativeButton("Don't save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SelectAct.this.finish();
                        }
                    })
                    .show();
        }

    }
}