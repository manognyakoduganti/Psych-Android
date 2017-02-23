package project.msd.teenviolence;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.annotations.SerializedName;

import java.util.concurrent.Semaphore;

public class Continue extends AppCompatActivity implements View.OnClickListener{

    TextView textView;
    Button exitgame,continuegame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_continue_screen);
        textView=(TextView)findViewById(R.id.continueID);
        Intent intent = getIntent();
        String text=intent.getStringExtra("text");
        textView.setText(text);
        exitgame=(Button)findViewById(R.id.exitgame);
        continuegame=(Button)findViewById(R.id.continuegame);
        exitgame.setOnClickListener(this);
        continuegame.setOnClickListener(this);

  }


    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.exitgame) {
            ParameterFile.QuestionSession=1;
            Intent intent=new Intent(Continue.this,Questions.class);
            Continue.this.startActivity(intent);
        }
        if (view.getId() == R.id.continuegame) {
           // ParameterFile.QuestionSession=0;
            new FetchImageParameter().execute();
            Intent intent=new Intent(Continue.this,DemoColorActivity.class);
            //ParameterFile.QuestionSession++;
            //intent.putExtra("demoNeeded",false);
            Continue.this.startActivity(intent);
        }
    }

    public void onBackPressed() {
        Intent intent=new Intent(Continue.this,HomeScreen.class);
        Continue.this.startActivity(intent);
    }
    protected void onDestroy(){
        super.onDestroy();
        System.out.println("Done on Destroy");
    }


}
