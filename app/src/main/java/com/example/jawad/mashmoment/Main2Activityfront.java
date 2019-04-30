package com.example.jawad.mashmoment;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.Date;

public class Main2Activityfront extends AppCompatActivity {


    public MyParcelable ob = new MyParcelable();
    public Matrix matrix = new Matrix();
    public int mashId;
    public RelativeLayout overlay;
    int DisplayHeight;
    int DisplayWidth;
    String filename;
    BitmapFactory.Options opts= new  BitmapFactory.Options();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2_activityfront);

        Bitmap bmp = null;
        filename = getIntent().getStringExtra("image2");
        try {
            FileInputStream is = this.openFileInput(filename);
            opts.inMutable=true;
            bmp = BitmapFactory.decodeStream(is,null,opts);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Bitmap bmp2 = null;
        //String filename2 = getIntent().getStringExtra("image2");
        ob = (MyParcelable) getIntent().getSerializableExtra("transform2");
        //Toast.makeText(this, "values of f"+ob.f.toString(), Toast.LENGTH_SHORT).show();
        matrix.setValues(ob.f);
        Log.d("transformValue", " " + matrix.toString());
        //Bundle b=getIntent().getExtras();
        //ob= (MyParcelable) b.getSerializable("transform");
        /*
        try {
            FileInputStream is2 = this.openFileInput(filename2);
            bmp2 = BitmapFactory.decodeStream(is2);
            is2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
        Log.d("transformValue", " " + matrix.toString());
        mashId = getIntent().getIntExtra("emblem2", 0);
        ImageView imgs = (ImageView) findViewById(R.id.imgs2);
        ImageView shakib = (ImageView) findViewById(R.id.shakib2);
        //int drawableResourceId = this.getResources().getIdentifier("mash"+mashId+".png", "drawable", this.getPackageName());
        Log.d("fuck", mashId + " ");
        switch (mashId) {
            case 1:
                shakib.setImageDrawable(getResources().getDrawable(R.drawable.mash01, getApplicationContext().getTheme()));
                break;
            case 2:
                shakib.setImageDrawable(getResources().getDrawable(R.drawable.mash02, getApplicationContext().getTheme()));
                break;
            case 3:
                shakib.setImageDrawable(getResources().getDrawable(R.drawable.mash03, getApplicationContext().getTheme()));
                break;
            case 4:
                shakib.setImageDrawable(getResources().getDrawable(R.drawable.mash04, getApplicationContext().getTheme()));
                break;
            case 5:
                shakib.setImageDrawable(getResources().getDrawable(R.drawable.tam01, getApplicationContext().getTheme()));
                break;
            case 6:
                shakib.setImageDrawable(getResources().getDrawable(R.drawable.tam02, getApplicationContext().getTheme()));
                break;
            case 7:
                shakib.setImageDrawable(getResources().getDrawable(R.drawable.shakib01, getApplicationContext().getTheme()));
                break;
            case 8:
                shakib.setImageDrawable(getResources().getDrawable(R.drawable.shakib02, getApplicationContext().getTheme()));
                break;
            case 9:
                shakib.setImageDrawable(getResources().getDrawable(R.drawable.shakib03, getApplicationContext().getTheme()));
                break;
            case 10:
                shakib.setImageDrawable(getResources().getDrawable(R.drawable.shakib04, getApplicationContext().getTheme()));
                break;
            case 11:
                shakib.setImageDrawable(getResources().getDrawable(R.drawable.shakib05, getApplicationContext().getTheme()));
                break;

        }
        //
        // ImageView shakib=findViewById(mashId);
        //ImageView shakib=new ImageView(this);
        //shakib.setImageBitmap(BitmapFactory.decodeFile(mashId));
        //bmp=Bitmap.createBitmap(bmp,0,0,bmp.getWidth(),bmp.getHeight());
        imgs.setImageBitmap(bmp);
        // Matrix matrix2 = new Matrix();
        //matrix2.preScale(1, -1);
        //matrix2.postTranslate(bmp.getWidth(), 0);
        //imgs.setImageMatrix(matrix2);
        //shakib.setImageBitmap(bmp2);
        Log.d("transformValue", " " + matrix.toString());
        shakib.setImageMatrix(matrix);
        overlay = (RelativeLayout) findViewById(R.id.overLay3);

        /*
        Canvas canvas = new Canvas(bmp);
        canvas.drawBitmap(bmp, 0,0, null);
        canvas.drawBitmap(((BitmapDrawable)shakib.getDrawable()).getBitmap(),matrix,null);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        OutputStream output = null;
        File file = new File(Environment.getExternalStorageDirectory()+"/pic.jpg");
        try {
            output = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        // Get the preview size
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);


        int height = metrics.heightPixels;
        int width = metrics.widthPixels;
        DisplayHeight = height;
        DisplayWidth = width;

        // Set the height of the overlay so that it makes the preview a square
        RelativeLayout.LayoutParams overlayParams = (RelativeLayout.LayoutParams) overlay.getLayoutParams();
        overlayParams.height = height - width;
        overlay.setLayoutParams(overlayParams);
    }
    public void saveImageToExternal(String imgName, Bitmap bm) throws IOException {
//Create Path to save Image
        String appFolder = " MashCam";
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + appFolder); //Creates app specific folder
        path.mkdirs();
        File imageFile = new File(path, imgName + ".png"); // Imagename.png
        FileOutputStream out = new FileOutputStream(imageFile);
        try {
            bm.compress(Bitmap.CompressFormat.PNG, 100, out); // Compress Image
            out.flush();
            out.close();

            // Tell the media scanner about the new file so that it is
            // immediately available to the user.
            MediaScannerConnection.scanFile(getWindow().getContext(), new String[]{imageFile.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    Log.i("ExternalStorage", "Scanned " + path + ":");
                    Log.i("ExternalStorage", "-> uri=" + uri);
                }
            });
        } catch (Exception e) {
            throw new IOException();
        }
    }
    public void takeScreenshot(View v) {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";
            String description="Captured by mashcam";
            String saved;

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);

            v1.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            v1.buildDrawingCache();
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            bitmap=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getWidth());
            saveImageToExternal(now.toString(),bitmap);
            /*
            saved=MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, now.toString() , description );

            if(saved!=null)
            {
                Log.d("gallery ","Image saved to the gallery");
            }
            else
            {
                Log.d("not","not saved");
            }*/
            v1.setDrawingCacheEnabled(false);
            v1.destroyDrawingCache();
            /*File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();*/
            Dialog settingsDialog = new Dialog(this);
            settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.popup
                    , null));
            settingsDialog.show();
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }
    }
    public void grayscale(View v){
        ImageView imgs=(ImageView) findViewById(R.id.imgs2);
        BitmapFactory.Options imageOptions=new BitmapFactory.Options();
        imageOptions.inMutable=true;
        //Bitmap original=((BitmapDrawable)imgs.getDrawable()).getBitmap();
        try
        {
            InputStream inputStream = this.openFileInput(filename);

            //streamBitmapDecoder=new StreamBitmapDecoder(this);
            //Resource<Bitmap> temp = streamBitmapDecoder.decode(is, Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL);
            //original = BitmapFactory.decodeStream(is);
            //Bitmap bitmap = temp.get();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream,null,imageOptions);
            Log.d("greyscale bitmap","bmp width "+bitmap.getWidth()+" bmp height "+bitmap.getHeight());
            inputStream.close();
            //Bitmap bitmap = original.copy(Bitmap.Config.ARGB_8888,true);
            //Bitmap bitmap = Bitmap.createBitmap(original.getWidth(),original.getHeight(), Bitmap.Config.ARGB_8888);
            Log.d("fuck","temp is working");
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.setSaturation(0);
            paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
            canvas.drawBitmap(bitmap, 0, 0, paint);
            imgs.setImageBitmap(bitmap);
            //imgs.setLayoutParams(layoutParams);
            imgs=(ImageView) findViewById(R.id.shakib2);
            BitmapFactory.Options filterOptions= new BitmapFactory.Options();
            filterOptions.inMutable=true;
            Bitmap emblem=null;
            switch (mashId) {
                case 1:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.mash01,filterOptions);
                    break;
                case 2:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.mash02,filterOptions);
                    break;
                case 3:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.mash03,filterOptions);
                    break;
                case 4:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.mash04,filterOptions);
                    break;
                case 5:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.tam01,filterOptions);
                    break;
                case 6:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.tam02,filterOptions);
                    break;
                case 7:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.shakib01,filterOptions);
                    break;
                case 8:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.shakib02,filterOptions);
                    break;
                case 9:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.shakib03,filterOptions);
                    break;
                case 10:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.shakib04,filterOptions);
                    break;
                case 11:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.shakib05,filterOptions);
                    break;

            }
            //bitmap = original.copy(Bitmap.Config.ARGB_8888,true);
            //bitmap = Bitmap.createBitmap(original.getWidth(),original.getHeight(), Bitmap.Config.ARGB_8888);
            canvas = new Canvas(emblem);
            paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
            canvas.drawBitmap(emblem, 0, 0, paint);
            Log.d("shakib","shakib width "+bitmap.getWidth()+" shakib height "+bitmap.getHeight());
            imgs.setImageBitmap(emblem);
            imgs.setImageMatrix(matrix);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //bitmap.recycle();
    }
    public void sepia(View v){
        ImageView imgs=(ImageView) findViewById(R.id.imgs2);
        BitmapFactory.Options imageOptions=new BitmapFactory.Options();
        imageOptions.inMutable=true;
        Bitmap picture=null;
        try{
            //Bitmap original=((BitmapDrawable)imgs.getDrawable()).getBitmap();
            InputStream is = this.openFileInput(filename);
            picture = BitmapFactory.decodeStream(is,null,imageOptions);
            is.close();
            //Bitmap bitmap = Bitmap.createBitmap(original.getWidth(),original.getHeight(), Bitmap.Config.ARGB_8888);
            //original.recycle();
            Canvas canvas = new Canvas(picture);
            Paint paint = new Paint();
            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.setSaturation(0);
            ColorMatrix colorScale = new ColorMatrix();
            colorScale.setScale(1, 1, 0.8f, 1);

            // Convert to grayscale, then apply brown color
            colorMatrix.postConcat(colorScale);

            paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
            canvas.drawBitmap(picture, 0, 0, paint);
            imgs.setImageBitmap(picture);
            ImageView shakib=(ImageView) findViewById(R.id.shakib2);
            BitmapFactory.Options filterOptions= new BitmapFactory.Options();
            filterOptions.inMutable=true;
            Bitmap emblem=null;
            switch (mashId) {
                case 1:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.mash01,filterOptions);
                    break;
                case 2:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.mash02,filterOptions);
                    break;
                case 3:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.mash03,filterOptions);
                    break;
                case 4:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.mash04,filterOptions);
                    break;
                case 5:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.tam01,filterOptions);
                    break;
                case 6:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.tam02,filterOptions);
                    break;
                case 7:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.shakib01,filterOptions);
                    break;
                case 8:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.shakib02,filterOptions);
                    break;
                case 9:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.shakib03,filterOptions);
                    break;
                case 10:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.shakib04,filterOptions);
                    break;
                case 11:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.shakib05,filterOptions);
                    break;

            }
            canvas = new Canvas(emblem);
            paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
            canvas.drawBitmap(emblem, 0, 0, paint);
            shakib.setImageBitmap(emblem);
            shakib.setImageMatrix(matrix);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void binary(View v){
        ImageView imgs=(ImageView) findViewById(R.id.imgs2);
        BitmapFactory.Options imageOptions=new BitmapFactory.Options();
        imageOptions.inMutable=true;
        Bitmap picture=null;
        try {
            //Bitmap bitmap = Bitmap.createBitmap(original.getWidth(),original.getHeight(), Bitmap.Config.ARGB_8888);

            InputStream is = this.openFileInput(filename);
            picture = BitmapFactory.decodeStream(is,null,imageOptions);
            is.close();
            Canvas canvas = new Canvas(picture);

            Paint paint = new Paint();
            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.setSaturation(0);

            float m = 255f;
            float t = -255 * 128f;
            ColorMatrix threshold = new ColorMatrix(new float[]{
                    m, 0, 0, 1, t,
                    0, m, 0, 1, t,
                    0, 0, m, 1, t,
                    0, 0, 0, 1, 0
            });

            // Convert to grayscale, then scale and clamp
            colorMatrix.postConcat(threshold);
            paint.setColorFilter(new ColorMatrixColorFilter(
                    colorMatrix));
            canvas.drawBitmap(picture, 0, 0, paint);
            imgs.setImageBitmap(picture);
            ImageView shakib = (ImageView) findViewById(R.id.shakib2);
            BitmapFactory.Options filterOptions= new BitmapFactory.Options();
            filterOptions.inMutable=true;
            Bitmap emblem=null;
            switch (mashId) {
                case 1:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.mash01,filterOptions);
                    break;
                case 2:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.mash02,filterOptions);
                    break;
                case 3:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.mash03,filterOptions);
                    break;
                case 4:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.mash04,filterOptions);
                    break;
                case 5:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.tam01,filterOptions);
                    break;
                case 6:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.tam02,filterOptions);
                    break;
                case 7:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.shakib01,filterOptions);
                    break;
                case 8:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.shakib02,filterOptions);
                    break;
                case 9:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.shakib03,filterOptions);
                    break;
                case 10:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.shakib04,filterOptions);
                    break;
                case 11:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.shakib05,filterOptions);
                    break;

            }
            canvas = new Canvas(emblem);

            paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
            canvas.drawBitmap(emblem, 0, 0, paint);
            shakib.setImageBitmap(emblem);
            shakib.setImageMatrix(matrix);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void invert(View v){
        ImageView imgs=(ImageView) findViewById(R.id.imgs2);
        BitmapFactory.Options imageOptions=new BitmapFactory.Options();
        imageOptions.inMutable=true;
        Bitmap picture=null;
        try {
            InputStream is = this.openFileInput(filename);
            picture = BitmapFactory.decodeStream(is, null, imageOptions);
            is.close();
            Canvas canvas = new Canvas(picture);

            Paint paint = new Paint();
            ColorMatrix colorMatrix = new ColorMatrix(new float[]{
                    -1, 0, 0, 0, 255,
                    0, -1, 0, 0, 255,
                    0, 0, -1, 0, 255,
                    0, 0, 0, 1, 0
            });
            paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
            canvas.drawBitmap(picture, 0, 0, paint);
            imgs.setImageBitmap(picture);
            ImageView shakib = (ImageView) findViewById(R.id.shakib2);
            BitmapFactory.Options filterOptions = new BitmapFactory.Options();
            filterOptions.inMutable = true;
            Bitmap emblem = null;
            switch (mashId) {
                case 1:
                    emblem = BitmapFactory.decodeResource(getResources(), R.drawable.mash01, filterOptions);
                    break;
                case 2:
                    emblem = BitmapFactory.decodeResource(getResources(), R.drawable.mash02, filterOptions);
                    break;
                case 3:
                    emblem = BitmapFactory.decodeResource(getResources(), R.drawable.mash03, filterOptions);
                    break;
                case 4:
                    emblem = BitmapFactory.decodeResource(getResources(), R.drawable.mash04, filterOptions);
                    break;
                case 5:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.tam01,filterOptions);
                    break;
                case 6:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.tam02,filterOptions);
                    break;
                case 7:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.shakib01,filterOptions);
                    break;
                case 8:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.shakib02,filterOptions);
                    break;
                case 9:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.shakib03,filterOptions);
                    break;
                case 10:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.shakib04,filterOptions);
                    break;
                case 11:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.shakib05,filterOptions);
                    break;

            }
            canvas = new Canvas(emblem);

            paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
            canvas.drawBitmap(emblem, 0, 0, paint);
            shakib.setImageBitmap(emblem);
            shakib.setImageMatrix(matrix);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void sketch(View v){
        ImageView imgs=(ImageView) findViewById(R.id.imgs2);
        BitmapFactory.Options imageOptions = new BitmapFactory.Options();
        imageOptions.inMutable = true;
        imageOptions.inSampleSize = 4;
        Bitmap picture = null;
        try
        {
            InputStream is = this.openFileInput(filename);
            picture = BitmapFactory.decodeStream(is,null,imageOptions);
            is.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        picture=drawSketch(picture);
        imgs.setImageBitmap(picture);
        ImageView shakib = (ImageView) findViewById(R.id.shakib2);
        BitmapFactory.Options filterOptions = new BitmapFactory.Options();
        filterOptions.inMutable = true;
        //filterOptions.inSampleSize=4;
        Bitmap emblem = null;
        switch (mashId) {
            case 1:
                emblem = BitmapFactory.decodeResource(getResources(), R.drawable.mash01, filterOptions);
                break;
            case 2:
                emblem = BitmapFactory.decodeResource(getResources(), R.drawable.mash02, filterOptions);
                break;
            case 3:
                emblem = BitmapFactory.decodeResource(getResources(), R.drawable.mash03, filterOptions);
                break;
            case 4:
                emblem = BitmapFactory.decodeResource(getResources(), R.drawable.mash04, filterOptions);
                break;
            case 5:
                emblem=BitmapFactory.decodeResource(getResources(),R.drawable.tam01,filterOptions);
                break;
            case 6:
                emblem=BitmapFactory.decodeResource(getResources(),R.drawable.tam02,filterOptions);
                break;
            case 7:
                emblem=BitmapFactory.decodeResource(getResources(),R.drawable.shakib01,filterOptions);
                break;
            case 8:
                emblem=BitmapFactory.decodeResource(getResources(),R.drawable.shakib02,filterOptions);
                break;
            case 9:
                emblem=BitmapFactory.decodeResource(getResources(),R.drawable.shakib03,filterOptions);
                break;
            case 10:
                emblem=BitmapFactory.decodeResource(getResources(),R.drawable.shakib04,filterOptions);
                break;
            case 11:
                emblem=BitmapFactory.decodeResource(getResources(),R.drawable.shakib05,filterOptions);
                break;
        }
        emblem = drawSketch(emblem);
        shakib.setImageBitmap(emblem);
        shakib.setImageMatrix(matrix);
    }
    public void noFilter(View v)
    {
        ImageView imgs = (ImageView) findViewById(R.id.imgs2);
        BitmapFactory.Options imageOptions=new BitmapFactory.Options();
        imageOptions.inMutable=true;
        Bitmap picture=null;
        try
        {
            InputStream is = this.openFileInput(filename);
            picture = BitmapFactory.decodeStream(is, null, imageOptions);
            is.close();
            imgs.setImageBitmap(picture);
            ImageView shakib = (ImageView) findViewById(R.id.shakib2);
            BitmapFactory.Options filterOptions = new BitmapFactory.Options();
            filterOptions.inMutable = true;
            Bitmap emblem = null;
            switch (mashId) {
                case 1:
                    emblem = BitmapFactory.decodeResource(getResources(), R.drawable.mash01, filterOptions);
                    break;
                case 2:
                    emblem = BitmapFactory.decodeResource(getResources(), R.drawable.mash02, filterOptions);
                    break;
                case 3:
                    emblem = BitmapFactory.decodeResource(getResources(), R.drawable.mash03, filterOptions);
                    break;
                case 4:
                    emblem = BitmapFactory.decodeResource(getResources(), R.drawable.mash04, filterOptions);
                    break;
                case 5:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.tam01,filterOptions);
                    break;
                case 6:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.tam02,filterOptions);
                    break;
                case 7:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.shakib01,filterOptions);
                    break;
                case 8:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.shakib02,filterOptions);
                    break;
                case 9:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.shakib03,filterOptions);
                    break;
                case 10:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.shakib04,filterOptions);
                    break;
                case 11:
                    emblem=BitmapFactory.decodeResource(getResources(),R.drawable.shakib05,filterOptions);
                    break;

            }
            shakib.setImageBitmap(emblem);
            shakib.setImageMatrix(matrix);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private Bitmap drawSketch(Bitmap picture)
    {
        Canvas canvas = new Canvas(picture);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(picture, 0, 0, paint);
        Bitmap picture2 = picture.copy(Bitmap.Config.ARGB_8888,true);
        canvas = new Canvas(picture2);

        paint = new Paint();
        colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(picture2, 0, 0, paint);
        canvas = new Canvas(picture);
        paint = new Paint();
        colorMatrix = new ColorMatrix(new float[]{
                -1, 0, 0, 0, 255,
                0, -1, 0, 0, 255,
                0, 0, -1, 0, 255,
                0, 0, 0, 1, 0
        });
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(picture, 0, 0, paint);
        RenderScript rs = RenderScript.create(this);

        Allocation allocIn = Allocation.createFromBitmap(rs, picture);
        //Allocation allocOut = Allocation.createFromBitmap(rs, original);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        blur.setInput(allocIn);
        blur.setRadius(25);
        blur.forEach(allocIn);

        allocIn.copyTo(picture);
        rs.destroy();
        IntBuffer buffBase = IntBuffer.allocate(picture.getWidth() * picture.getHeight());
        picture.copyPixelsToBuffer(buffBase);
        buffBase.rewind();

        IntBuffer buffBlend = IntBuffer.allocate(picture2.getWidth() * picture2.getHeight());
        picture2.copyPixelsToBuffer(buffBlend);
        buffBlend.rewind();

        IntBuffer buffOut = IntBuffer.allocate(picture.getWidth() * picture.getHeight());
        buffOut.rewind();

        while (buffOut.position() < buffOut.limit()) {

            int filterInt = buffBlend.get();
            int srcInt = buffBase.get();

            int redValueFilter = Color.red(filterInt);
            int greenValueFilter = Color.green(filterInt);
            int blueValueFilter = Color.blue(filterInt);

            int redValueSrc = Color.red(srcInt);
            int greenValueSrc = Color.green(srcInt);
            int blueValueSrc = Color.blue(srcInt);

            int redValueFinal = colordodge(redValueFilter, redValueSrc);
            int greenValueFinal = colordodge(greenValueFilter, greenValueSrc);
            int blueValueFinal = colordodge(blueValueFilter, blueValueSrc);


            int pixel = Color.argb(255, redValueFinal, greenValueFinal, blueValueFinal);


            buffOut.put(pixel);
        }

        buffOut.rewind();

        picture.copyPixelsFromBuffer(buffOut);
        picture2.recycle();
        int[] allpixels = new int[picture.getHeight() * picture.getWidth()];

        picture.getPixels(allpixels, 0, picture.getWidth(), 0, 0, picture.getWidth(), picture.getHeight());

        for (int i = 0; i < allpixels.length; i++) {
            if (allpixels[i] == Color.BLACK) {
                allpixels[i] = Color.TRANSPARENT;
            }
        }
        picture.setPixels(allpixels, 0, picture.getWidth(), 0, 0, picture.getWidth(), picture.getHeight());
        return picture;
    }
    private int colordodge(int in1, int in2)
    {
        float image = (float)in2;
        float mask = (float)in1;
        return ((int) ((image == 255) ? image:Math.min(255, (((long)mask << 8 ) / (255 - image)))));
    }
    public void toEffect(View v)
    {
        findViewById(R.id.horizontal_scroll5).setVisibility(View.INVISIBLE);
        findViewById(R.id.horizontal_scroll4).setVisibility(View.VISIBLE);

    }
    public void toEmblem(View v)
    {
        findViewById(R.id.horizontal_scroll4).setVisibility(View.INVISIBLE);
        findViewById(R.id.horizontal_scroll5).setVisibility(View.VISIBLE);

    }
    public void set_emblem(View v)
    {
        Drawable drawable = null;
        switch (v.getId()) {
            /*
            case R.id.e01f:
                drawable=getResources().getDrawable(R.drawable.e01, getApplicationContext().getTheme());
                break;
            case R.id.e02f:
                drawable=getResources().getDrawable(R.drawable.e02, getApplicationContext().getTheme());
                break;
            */
            case R.id.e03f:
                drawable=getResources().getDrawable(R.drawable.e03, getApplicationContext().getTheme());
                break;
            case R.id.e04f:
                drawable=getResources().getDrawable(R.drawable.e04, getApplicationContext().getTheme());
                break;
        }
        //Drawable drawable=getResources().getDrawable(R.drawable.f, getApplicationContext().getTheme());
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
        bitmap=Bitmap.createScaledBitmap(bitmap,DisplayWidth,DisplayWidth,true);
        ImageView imgs=(ImageView)findViewById(R.id.emblem2);
        imgs.setVisibility(View.VISIBLE);
        imgs.setImageBitmap(bitmap);
    }
    public void noEmblem(View v)
    {
        findViewById(R.id.emblem2).setVisibility(View.INVISIBLE);
    }
}