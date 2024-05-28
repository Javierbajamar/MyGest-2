package com.atecresa.gestionCobros.tpv;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

@SuppressWarnings({"deprecation", "UnusedReturnValue"})
public class DrawingView extends View {

    private final Paint mPaint;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private final Path mPath;
    private final Paint mBitmapPaint;
    private final Context context;

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 0;

    private boolean controlFirma = false;


    public DrawingView(Context context) {
        super(context);
        this.context = context;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(2);

        mPath = new Path();
        mBitmapPaint = new Paint();
        mBitmapPaint.setColor(Color.BLACK);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), paint);
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mPaint);

    }


    private void touch_start(float x, float y) {
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        mCanvas.drawPath(mPath, mPaint);
        mPath.reset();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }

        setControlFirma();
        return true;
    }

    public Bitmap getBitmap() {
        this.setDrawingCacheEnabled(true);
        this.buildDrawingCache();
        Bitmap bmp = Bitmap.createBitmap(this.getDrawingCache());
        this.setDrawingCacheEnabled(false);
        return bmp;
    }

    public void setBitmap(Bitmap bit) {
        mBitmap = bit;
        draw(mCanvas);
    }


    public boolean saveBitMap(Bitmap bitmap, String fileName) {

        try {

            File file = new File(context.getExternalFilesDir(null).getPath() + "/" + fileName + ".png");
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

            Log.v("draw", "Imagen creada -->" + context.getExternalFilesDir(null).getPath() + "/" + fileName + ".png");
            return true;

        } catch (Exception e) {
            Log.v("draw", "Error al crear imagen " + e.toString());
            return false;
        }

    }

    public void limpiar() {
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    }

    public boolean isControlFirma() {
        return controlFirma;
    }

    private void setControlFirma() {
        this.controlFirma = true;
    }

    public String getStringBitmap(){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        this.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
