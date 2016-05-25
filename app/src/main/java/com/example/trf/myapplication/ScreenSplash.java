package com.example.trf.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
public class ScreenSplash extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = new SplashView(ScreenSplash.this);
        setContentView(view);
        /** set time to splash out **/
        final int nWelcomeScreenDisplay = 3000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(ScreenSplash.this, ListActivity.class);
                startActivity(mainIntent);
                ScreenSplash.this.finish();
            }
        }, nWelcomeScreenDisplay);
    }
    class SplashView extends View {
        SplashView(Context context) {
            super(context);
        }
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Paint paint = new Paint();
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.start1);
            canvas.drawBitmap(bitmap, 0, 0, paint);
        }
    }
}
