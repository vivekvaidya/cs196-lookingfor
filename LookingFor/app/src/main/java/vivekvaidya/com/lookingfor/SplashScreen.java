package vivekvaidya.com.lookingfor;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;


public class SplashScreen extends Activity {

    private ProgressBar mProgressBar;

    private int mProgressStatus = 0;

    private ImageView background;

    private static int interval = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mProgressBar = findViewById(R.id.progressBar2);
        background = findViewById(R.id.imageView);
        background.setColorFilter(Color.argb(100, 255, 255, 255), PorterDuff.Mode.LIGHTEN);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mProgressStatus < 100){
                    mProgressStatus++;
                    android.os.SystemClock.sleep(30);
                    mProgressBar.setProgress(mProgressStatus);

                }


                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();

            }

        }).start();


    }

}



//    Thread myThread = new Thread() {
//        @Override
//        public void run() {
//            try {
//                sleep(3000);
//                Intent i = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(i);
//                finish();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//        }
//    };
//        myThread.start();

// mProgressBar = (ProgressBar) findViewById(R.id.progressBar2);
//
//

//
//
//         new Handler().postDelayed(new Runnable() {
//@Override
//public void run() {
//        Intent i = new Intent(SplashScreen.this, MainActivity.class);
//        startActivity(i);
//        finish();
//        }
//
//private void finish() {
//        }
//        }, interval);