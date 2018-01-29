package com.application.medicare.medicare;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.application.medicare.medicare.patient.PatientListActivity;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        final EditText idText = (EditText) findViewById(R.id.idText);
        final EditText passwordText = (EditText) findViewById(R.id.passwordText);

        Button loginButton = (Button) findViewById(R.id.loginButton);
        TextView joinButton =(TextView) findViewById(R.id.joinButton);

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent joinIntent = new Intent(LoginActivity.this, JoinActivity.class);
                LoginActivity.this.startActivity(joinIntent);

            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userId = idText.getText().toString();
                final String userPassword = passwordText.getText().toString();

                LoginNetwork network = new LoginNetwork();
                Map<String,String> params = new HashMap<>();
                params.put("userId",userId);
                params.put("userPassword",userPassword);

                network.execute(params);

            }
        });

    }

    public class LoginNetwork extends AsyncTask<Map<String,String>, Integer,String> {
        /** * doInBackground 실행되기 이전에 동작한다. */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Map<String,String>...maps){
            // HTTP 요청 준비 작업
            HttpClient.Builder http = new HttpClient.Builder("POST", "http://192.168.43.151:8080/test/login");

            http.addAllParameters(maps[0]);
            //http.addAllParameters(params);

            // HTTP 요청 전송
            HttpClient post = http.create(); post.request();

            // 응답 상태코드 가져오기
            int statusCode = post.getHttpStatusCode();

            // 응답 본문 가져오기
            String body = post.getBody();

            return body;
        }
        /** * doInBackground 종료되면 동작한다. * @param s : doInBackground가 리턴한 값이 들어온다. */

        @Override
        protected void onPostExecute(String s) {

            // Log.d("JSON_RESULT", s);

            Gson gson = new Gson();
            Data data = gson.fromJson(s,Data.class);

            if(data.getData1().equals("success")){

                //AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

                Intent intent = new Intent(LoginActivity.this , PatientListActivity.class);
                intent.putExtra("userId",data.getUserId());
                intent.putExtra("userPassword",data.getUserPassword());
                LoginActivity.this.startActivity(intent); //메인화면으로 값 넘겨주기

            }else if(data.getData1().equals("fail")){
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this); //현재창에서 성공 알람창 띄우기
                builder.setMessage("로그인에 실패 했습니다.")
                        .setNegativeButton("다시시도",null)
                        .create()
                        .show();

            }


        }
    }

}