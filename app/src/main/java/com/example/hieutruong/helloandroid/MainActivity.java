package com.example.hieutruong.helloandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;

    private EditText metTextHint;
    private ListView mlvTextMatches;
    private Spinner msTextMatches;
    private Button mbtSpeak;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        metTextHint = (EditText) findViewById(R.id.etTextHint);
        mlvTextMatches = (ListView) findViewById(R.id.lvTextMatches);
        msTextMatches = (Spinner) findViewById(R.id.sNoOfMatches);
        mbtSpeak = (Button) findViewById(R.id.btSpeak);

        checkVoiceRecognition();
    }

    public void checkVoiceRecognition() {
        Log.v("", "checkVoiceRecognition checkVoiceRecognition");
        // Kiem tra thiet bi cho phep nhan dang giong noi hay ko
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0) {
            mbtSpeak.setEnabled(false);
            Toast.makeText(this, "Voice recognizer not present", Toast.LENGTH_SHORT).show();
        }
    }

    // Gui tap tin am thanh
    public void speak(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // xac nhan ung dung muon gui yeu cau
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());

        // goi y nhung dieu nguoi dung muon noi
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, metTextHint.getText().toString());

        // goi y nhan dang nhung gi nguoi dung se noi
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);

        // Kiem tra item muon hien thi da chon tron spinner
        if (msTextMatches.getSelectedItemPosition() == AdapterView.INVALID_POSITION) {
            Toast.makeText(this, "Please select No. of Matches from spinner", Toast.LENGTH_SHORT).show();
            return;
        }

        int noOfMatches = Integer.parseInt(msTextMatches.getSelectedItem().toString());

        // Xac dinh ban muon bao nhieu ket qua gan dung duoc tra ve
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, noOfMatches);

        // Gui yeu cau di
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    // Su kien nhan lai ket qua
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE)

            // Truong hop co gia tri tra ve
            if(resultCode == RESULT_OK) {
                ArrayList<String> textMatchList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                if (!textMatchList.isEmpty()) {
                    // kiem tra neu co chua tu khoa 'search' thi se bat dau tim kiem tren web
                    if (textMatchList.get(0).contains("search")) {
                        String searchQuery = textMatchList.get(0).replace("search", " ");
                        Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
                        search.putExtra(SearchManager.QUERY, searchQuery);
                        startActivity(search);
                    } else {
                        // Hien thi ket qua
                        mlvTextMatches.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, textMatchList));
                    }
                }
                // Cac truong hop loi
            } else if (resultCode == RecognizerIntent.RESULT_AUDIO_ERROR){
                showToastMessage("Audio Error");
            } else if (resultCode == RecognizerIntent.RESULT_CLIENT_ERROR){
                showToastMessage("Client Error");
            } else if (resultCode == RecognizerIntent.RESULT_NETWORK_ERROR){
                showToastMessage("Network Error");
            } else if (resultCode == RecognizerIntent.RESULT_NO_MATCH){
                showToastMessage("No Match");
            } else if (resultCode == RecognizerIntent.RESULT_SERVER_ERROR){
                showToastMessage("Server Error");
            }
        super.onActivityResult(requestCode, resultCode, data);
    }

    void showToastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
