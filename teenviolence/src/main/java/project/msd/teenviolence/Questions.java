package project.msd.teenviolence;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.AppCompatRadioButton;
import android.view.Gravity;
import android.view.View;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class Questions extends AppCompatActivity implements View.OnClickListener {


    Button button = null;
    LinearLayout layout = null;
    View[] edits;
    TextView[] questionsViews;

    boolean isQuestion;
    static Questions questions = null;
    Semaphore semaphore = new Semaphore(0, true);
    ProgressDialog progressDialog = null;
    boolean demoPlayed = true;
    ArrayList<QuestionDetails> questionsArrayList  = null;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        semaphore = new Semaphore(0, true);
        layout = (LinearLayout) findViewById(R.id.scrol);
        Intent intent = new Intent();
        demoPlayed = intent.getBooleanExtra("demoNeeded", true);
        if (intent.hasExtra("isQuestion")) {
            isQuestion = intent.getBooleanExtra("isQuestion", false);
        }
        new fetchQuestions().execute();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait while questions are being fetched");
        progressDialog.show();
        ;
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Questions Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    class fetchQuestions extends AsyncTask<String, Void, ArrayList<QuestionDetails>> {

        protected ArrayList<QuestionDetails> doInBackground(String... parms) {


            try {
                InputStream stream = BuildConnections.buildConnection(Constant.SERVER_ADDRESS + "" +
                        "question?"+Constant.TARGET_GROUP_ID+"="+ParameterFile.tgId+"&"+Constant.SOURCE+"="+Constant.ANDROID);

                JSONObject object = BuildConnections.getJSOnObject(stream);

                JSONArray array = object.getJSONArray("results");
                int length = array.length();
                questionsArrayList = new ArrayList<QuestionDetails>();

                for (int i = 0; i < length; i++) {

                    QuestionDetails questionDetails = new QuestionDetails();
                    questionDetails.setresponseType(array.getJSONObject(i).getString("responseType"));
                    questionDetails.setquestionId(array.getJSONObject(i).getString("questionId"));
                    questionDetails.setstartLabel(array.getJSONObject(i).getString("startLabel"));
                    questionDetails.setendLabel(array.getJSONObject(i).getString("endLabel"));
                    questionDetails.setquestionName(array.getJSONObject(i).getString("questionName"));
                    questionsArrayList.add(questionDetails);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return questionsArrayList;
        }


        protected void onPostExecute(ArrayList<QuestionDetails> questionsArrayList) {
            displayQuestions(questionsArrayList);
            semaphore.release();
            displayButton();
            progressDialog.dismiss();
        }
    }

    public void displayButton() {
        LinearLayout lLayout = new LinearLayout(this);
        lLayout.setOrientation(LinearLayout.HORIZONTAL);


        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lparams.weight = 1;

        button = new Button(this);
        button.setText("Continue");
        button.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        lLayout.addView(button);
        button.setOnClickListener(this);
        layout.addView(lLayout);

    }

    @Override
    public void onClick(View view) {

        new SendFeedback().execute(getResults());
        try {
            semaphore.acquire();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void createNewActivity(Class activity, String text) {
        Intent intent = new Intent(Questions.this, activity);
        intent.putExtra("speed", 100);
        intent.putExtra("text", text);
        Questions.this.startActivity(intent);
    }

    public void displayQuestions(ArrayList<QuestionDetails> questionsArrayList) {
        layout.removeAllViews();
        int nbrOfQuestion = questionsArrayList.size();
        //edits = new SeekBar[nbrOfQuestion];
        edits = new View[nbrOfQuestion];
        questionsViews = new TextView[nbrOfQuestion];
        LinearLayout lLayout = null;
        int count=112310;
        for (int i = 0; i < nbrOfQuestion; i++) {
                count++;
            lLayout = new LinearLayout(this);
            lLayout.setOrientation(LinearLayout.VERTICAL);


            LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lparams.weight = 1;
            lparams.setMargins(0, 10, 0, 0);
            TextView tView = buildTextView(i, questionsArrayList.get(i).getquestionName(), lparams);
            View questionReplyView = null;
            if(questionsArrayList.get(i).getresponseType().equals("Categorical")){

                questionReplyView = buildRadioGroupView(count,lparams, questionsArrayList.get(i).getstartLabel(),
                        questionsArrayList.get(i).getendLabel());

            }

            else{

                questionReplyView = buildEditView(lparams, questionsArrayList.get(i).getstartLabel(),
                        questionsArrayList.get(i).getendLabel(), questionsArrayList.get(i).getquestionId());
                //
            }
            //SeekBar eView =buildEditView(lparams);
            lLayout.addView(tView);
            lLayout.addView(questionReplyView);
            edits[i] = questionReplyView;
            questionsViews[i] = tView;
            layout.addView(lLayout);
        }
    }


    public LinearLayout buildEditView(ViewGroup.LayoutParams layoutParams,
                                      String startLabelStr,
                                      String endLabelStr,
                                      String questionId) {

        LinearLayout lLayout = new LinearLayout(this);
        lLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lparams.weight = 1f;
        lparams.setMargins(0, 10, 0, 0);

        SeekBar bar = new SeekBar(this);
        bar.setMax(5);
        bar.setBackground(getDrawable(R.drawable.edit_text));
        bar.setLayoutParams(layoutParams);
        bar.setId(Integer.parseInt(questionId));
        lLayout.addView(bar);


        RelativeLayout labelLayout = new RelativeLayout(this);
        //labelLayout.setOrientation(LinearLayout.HORIZONTAL);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        //params.addRule(RelativeLayout.LEFT_OF, R.id.id_to_be_left_of);

        TextView label_left = new TextView(this);
        label_left.setTextColor(Color.WHITE);
        label_left.setLayoutParams(layoutParams);
        label_left.setText(startLabelStr);

        TextView label_right = new TextView(this);
        label_right.setTextColor(Color.WHITE);
        label_right.setLayoutParams(params);
        label_right.setText(endLabelStr);

        //labelLayout.addView(label_left);
        //labelLayout.addView(label_right);

        labelLayout.addView(label_left);
        labelLayout.addView(label_right);
        lLayout.addView(labelLayout);

        return lLayout;
    }

    public RadioGroup buildRadioGroupView(int count,ViewGroup.LayoutParams layoutParams,
                                          String startLabelStr,
                                          String endLabelStr) {


        RadioGroup rg = new RadioGroup(this);
        rg.setOrientation(RadioGroup.VERTICAL);
        rg.setLayoutParams(layoutParams);
        rg.setId(count++);
        AppCompatRadioButton startLabel  = new AppCompatRadioButton(this);
        //startLabel.button
        AppCompatRadioButton endLabel  = new AppCompatRadioButton(this);
        rg.addView(startLabel, layoutParams);
        rg.addView(endLabel, layoutParams);
        startLabel.setText(startLabelStr);
        startLabel.setId(count++);
        endLabel.setId(count++);
        startLabel.setSelected(true);
        startLabel.setTextColor(Color.WHITE);
        endLabel.setTextColor(Color.WHITE);
        //startLabel.setBackground(getDrawable(R.drawable.edit_text));
        //endLabel.setBackground(getDrawable(R.drawable.edit_text));
        endLabel.setText(endLabelStr);
        rg.check(startLabel.getId());
        rg.setBackground(getDrawable(R.drawable.edit_text));

        ColorStateList colorStateList = new ColorStateList(
                new int[][]{

                        new int[]{-android.R.attr.state_enabled}, //disabled
                        new int[]{android.R.attr.state_enabled} //enabled
                },
                new int[] {
                        Color.BLACK //disabled
                        ,Color.parseColor("#25a396") //enabled

                }
        );
        startLabel.setSupportButtonTintList(colorStateList);
        endLabel.setSupportButtonTintList(colorStateList);
        return rg;
    }

    public TextView buildTextView(int i, String question, ViewGroup.LayoutParams lparams) {
        TextView edit_text = new TextView(this);
        edit_text.canScrollHorizontally(0);
        edit_text.setTextColor(Color.WHITE);
        edit_text.setMaxLines(100);
        edit_text.setLayoutParams(lparams);
        edit_text.setText(i + 1 + ") " + question);
        return edit_text;
    }



    class SendFeedback extends AsyncTask<HashMap<String, Object>, Void, JSONObject> {

        protected JSONObject doInBackground(HashMap<String, Object>... params) {
            JSONObject object = null;
            try {

                System.out.println("params: " + params.toString());
                System.out.println("params length: " +  params.length);
                for (String key: params[0].keySet()){
                    System.out.println(key + ": " + params[0].get(key));
                }

                //String feedback = params[0];
                //System.out.println("feedback " + feedback);
                InputStream stream = BuildConnections.buildPostConnection(Constant.SERVER_ADDRESS+"Questionnaire", params[0]);
                //InputStream stream = BuildConnections.buildConnection(QUESTION_URL + "?requestType=feedback" + feedback);
                object = BuildConnections.getJSOnObject(stream);
                ParameterFile.positiveColor = object.getString(Constant.POSITIVE_COLOR);
                ParameterFile.negativeColor = object.getString(Constant.NEGATIVE_COLOR);
                ParameterFile.sessionID = object.getLong(Constant.SESSION_ID);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //System.out.println("params: " + params.toString());
            return object;

        }

        protected void onPostExecute(JSONObject jsonObject) {

            try {

                String val = jsonObject.getString("save");
                if (val.equalsIgnoreCase("successful")) {

                    if (ParameterFile.QuestionSession == 1) {
                        createNewActivity(HomeScreen.class, "Thanks for playing\n" + ParameterFile.userName);
                    } else {
                        new FetchImageParameter().execute();
                        System.out.println("value of demoplayed "+demoPlayed);
                        if (demoPlayed) {
                            startNewActivity(DemoColorActivity.class);
                        } else {
                            System.out.println("in else playdemo false "+demoPlayed);
                        startNewActivity(PlayGame.class);
                        }
                    }
                } else
                    buildAlertDialog(jsonObject.getString("message" + "\nPlease try again."));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }



    public void buildAlertDialog(String message) {

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(questions);
        alertDialogBuilder.setTitle("Error in saving feedback");
        boolean check = false;
        // set dialog message
        alertDialogBuilder
                .setMessage(message + "\nPlease try again")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void startNewActivity(Class classToBePlayed) {
        Intent intent = new Intent(Questions.this, classToBePlayed);
        intent.putExtra("speed", 100);
        Questions.this.startActivity(intent);
    }

    public static String convertHMToString(HashMap<String, String> hashMap){

        Iterator<Map.Entry<String, String>> iterator = hashMap.entrySet().iterator();
        StringBuilder str = new StringBuilder();
        str.append("{");
        while(iterator.hasNext()){
            Map.Entry<String, String> pair = iterator.next();

            str.append("\""+pair.getKey()+"\":");
            str.append("\""+pair.getValue()+"\"");
            str.append(",");
        }
        int length = str.length();
        str.deleteCharAt(length-1);
        str.append("}");
        return str.toString();
    }


    public HashMap<String, Object> getResults() {


        HashMap<String, Object> results = new HashMap<String, Object>();

        ArrayList<String> responses = new ArrayList<String>();

        for (int i = 0; i < questionsViews.length; i++) {
            HashMap<String, String> response = new HashMap<String, String>();
            response.put("questionId", questionsArrayList.get(i).getquestionId());
            //if(edits[i] instanceof SeekBar){
            if(edits[i] instanceof RadioGroup){
                // Extract which button is selected
                // Google how to extract which button is selected for the given radioGroup.
                AppCompatRadioButton temp = (AppCompatRadioButton)((RadioGroup)edits[i]).findViewById(((RadioGroup)edits[i]).getCheckedRadioButtonId());
                response.put("response",temp.getText().toString());
                response.put("responseType", "Categorical");
            }else if(edits[i] instanceof LinearLayout){
                LinearLayout layout = (LinearLayout) edits[i];
                int count = layout.getChildCount();
                View v;
                for(int j=0;j<count;j++) {
                    v = layout.getChildAt(j);
                    if(v.getId() == Integer.parseInt(questionsArrayList.get(i).getquestionId())){
                        //System.out.println("Seekbar found");
                        SeekBar seekBar = (SeekBar)v;
                        response.put("response",Integer.toString(seekBar.getProgress()));
                        response.put("responseType", "Continuous");
                        break;
                    }
                }
                //int process = ((SeekBar)(((LinearLayout)edits[i]).
                //        findViewById(Integer.parseInt(questionsArrayList.get(i).getquestionId())))).getProgress();
                //System.out.print(process);
                //response.put("response",Integer.toString(process));
                //response.put("responseType", "Continuous");
            }
            responses.add(convertHMToString(response));
            //String answer = getAnswer(((SeekBar)edits[i]).getProgress());
        }
        // okay
        results.put(Constant.RESPONSES, responses);
        results.put(Constant.USER_ID, Long.toString(ParameterFile.userID));
        results.put(Constant.TG_ID, Long.toString(ParameterFile.tgId));
        results.put(Constant.PARTICIPANTID, Long.toString(ParameterFile.participantId));
        results.put(Constant.SESSION_ID, Long.toString(ParameterFile.sessionID));
        results.put(Constant.QUESTION_SESSION, ParameterFile.QuestionSession);

        //results.put("sessionDate",(new Date()).toString());
        //results.put("questionSession",Integer.toString(ParameterFile.QuestionSession));
        //return questions + answers + "&userID=" + ParameterFile.userID + "&sessionID=" + ParameterFile.sessionID +
        //        "&sessionDate=" + (new Date()).toString() + "&questionSession=" + ParameterFile.QuestionSession;
        return results;

    }

    public String getAnswer(int progress) {
        String result = "";
        switch (progress) {
            case 0:
                result = Register.encodeString("Very Slightly or not at all");
                break;
            case 1:
                result = Register.encodeString("A little");
                break;
            case 2:
                result = Register.encodeString("Moderatly");
                break;
            case 3:
                result = Register.encodeString("Quite a bit");
                break;
            case 4:
                result = Register.encodeString("Extremely");
                break;
        }
        return result;
    }

    public void onBackPressed() {

        return;
    }


    protected void onDestroy() {
        super.onDestroy();
        System.out.println("Done on Destroy");
    }


}
