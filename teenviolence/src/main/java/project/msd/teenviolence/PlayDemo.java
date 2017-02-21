package project.msd.teenviolence;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;

import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

public class PlayDemo extends Activity implements SurfaceHolder.Callback {

    VideoView videoView = null;
    Button skipButton = null;
    boolean isSkipEnable = true;

    public void onBackPressed() {

        Intent intent = new Intent(PlayDemo.this, HomeScreen.class);
        PlayDemo.this.startActivity(intent);
        finish();
    }

        /**
         * Called when the activity is first created.
         */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_play_demo);

            Button buttonPlayVideo = (Button) findViewById(R.id.skipButton);

            getWindow().setFormat(PixelFormat.UNKNOWN);

            //Displays a video file.
            VideoView mVideoView = (VideoView) findViewById(R.id.videoView);


            String uriPath = "android.resource://"+ getPackageName() + "/"+ R.raw.samplevid;
            Uri uri = Uri.parse(uriPath);
            mVideoView.setVideoURI(uri);
            mVideoView.requestFocus();
            mVideoView.start();


            buttonPlayVideo.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {

                    // VideoView refference see main.xml
                    VideoView mVideoView = (VideoView) findViewById(R.id.videoView);

                    String uriPath = "android.resource://"+ getPackageName() + "/"+ R.raw.samplevid;

                    Uri uri = Uri.parse(uriPath);
                    mVideoView.setVideoURI(uri);
                    mVideoView.requestFocus();
                    mVideoView.start();


                }
            });
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            // TODO Auto-generated method stub

        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // TODO Auto-generated method stub

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // TODO Auto-generated method stub

        }
    }

