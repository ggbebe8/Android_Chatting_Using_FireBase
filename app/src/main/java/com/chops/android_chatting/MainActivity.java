package com.chops.android_chatting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    //이메일 입력
    EditText metEmail;
    //패스워드 입력
    EditText metPassword;
    //로그인 중
    ProgressBar pbLogin;

    //파이어베이스 데이터베이스
    FirebaseDatabase mDatabase;
    //파이어베이스 인증 관련
    private FirebaseAuth mAuth;

    //et의 키보드를 내리자.
    View.OnFocusChangeListener mliKeyboardDown;
    //키보드 컨트롤
    InputMethodManager mIMM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mfnInitVariable();
        mfnListener();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    //변수 및 객체 초기화
    private void mfnInitVariable()
    {
        //파이어베이스 데이터베이스
        mDatabase = FirebaseDatabase.getInstance();
        //파이어베이스 인증 초기화
        mAuth = FirebaseAuth.getInstance();
        //textview 이메일
        metEmail = (EditText)findViewById(R.id.etEmail);
        //textview 패스워드
        metPassword = (EditText)findViewById(R.id.etPassword);
        //로딩 중.. 표시
        pbLogin = (ProgressBar)findViewById(R.id.pbLogin);
    }

    //리스너
    private void mfnListener()
    {
        //등록 버튼
        Button btnRegister = (Button)findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(v.getContext(), com.chops.android_chatting.Register.class);
                startActivity(in);
            }
        });

        //로그인 버튼
        Button btnLogin = (Button)findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mfnUserLogin(metEmail.getText().toString(), metPassword.getText().toString());
            }
        });

        //취소 버튼
        Button btnCancel = (Button)findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                metEmail.setText("");
                metPassword.setText("");
            }
        });

        //키보드 내리자.
        mIMM = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mliKeyboardDown = new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View view, boolean bFocus) {
                if(!bFocus && !(getCurrentFocus() instanceof EditText))
                {
                    mIMM.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                else
                {
                    mIMM.showSoftInput(view,0);
                }
            }
        };
        metEmail.setOnFocusChangeListener(mliKeyboardDown);
        metPassword.setOnFocusChangeListener(mliKeyboardDown);
    }


    private void updateUI(FirebaseUser p_currentUser)
    {
        return;
    }

    //로그인
    private void mfnUserLogin(String p_strEmail, String p_strPassword)
    {
        //예외처리
        if(metEmail.getText().toString().equals("") || metEmail.getText() == null) {
            Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_LONG).show();
            return;
        }
        else if (metPassword.getText().toString().equals("") || metPassword.getText() == null) {
            Toast.makeText(this, "패스워드를 입력해주세요.", Toast.LENGTH_LONG).show();
            return;
        }
        pbLogin.setVisibility(View.VISIBLE);

        //로그인 인증
        mAuth.signInWithEmailAndPassword(p_strEmail, p_strPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            pbLogin.setVisibility(View.GONE);
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithEmail:success");
                            //Toast.makeText( MainActivity.this, "로그인 성공",Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

                            //성공 시, 뷰 전환.
                            Intent in = new Intent(MainActivity.this, tabChatMain.class);
                            in.putExtra("Email", metEmail.getText().toString());
                            startActivity(in);
                            finish();

                        } else {
                            pbLogin.setVisibility(View.GONE);
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "로그인 실패",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }
}
