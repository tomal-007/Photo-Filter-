package com.example.jawad.mashmoment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.FileInputStream;

public class Gallery extends AppCompatActivity implements View.OnTouchListener {
    public Matrix p_matrix = new Matrix();
    public float[] f =new float[9];
    private Matrix savedMatrix = new Matrix();
    // we can be in one of these mash3 states
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;
    // remember some things for zooming
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;
    private float d = 0f;
    private float newRot = 0f;
    private float[] lastEvent = null;
    public  int mashId=7;
    public MyParcelable o=new MyParcelable();
    String filename;
    BitmapFactory.Options opts= new  BitmapFactory.Options();
    ViewGroup.LayoutParams layoutParams;
    private RelativeLayout overlay;
    DisplayMetrics metrics = new DisplayMetrics();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        Bitmap bitmap;
        ImageView i= (ImageView) findViewById(R.id.image);
        ImageView view = (ImageView) findViewById(R.id.imageView);
        view.setOnTouchListener(this);
        FileInputStream is;
        Bitmap bmp = null;
        overlay=(RelativeLayout)findViewById(R.id.overLay);
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        RelativeLayout.LayoutParams overlayParams = (RelativeLayout.LayoutParams) overlay.getLayoutParams();
        overlayParams.height = metrics.heightPixels - metrics.widthPixels;
        overlay.setLayoutParams(overlayParams);
        filename = getIntent().getStringExtra("image");
        try {
            is = this.openFileInput(filename);
            opts.inMutable=true;
            bmp = BitmapFactory.decodeStream(is,null,opts);
            Log.d("onCreate bmp","bmp width "+bmp.getWidth()+" bmp height "+bmp.getHeight());
            Log.d("opts","opts width "+opts.outWidth+" opts height "+opts.outHeight);
            is.close();
        } catch (Exception e) {
                e.printStackTrace();
        }
        i.setImageBitmap(bmp);
    }
    public boolean onTouch(View v, MotionEvent event) {
        // handle touch events here
        ImageView view = (ImageView) v;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(p_matrix);
                start.set(event.getX(), event.getY());
                mode = DRAG;
                lastEvent = null;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    savedMatrix.set(p_matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                lastEvent = new float[4];
                lastEvent[0] = event.getX(0);
                lastEvent[1] = event.getX(1);
                lastEvent[2] = event.getY(0);
                lastEvent[3] = event.getY(1);
                d = rotation(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                lastEvent = null;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    p_matrix.set(savedMatrix);
                    float dx = event.getX() - start.x;
                    float dy = event.getY() - start.y;
                    p_matrix.postTranslate(dx, dy);
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        p_matrix.set(savedMatrix);
                        float scale = (newDist / oldDist);
                        p_matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                    if (lastEvent != null) {
                        newRot = rotation(event);
                        float r = newRot - d;
                        float[] values = new float[9];
                        p_matrix.getValues(values);
                        float tx = values[2];
                        float ty = values[5];
                        float sx = values[0];
                        float xc = (view.getWidth() / 2) * sx;
                        float yc = (view.getHeight() / 2) * sx;
                        p_matrix.postRotate(r, tx + xc, ty + yc);
                    }
                }
                break;
        }
        view.setImageMatrix(p_matrix);
        return true;
    }
    /**
     * Determine the space between the first two fingers
     */

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /**
     * Calculate the degree to be rotated by.
     *
     * @param event
     * @return Degrees
     */
    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }
    public void mashAction(View v){
        ImageButton lulli = (ImageButton) v;
        ImageView view = (ImageView) findViewById(R.id.imageView);
        switch(v.getId()) {
            /*
            case R.id.m1:
                view.setImageDrawable(getResources().getDrawable(R.drawable.mash01, getApplicationContext().getTheme()));
                mashId = 1;
                break;
            case R.id.m2:
                view.setImageDrawable(getResources().getDrawable(R.drawable.mash02, getApplicationContext().getTheme()));
                mashId = 2;
                break;
            case R.id.m3:
                view.setImageDrawable(getResources().getDrawable(R.drawable.mash03, getApplicationContext().getTheme()));
                mashId = 3;
                break;
            case R.id.m4:
                view.setImageDrawable(getResources().getDrawable(R.drawable.mash04, getApplicationContext().getTheme()));
                mashId = 4;
                break;
            case R.id.m5:
                view.setImageDrawable(getResources().getDrawable(R.drawable.tam01, getApplicationContext().getTheme()));
                mashId = 5;
                break;
            case R.id.m6:
                view.setImageDrawable(getResources().getDrawable(R.drawable.tam02, getApplicationContext().getTheme()));
                mashId = 6;
                break;
            */
            case R.id.s1:
                view.setImageDrawable(getResources().getDrawable(R.drawable.shakib01, getApplicationContext().getTheme()));
                mashId = 7;
                break;
            case R.id.s2:
                view.setImageDrawable(getResources().getDrawable(R.drawable.shakib02, getApplicationContext().getTheme()));
                mashId = 8;
                break;
            case R.id.s3:
                view.setImageDrawable(getResources().getDrawable(R.drawable.shakib03, getApplicationContext().getTheme()));
                mashId = 9;
                break;
            case R.id.s4:
                view.setImageDrawable(getResources().getDrawable(R.drawable.shakib04, getApplicationContext().getTheme()));
                mashId = 10;
                break;
            case R.id.s5:
                view.setImageDrawable(getResources().getDrawable(R.drawable.shakib05, getApplicationContext().getTheme()));
                mashId = 11;
                break;
        }
    }
    public void go_next(View v)
    {
        p_matrix.getValues(f);
        Log.d("matrixValue"," "+p_matrix.toString());
        o.f=f;
        try
        {
            Intent in1 = new Intent(Gallery.this, Main2Activity.class);
            in1.putExtra("image", filename);
            in1.putExtra("emblem",mashId);
            Log.d("emblemID"," "+mashId);
            in1.putExtra("transform", o);
            Gallery.this.startActivity(in1);
            Log.d("emblemID","after intent call "+mashId);
            //Toast.makeText(AndroidCamera2API.this, "fuck after intent in1 call", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            filename = null;
        }
    }

}
