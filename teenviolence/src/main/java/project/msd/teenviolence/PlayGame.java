package project.msd.teenviolence;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * Created by surindersokhal on 1/30/16.
 */
public class PlayGame extends Activity implements GestureDetector.OnGestureListener, Animation.AnimationListener {

    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 10;

    static int nextCounter = 0;

    static int totalCorrectResponse=0;
    static int totalwrongResponse=0;
    static int unattemptedQuestions=0;
    static long totalTimeTaken=0;
    static int totalQuestions=0;
    static boolean nextImageNeeded = false;
    static boolean gameOver = false;
    static boolean paintInPostExecuteNeeded = true;
    GestureDetector detector = null;
    static boolean checkImagePainted=false;

    ImageView view = null;
    PlayGame that = null;
    static long startTime = 0, endTime = 0;
    static int correctResponses = 0;
    static ArrayList<TestSubjectResults> testSubjectResults = new ArrayList<TestSubjectResults>();
    static Thread thread=null;
    ProgressDialog dialog = null;
    private Animation animZoomIn = null;
    private Animation animZoomOut = null, animNormal = null, animFadeOut = null;
    LinearLayout linearLayout;
    final Semaphore semaphore=new Semaphore(0,true);

    public void initialiseValues(){
        totalCorrectResponse=0;
        totalwrongResponse=0;
        unattemptedQuestions=0;
        totalTimeTaken=0;
        totalQuestions=0;
        nextImageNeeded = false;
        gameOver = false;
        paintInPostExecuteNeeded = true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (ParameterFile.continueGame == false) {
            initialiseValues();
        }
        else {
            nextImageNeeded = false;
            gameOver = false;
            paintInPostExecuteNeeded = true;
        }
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.play_game);
        dialog = new ProgressDialog(this);
        that = this;

        linearLayout=(LinearLayout)findViewById(R.id.layoutID);
        linearLayout.setBackgroundColor(Color.rgb(12, 12, 12));
        view = (ImageView) findViewById(R.id.imageView);
        detector = new GestureDetector(this, this);


        loadAnimaions();
        startPlayingTheGame();
}

    public void loadAnimaions(){
        animZoomIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_zoom_in);
        animZoomOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_zoom_out);
        animNormal = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_normal);

        animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out_animation);
        animZoomIn.setAnimationListener(this);
        animZoomOut.setAnimationListener(this);
        animFadeOut.setAnimationListener(this);
    }



    public void fingerSwipedUp() {
        view.startAnimation(animZoomOut);
    }

    public void fingerSwipeDown() {
        view.startAnimation(animZoomIn);
    }

    public void fingerSwipeRight() {
        view.startAnimation(animFadeOut);
    }

    public void startPlayingTheGame() {

        thread =new Thread(new Runnable() {
            @Override
            public void run() {

                paintImages();

            }
        });
        thread.start();




    }


    synchronized public void paintNextImage(Semaphore sema) {
        final Semaphore semaphore=sema;
        if(!gameOver) {

            runOnUiThread(new Runnable() {
                public void run() {
                    view.startAnimation(animNormal);
                    System.out.println("----------------------------");
                    System.out.println(testSubjectResults.size());
                    System.out.println(nextCounter);
                    if (testSubjectResults.get(nextCounter).isPositive) {
                        if (testSubjectResults.get(nextCounter).backgroundColor.equalsIgnoreCase("#"))
                            testSubjectResults.get(nextCounter).backgroundColor = ParameterFile.positiveColor;

                        linearLayout.setBackgroundColor(Color.parseColor(testSubjectResults.get(nextCounter).backgroundColor));
                    } else if (testSubjectResults.get(nextCounter).isNeutral){
                        if (testSubjectResults.get(nextCounter).backgroundColor.equalsIgnoreCase("#"))
                            testSubjectResults.get(nextCounter).backgroundColor = ParameterFile.neutralColor;
                        linearLayout.setBackgroundColor(Color.parseColor(testSubjectResults.get(nextCounter).backgroundColor));
                    }
                    else {
                        if (testSubjectResults.get(nextCounter).backgroundColor.equalsIgnoreCase("#"))
                            testSubjectResults.get(nextCounter).backgroundColor = ParameterFile.negativeColor;
                        linearLayout.setBackgroundColor(Color.parseColor(testSubjectResults.get(nextCounter).backgroundColor));
                    }
                    view.setImageBitmap(testSubjectResults.get(nextCounter).image);

                    nextCounter++;
                    nextImageNeeded = false;
                    dialog.dismiss();

                    paintInPostExecuteNeeded = false;
                    startTime = System.currentTimeMillis();
                    checkImagePainted = true;

                }

            });


        }
    }


    public void paintImages() {

        while (!gameOver) {

            //if (nextCounter>= ParameterFile.totalGames) {
            if (nextCounter>= PlayGame.testSubjectResults.size()) {
                gameOver=true;

                buildReport();
                break;
            }

            if(nextImageNeeded && ((nextCounter) > (testSubjectResults.size() - 1))){

                paintInPostExecuteNeeded=true;
                dialog.dismiss();
            }
            if (((nextCounter==0 && testSubjectResults.size()>1 )|| nextImageNeeded)) {

                paintNextImage(semaphore);
                try{

                    semaphore.acquire();
                    checkImagePainted=false;

                }catch (Exception e){

                    e.printStackTrace();;
                }
            }

            if (paintInPostExecuteNeeded && !dialog.isShowing()){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.setMessage("Please wait while next image is being fetched");
                        dialog.show();
                    }
                });


            }
        }
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

    public void checkForNextImage() {
        //if (nextCounter < ParameterFile.totalGames) {
        if (nextCounter < PlayGame.testSubjectResults.size()) {
            if (nextCounter-1 <testSubjectResults.size() - 1) {
                nextImageNeeded = true;
            } else {
                paintInPostExecuteNeeded = true;
                dialog.dismiss();

            }
        }else{
            gameOver=true;
            buildReport();
        }
    }


    public void buildReport(){

        totalQuestions+=testSubjectResults.size();
        ParameterFile.isGamePlayed=true;
        Semaphore semaphore=new Semaphore(0,true);
        new SendFeedback(semaphore).execute();
        gameOver=true;
        nextCounter = 0;
        //new FetchImageParameter().execute();
        testSubjectResults.clear();
        testSubjectResults = new ArrayList<TestSubjectResults>();
        //ParameterFile.QuestionSession=1;
        System.out.println("testSubjectResults in play game"+testSubjectResults.size());
        System.out.println("QS in play game "+ParameterFile.QuestionSession);
        Intent intent=new Intent(PlayGame.this,Continue.class);
        intent.putExtra("isQuestion",true);
        PlayGame.this.startActivity(intent);

    }


    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                            float velocityY) {

        float diffY = e2.getY() - e1.getY();
        float diffX = e2.getX() - e1.getX();

        TestSubjectResults results = testSubjectResults.get(nextCounter-1);

        //double time=(System.currentTimeMillis()-startTime)/(Math.pow(10,9));
        double time=(System.currentTimeMillis()-startTime);


        try {
            if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {
                    fingerSwipeDown();
                    if ((time) > results.displayTime && nextCounter > 0 && ((nextCounter - 1) < (testSubjectResults.size() - 1))) {
                        System.out.print("More than allocated time");
                        unattemptedQuestions++;
                        //semaphore.release();

                        return false;

                    }

                    results.correctness = results.isPositive == true ? true : false;

                    //endTime = System.nanoTime();
                    endTime = System.currentTimeMillis();
                    results.responseTime = (endTime - startTime);
                }

                else{
                    fingerSwipedUp();
                    if ((time) > results.displayTime && nextCounter > 0 && ((nextCounter - 1) < (testSubjectResults.size() - 1))) {
                        System.out.print("More than allocated time");
                        unattemptedQuestions++;
                        //semaphore.release();
                        return false;
                    }

                    results.correctness = results.isPositive == false ? true : false;
                    //endTime = System.nanoTime();
                    results.isAttempted = true;
                    endTime = System.currentTimeMillis();
                    results.responseTime = (endTime - startTime);
                }
            }

            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        fingerSwipeRight();
                        if ((time) > results.displayTime && nextCounter > 0 && ((nextCounter - 1) < (testSubjectResults.size() - 1))) {
                            System.out.print("More than allocated time");
                            unattemptedQuestions++;
                            //semaphore.release();

                            return false;

                        }

                        results.correctness = results.isNeutral == true ? true : false;

                        //endTime = System.nanoTime();
                        endTime = System.currentTimeMillis();
                        results.responseTime = (endTime - startTime);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        results.isAttempted = true;
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {

        return false;
    }


    //gets the current orientation of the phone.
    public int getScreenOrientation() {
        final int rotation = ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).
                getDefaultDisplay().getOrientation();
        switch (rotation) {
            case Surface.ROTATION_0:
                return 0;
            case Surface.ROTATION_90:
                return 1;
            case Surface.ROTATION_180:
                return 2;
            default:
                return 3;
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {
        Log.i("Animation", "start");
     }

    @Override
    public void onAnimationEnd(Animation animation) {
        Log.i("Animation", "end");

        checkForNextImage();

        semaphore.release();

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }


    protected void onDestroy(){
        super.onDestroy();
        System.out.println("Done on Destroy");

    }

    public void onBackPressed() {
       return;

    }

}
