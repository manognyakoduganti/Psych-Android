package project.msd.teenviolence;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DemoColorActivity extends Activity implements Runnable,GestureDetector.OnGestureListener, Animation.AnimationListener {

    TextView view;
    LinearLayout layout;
    Thread thread = null;
    private Animation animZoomIn = null;
    private Animation animZoomOut = null, animNormal = null, animFadeOut = null;
    GestureDetector detector = null;
    boolean swipeDown = false, animStarted = false, swipeDone = false,swipeRight = false;
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_demo_color);
        view=(TextView)findViewById(R.id.demoColor);
        layout=(LinearLayout)findViewById(R.id.layoutColor);
        detector = new GestureDetector(this, this);
        layout.setBackgroundColor((Color.parseColor(ParameterFile.positiveColor)));
        view.setText("Please swipe down if you see this color");
        loadAnimaions();
        thread=new Thread(this);
        thread.start();

    }

    public void run(){
        loadFirstAnimation();
    }

    public void loadFirstAnimation(){animStarted=true;
       view.startAnimation(animZoomIn);
    }
    public void loadAnimaions(){
        animZoomIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_zoom_hint_in);
        animZoomOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_zoom_hint_out);
       // animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out_animation);
        animZoomIn.setAnimationListener(this);
        animZoomOut.setAnimationListener(this);
        //animFadeOut.setAnimationListener(this);
    }


    @Override
    public void onAnimationStart(Animation animation) {
        Log.i("Animation", "start");
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if(!swipeDone) {
            if (animStarted) {
                System.out.println("in 1st if  dca class");
                view.startAnimation(animZoomOut);
                animStarted = false;
                //swipeDown=false;
            } else {

                    System.out.println("in 2nd else  dca class");
                    view.startAnimation(animZoomIn);
                    animStarted = true;
            }
        }else{
            createNewActivity();
        }

        }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
    @Override
    public void onLongPress(MotionEvent e) {

    }


    @Override
    public boolean onTouchEvent(MotionEvent me) {
        this.detector.onTouchEvent(me);
        return super.onTouchEvent(me);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.d("---onDown----", e.toString());
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                           float distanceY) {

        //  checkForNextImage();
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                            float velocityY) {
        float diffY = e2.getY() - e1.getY();
        float diffX = e2.getX() - e1.getX();

        System.out.println("in onscroll dca class");

        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (swipeDown && swipeRight && diffX > 0) {
                    System.out.println("swipe done true");
                    swipeDone = true;
                }
            }
        }

        if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
            if (diffY > 0) {
                if (!swipeDown && !swipeRight) {
                    //swipeDown=true;
                    swipeRight = true;
                    layout.setBackgroundColor((Color.parseColor(ParameterFile.negativeColor)));
                    System.out.println(" in swipe up " + swipeDown + " " + swipeRight);
                    view.setText("Please swipe up if you see this color");

                }
            } else {
                if (!swipeDown && swipeRight) {
                    swipeDown = true;
                    System.out.println("swipedown and swipedone value in case 3 " + swipeDown + " " + swipeRight);
                    layout.setBackgroundColor(Color.parseColor(ParameterFile.neutralColor));
                    view.setText("Please swipe right if you see this color");

                }

            }
        }
        return false;

    }
    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {

        return false;
    }

    protected void onDestroy(){
        super.onDestroy();
    }

    public void onBackPressed() {

        return;
    }
    public void createNewActivity(){
        Intent intent=new Intent(DemoColorActivity.this,PlayGame.class);
        intent.putExtra("speed", 100);
        DemoColorActivity.this.startActivity(intent);
    }
}
