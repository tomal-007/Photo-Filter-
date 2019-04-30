package com.example.jawad.mashmoment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class AndroidCamera2APIfront extends AppCompatActivity implements View.OnTouchListener  {
    private static final String TAG = "AndroidCameraApi";
    private ImageButton takePictureButton;
    private Button swapButton;
    private AutoFitTextureView textureView;
    private Semaphore mCameraOpenCloseLock=new Semaphore(1);
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }
    private String cameraId;
    private static final int MAX_PREVIEW_WIDTH = 1920;

    /**
     * Max preview height that is guaranteed by Camera2 API
     */
    private static final int MAX_PREVIEW_HEIGHT = 1080;
    protected CameraDevice cameraDevice;
    protected CameraCaptureSession cameraCaptureSessions;
    protected CaptureRequest captureRequest;
    protected CaptureRequest.Builder captureRequestBuilder;
    private ImageReader imageReader;
    private File file;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private boolean mFlashSupported;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    private Size mPreviewSize;
    // new variables

    // these matrices will be used to move and zoom image
    public static Matrix matrix = new Matrix();
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
    private int fwidth,fheight;
    private boolean FlashSupported;

    private int  mashId=7;
    int DisplayWidth=1080;
    int DisplayHeight=1920;

    /**
     * Orientation of the camera sensor
     */
    private int SensorOrientation;
    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader reader) {
            mBackgroundHandler.post(new ImageSaver(reader.acquireNextImage(), file));
        }

    };
    public MyParcelable o=new MyParcelable();
    //private RelativeLayout overlay;
    private Button take_now;
    private RelativeLayout overlay;
    DisplayMetrics metrics = new DisplayMetrics();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_camera2_api);
        textureView = (AutoFitTextureView) findViewById(R.id.texture);
        assert textureView != null;
        textureView.setSurfaceTextureListener(textureListener);
        takePictureButton = (ImageButton) findViewById(R.id.btn_takepicture);
        //swapButton = (Button) findViewById(R.id.button2);
        assert takePictureButton != null;
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
        overlay=(RelativeLayout)findViewById(R.id.overLay);
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        /*take_now=(Button)findViewById(R.id.take_now);
        take_now.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                takeScreenshot();
            }
        }) ;*/
        ImageView view = (ImageView) findViewById(R.id.imageView);
        view.setOnTouchListener(this);

        LinearLayout layout = (LinearLayout) findViewById(R.id.linear);
        /*for (int i = 1; i <= 3; i++) {
            VamChod lulli = new VamChod(this);
            lulli.setPadding(2, 2, 2, 2);
            lulli.setS("mash"+i+".png");
            lulli.setScaleType(ImageView.ScaleType.FIT_XY);
            lulli.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    //BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                    //Bitmap bitmap = drawable.getBitmap();
                    VamChod lulli=(VamChod) v;
                    ImageView view = (ImageView) findViewById(R.id.imageView);
                    view.setImageBitmap(((BitmapDrawable) lulli.getDrawable()).getBitmap());
                    mashId=lulli.getS();
                }
            }) ;
            //layout.addView(lulli);
        }*/
    }
    public void mashAction(View v){
        //ImageButton lulli = (ImageButton) v;
        ImageView view = (ImageView) findViewById(R.id.imageView);
        switch(v.getId()) {
            /*
            case R.id.m1:
                //lulli = (ImageButton) findViewById(R.id.m1);
                view.setImageDrawable(getResources().getDrawable(R.drawable.mash01, getApplicationContext().getTheme()));
                mashId =1;
                break;
            case R.id.m2:
                //lulli = (ImageButton) findViewById(R.id.m2);
                view.setImageDrawable(getResources().getDrawable(R.drawable.mash02, getApplicationContext().getTheme()));
                mashId=2;
                break;
            case R.id.m3:
                //lulli = (ImageButton) findViewById(R.id.m3);
                view.setImageDrawable(getResources().getDrawable(R.drawable.mash03, getApplicationContext().getTheme()));
                mashId=3;
                break;
            case R.id.m4:
                //lulli = (ImageButton) findViewById(R.id.m4);
                view.setImageDrawable(getResources().getDrawable(R.drawable.mash04, getApplicationContext().getTheme()));
                mashId=4;
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

        //view.setImageBitmap(((BitmapDrawable) lulli.getDrawable()).getBitmap());
    }
    public boolean onTouch(View v, MotionEvent event) {
        // handle touch events here
        ImageView view = (ImageView) v;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                mode = DRAG;
                lastEvent = null;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
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
                    matrix.set(savedMatrix);
                    float dx = event.getX() - start.x;
                    float dy = event.getY() - start.y;
                    matrix.postTranslate(dx, dy);
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = (newDist / oldDist);
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                    if (lastEvent != null) {
                        newRot = rotation(event);
                        float r = newRot - d;
                        float[] values = new float[9];
                        matrix.getValues(values);
                        float tx = values[2];
                        float ty = values[5];
                        float sx = values[0];
                        float xc = (view.getWidth() / 2) * sx;
                        float yc = (view.getHeight() / 2) * sx;
                        matrix.postRotate(r, tx + xc, ty + yc);
                    }
                }
                break;
        }
        //o.matrix=matrix;
        view.setImageMatrix(matrix);
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


    public void swap(View view)
    {
        Intent front_go;
        closeCamera();
        front_go = new Intent(this,AndroidCamera2API.class);
        startActivity(front_go);
    }
    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            //open your camera here
            fwidth=width;
            fheight=height;
            int previewWidth = ((AutoFitTextureView)findViewById(R.id.texture)).getMeasuredWidth();
            int previewHeight = ((AutoFitTextureView)findViewById(R.id.texture)).getMeasuredHeight();

            RelativeLayout.LayoutParams overlayParams = (RelativeLayout.LayoutParams) overlay.getLayoutParams();
            overlayParams.height = metrics.heightPixels - metrics.widthPixels;
            overlay.setLayoutParams(overlayParams);
            openCamera(width,height);
        }
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            configureTransform(width, height);

            // Transform you image captured size according to the surface width and height
        }
        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }
        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };
    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            //This is called when the camera is open
            Log.e(TAG, "onOpened");
            mCameraOpenCloseLock.release();
            cameraDevice = camera;
            createCameraPreview();
        }
        @Override
        public void onDisconnected(CameraDevice camera) {

            mCameraOpenCloseLock.release();
            cameraDevice.close();
            cameraDevice=null;
        }
        @Override
        public void onError(CameraDevice camera, int error) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            cameraDevice = null;
        }
    };
    final CameraCaptureSession.CaptureCallback captureCallbackListener = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            Toast.makeText(AndroidCamera2APIfront.this, "Saved:" + file, Toast.LENGTH_SHORT).show();
            createCameraPreview();
        }
    };
    protected void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }
    protected void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);

            v1.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            v1.buildDrawingCache();
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);
            v1.destroyDrawingCache();
            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();


        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }
    }
    public void go_to_new(String fileName)
    {
        //String fileName = "myImage2";
        //no .png or .jpg needed
        //String fileName2 = "myImage2";
        matrix.getValues(f);
        Log.d("matrixValue"," "+matrix.toString());

        o.f=f;

        //Toast.makeText(AndroidCamera2API.this, "values of f"+f.toString(), Toast.LENGTH_LONG).show();
        //Toast.makeText(AndroidCamera2APIfront.this, "fuck before try stage", Toast.LENGTH_SHORT).show();
        try {
            //ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            //ByteArrayOutputStream bytes2 = new ByteArrayOutputStream();
            //bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            //bmp2.compress(Bitmap.CompressFormat.PNG, 100, bytes2);
            //FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
            //FileOutputStream fo2 = openFileOutput(fileName2, Context.MODE_PRIVATE);
            //fo.write(bytes.toByteArray());
            //fo2.write(bytes2.toByteArray());
            // remember close file output
            //fo.close();
            //fo2.close();
            Intent in1 = new Intent(AndroidCamera2APIfront.this, Main2Activityfront.class);
            in1.putExtra("image2", fileName);
            //Bundle b=new Bundle();
            //b.putSerializable("transform",o);
            //in1.putExtras(b);
            //in1.putExtra("image2",fileName2);
            //in1.putExtra("transform", o);
            in1.putExtra("emblem2",mashId);
            Log.d("emblemID2"," "+mashId);
            //Toast.makeText(AndroidCamera2APIfront.this, "fuck before putextra transform in1 call", Toast.LENGTH_SHORT).show();
            in1.putExtra("transform2", o);
            //Toast.makeText(AndroidCamera2APIfront.this, "fuck before intent in1 call", Toast.LENGTH_SHORT).show();
            AndroidCamera2APIfront.this.startActivity(in1);
            Log.d("emblemID","after intent call "+mashId);
            //Toast.makeText(AndroidCamera2APIfront.this, "fuck after intent in1 call", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(AndroidCamera2APIfront.this, "Exception call"+e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            fileName = null;
        }
    }
    protected void takePicture() {
        if(null == cameraDevice) {
            Log.e(TAG, "cameraDevice is null");
            return;
        }
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpegSizes = null;
            if (characteristics != null) {
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            }
            int width = 640;
            int height = 480;
            if (jpegSizes != null && 0 < jpegSizes.length) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));
            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            // Orientation
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            final File file = new File(Environment.getExternalStorageDirectory()+"/pic.jpg");
            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;
                    try {
                        image = reader.acquireLatestImage();
                        //ImageView imageView=(ImageView)findViewById(R.id.imageView);
                        //BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                        //Bitmap bitmap = drawable.getBitmap();
                        //bitmap=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),savedMatrix,true);

                        //ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        //imageView.setImageDrawable(new BitmapDrawable(bitmap));

                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.remaining()];
                        buffer.get(bytes);
                        // buffer=null;


                        //BitmapFactory.Options options=new BitmapFactory.Options();// Create object of bitmapfactory's option method for further option use
                        ///options.inPurgeable = true; // inPurgeable is used to free up memory while required
                        //Bitmap songImage1 = BitmapFactory.decodeByteArray(bytes,0, bytes.length,options);//Decode image, "thumbnail" is the object of image file
                        //Bitmap songImage = Bitmap.createScaledBitmap(songImage1, 50 , 50 , true);// convert decoded bitmap into well scalled Bitmap format.

                        //imageview.SetImageDrawable(songImage);
                        Optimization optimization=new Optimization();
                        DisplayMetrics metrics = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(metrics);
                        if(metrics.heightPixels<1920||metrics.widthPixels<1080)
                        {
                            DisplayHeight=metrics.heightPixels;
                            DisplayWidth=metrics.widthPixels;
                        }
                        else
                        {
                            DisplayHeight=2048;
                            DisplayWidth=1080;
                        }
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds=true;
                        Bitmap sex = BitmapFactory.decodeByteArray(bytes,0,bytes.length,options);
                        options.inMutable=true;
                        int ImageHeight=options.outHeight;
                        int ImageWidth=options.outWidth;
                        String ImageTyepe=options.outMimeType;
                        ImageView iv = (ImageView)findViewById(R.id.imgs);
                        if(iv!=null)
                        {
                            Log.d("imageview","IV width "+iv.getLayoutParams().width+ " IV height "+ iv.getLayoutParams().height);
                        }
                        options.inSampleSize=optimization.calculateInSampleSize(options,DisplayWidth,DisplayHeight);
                        options.inJustDecodeBounds=false;
                        Bitmap usablebitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.length,options);
                        if(usablebitmap!=null) {
                            Log.d("sample size","same Size "+ options.inSampleSize);
                            Log.d("display","display width"+DisplayWidth+ " display height "+ DisplayHeight);
                            Log.d("image","image width"+ImageWidth+ " Image height "+ ImageHeight);
                            Log.d("scaled bitmap","bitmap width"+usablebitmap.getWidth()+ " bitmap height "+ usablebitmap.getHeight());
                            //Toast.makeText(AndroidCamera2APIfront.this, "width " + usablebitmap.getWidth() + "height " + usablebitmap.getHeight() + "\n", Toast.LENGTH_LONG);
                        }
                        else
                            Toast.makeText(AndroidCamera2APIfront.this,"Bitmap is null",Toast.LENGTH_LONG);
                        //ByteArrayOutputStream bits = new ByteArrayOutputStream();
                        String fileName = "myImage2";
                        FileOutputStream fout = openFileOutput(fileName,Context.MODE_PRIVATE);
                        usablebitmap.compress(Bitmap.CompressFormat.JPEG,100,fout);
                        fout.close();


                        //Bitmap sex = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        //RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                        //bytes=null;
// get the width and height of the source bitmap.
                        //int width = sex.getWidth();
                        //int height = sex.getHeight();

//Copy the byte to the file
//Assume source bitmap loaded using options.inPreferredConfig = Config.ARGB_8888;
                        //FileChannel channel = randomAccessFile.getChannel();
                        //MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, width*height*4);
                        //sex.copyPixelsToBuffer(map);
//recycle the source bitmap, this will be no longer used.
                        //sex.recycle();
                        //sex=null;
//Create a new bitmap to load the bitmap again.
                        //sex = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                        //map.position(0);
//load it back from temporary
                        //sex.copyPixelsFromBuffer(map);
//close the temporary file and channel , then delete that also
                        //channel.close();
                        //randomAccessFile.close();

                        //sex.compress(Bitmap.CompressFormat.JPEG,100,stream);
                        //ImageView z = (ImageView) findViewById(R.id.imageView);
                        //z.setDrawingCacheEnabled(true);

                        //Bitmap bitmap = Bitmap.createBitmap(z.getDrawingCache());
                        //Toast.makeText(AndroidCamera2APIfront.this, "fuck first stage", Toast.LENGTH_SHORT).show();
                        go_to_new(fileName);
                        //Toast.makeText(AndroidCamera2APIfront.this, "fuck second stage", Toast.LENGTH_SHORT).show();

                        //Log.d("sex",sex.getWidth()+" "+sex.getHeight());
                        //Log.d("bitmap",bitmap.getWidth()+" "+bitmap.getHeight());

                        //sex=Bitmap.createScaledBitmap(sex,bitmap.getWidth(),bitmap.getWidth(),false);

                        //sex=Bitmap.createBitmap(sex, 0,0,sex.getWidth(), sex.getWidth());
                        //bitmap=Bitmap.createBitmap(bitmap, 0,0,bitmap.getWidth(), bitmap.getWidth());

                        //Log.d("scaled_sex",sex.getWidth()+" "+sex.getHeight());
                        //Log.d("scaled_bitmap",bitmap.getWidth()+" "+bitmap.getHeight());

                        //Bitmap bmOverlay = Bitmap.createBitmap(sex.getWidth(), sex.getHeight(), sex.getConfig());
                        //Canvas canvas = new Canvas(bitmap);
                        //canvas.drawBitmap(sex, 0,0, null);
                        //canvas.drawBitmap(bitmap,0,0, null);
                        //ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        //OutputStream output = null;
                        //output = new FileOutputStream(file);
                        //sex.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        //output.close();
                        //
                        //bytes = stream.toByteArray();
                        // save(bytes);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (image != null) {
                            image.close();
                        }
                    }
                }

                private void save(byte[] bytes) throws IOException {
                    OutputStream output = null;
                    try {
                        output = new FileOutputStream(file);
                        output.write(bytes);
                    } finally {
                        if (null != output) {
                            output.close();
                        }
                    }
                }
            };
            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);
            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    Toast.makeText(AndroidCamera2APIfront.this, "Saved:" + file, Toast.LENGTH_SHORT).show();
                    createCameraPreview();
                }
            };
            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private int getOrientation(int rotation) {
        // Sensor orientation is 90 for most devices, or 270 for some devices (eg. Nexus 5X)
        // We have to take that into account and rotate JPEG properly.
        // For devices with orientation of 90, we simply return our mapping from ORIENTATIONS.
        // For devices with orientation of 270, we need to rotate the JPEG 180 degrees.
        return (ORIENTATIONS.get(rotation) + SensorOrientation + 270) % 360;
    }

    protected void createCameraPreview() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface, imageReader.getSurface()), new CameraCaptureSession.StateCallback(){
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    //The camera is already closed
                    if (null == cameraDevice) {
                        return;
                    }
                    // When the session is ready, we start displaying the preview.
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }
                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(AndroidCamera2APIfront.this, "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        //RelativeLayout overlay = (RelativeLayout) findViewById(R.id.overlay);

        //int previewWidth = ((AutoFitTextureView)findViewById(R.id.texture)).getMeasuredWidth(),
        // previewHeight = ((AutoFitTextureView)findViewById(R.id.texture)).getMeasuredHeight();

        //RelativeLayout.LayoutParams overlayParams = (RelativeLayout.LayoutParams) overlay.getLayoutParams();
        //overlayParams.height = previewHeight - previewWidth;
        //overlay.setLayoutParams(overlayParams);
    }

    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }

    private void setUpCameraOutputs(int width, int height) {

        CameraManager manager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String mcameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics
                        = manager.getCameraCharacteristics(mcameraId);

                // We don't use a front facing camera in this sample.
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_BACK) {
                    continue;
                }

                StreamConfigurationMap map = characteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null) {
                    continue;
                }

                // For still image captures, we use the largest available size.
                Size largest = Collections.max(
                        Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                        new CompareSizesByArea());
                imageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(),
                        ImageFormat.JPEG, /*maxImages*/2);
                imageReader.setOnImageAvailableListener(
                        mOnImageAvailableListener, mBackgroundHandler);

                // Find out if we need to swap dimension to get the preview size relative to sensor
                // coordinate.
                int displayRotation = getWindowManager().getDefaultDisplay().getRotation();
                //noinspection ConstantConditions
                SensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                boolean swappedDimensions = false;
                switch (displayRotation) {
                    case Surface.ROTATION_0:
                    case Surface.ROTATION_180:
                        if (SensorOrientation == 90 || SensorOrientation == 270) {
                            swappedDimensions = true;
                        }
                        break;
                    case Surface.ROTATION_90:
                    case Surface.ROTATION_270:
                        if (SensorOrientation == 0 || SensorOrientation == 180) {
                            swappedDimensions = true;
                        }
                        break;
                    default:
                        Log.e(TAG, "Display rotation is invalid: " + displayRotation);
                }

                Point displaySize = new Point();
                getWindowManager().getDefaultDisplay().getSize(displaySize);
                int rotatedPreviewWidth = width;
                int rotatedPreviewHeight = height;
                int maxPreviewWidth = displaySize.x;
                int maxPreviewHeight = displaySize.y;

                if (swappedDimensions) {
                    rotatedPreviewWidth = height;
                    rotatedPreviewHeight = width;
                    maxPreviewWidth = displaySize.y;
                    maxPreviewHeight = displaySize.x;
                }

                if (maxPreviewWidth > MAX_PREVIEW_WIDTH) {
                    maxPreviewWidth = MAX_PREVIEW_WIDTH;
                }

                if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) {
                    maxPreviewHeight = MAX_PREVIEW_HEIGHT;
                }

                // Danger, W.R.! Attempting to use too large a preview size could  exceed the camera
                // bus' bandwidth limitation, resulting in gorgeous previews but the storage of
                // garbage capture data.
                mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                        rotatedPreviewWidth, rotatedPreviewHeight, maxPreviewWidth,
                        maxPreviewHeight, largest);

                // We fit the aspect ratio of TextureView to the size of preview we picked.
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    textureView.setAspectRatio(
                            mPreviewSize.getWidth(), mPreviewSize.getHeight());
                } else {
                    textureView.setAspectRatio(
                            mPreviewSize.getHeight(), mPreviewSize.getWidth());
                }

                // Check if the flash is supported.
                Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                mFlashSupported = available == null ? false : available;
                cameraId = mcameraId;
                int previewWidth = ((AutoFitTextureView)findViewById(R.id.texture)).getMeasuredWidth();
                int previewHeight = ((AutoFitTextureView)findViewById(R.id.texture)).getMeasuredHeight();

                ImageView i=(ImageView) findViewById(R.id.imageView);
                ViewGroup.LayoutParams ivParams = i.getLayoutParams();

                ivParams.height = previewHeight;
                ivParams.width=previewWidth;

                i.setLayoutParams(ivParams );


                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.

        }
    }

    private static Size chooseOptimalSize(Size[] choices, int textureViewWidth,
                                          int textureViewHeight, int maxWidth, int maxHeight, Size aspectRatio) {

        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        // Collect the supported resolutions that are smaller than the preview Surface
        List<Size> notBigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getWidth() <= maxWidth && option.getHeight() <= maxHeight &&
                    option.getHeight() == option.getWidth() * h / w) {
                if (option.getWidth() >= textureViewWidth &&
                        option.getHeight() >= textureViewHeight) {
                    bigEnough.add(option);
                } else {
                    notBigEnough.add(option);
                }
            }
        }

        // Pick the smallest of those big enough. If there is no one big enough, pick the
        // largest of those not big enough.
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else if (notBigEnough.size() > 0) {
            return Collections.max(notBigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }


    private void openCamera(int width,int height) {


        setUpCameraOutputs(width, height);
        configureTransform(width, height);
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        Log.e(TAG, "is camera open");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AndroidCamera2APIfront.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
            return;
        }
        try {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            manager.openCamera(cameraId, stateCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
        }
        // manager.openCamera(cameraId, stateCallback, mBackgroundHandler);
        Log.e(TAG, "openCamera X");
    }



    private void configureTransform(int viewWidth, int viewHeight) {
        if (null == textureView || null == mPreviewSize) {
            return;
        }
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        textureView.setTransform(matrix);
    }

    protected void updatePreview() {
        if(null == cameraDevice) {
            Log.e(TAG, "updatePreview error, return");
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            if (null != cameraCaptureSessions) {
                cameraCaptureSessions.close();
                cameraCaptureSessions = null;
            }
            if (null != cameraDevice) {
                cameraDevice.close();
                cameraDevice = null;
            }
            if (null != imageReader) {
                imageReader.close();
                imageReader = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            mCameraOpenCloseLock.release();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(AndroidCamera2APIfront.this, "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        /*RelativeLayout overlay;
        overlay=(RelativeLayout)findViewById(R.id.overLay);
        int previewWidth = ((AutoFitTextureView)findViewById(R.id.texture)).getMeasuredWidth();
        int previewHeight = ((AutoFitTextureView)findViewById(R.id.texture)).getMeasuredHeight();
        RelativeLayout.LayoutParams overlayParams = (RelativeLayout.LayoutParams) overlay.getLayoutParams();
        overlayParams.height = previewHeight - previewWidth;
        overlay.setLayoutParams(overlayParams);*/
        Log.e(TAG, "onResume");
        startBackgroundThread();

        if (textureView.isAvailable()) {
            ;          Log.e(TAG,"here available");
            openCamera(textureView.getWidth(),textureView.getHeight());

        } else {
            Log.e(TAG,"here not available");
            textureView.setSurfaceTextureListener(textureListener);

        }
        //textureView.setSurfaceTextureListener(textureListener);
    }
    @Override
    protected void onPause() {
        Log.e(TAG, "onPause");
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }
    private static class ImageSaver implements Runnable {

        /**
         * The JPEG image
         */
        private final Image mImage;
        /**
         * The file we save the image into.
         */
        private final File mFile;

        public ImageSaver(Image image, File file) {
            mImage = image;
            mFile = file;
        }

        @Override
        public void run() {
            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            FileOutputStream output = null;
            try {
                output = new FileOutputStream(mFile);
                output.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mImage.close();
                if (null != output) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

}