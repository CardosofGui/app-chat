package com.example.appchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.example.appchat.Util.UtilFirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private BootstrapEditText edtEmail, edtSenha;
    private BootstrapButton btnLogar;
    private AwesomeTextView txtIrCadastro, txtRecuperarSenha;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        verificacaoAutenticacao();


        // Identificando Ids
        edtEmail = findViewById(R.id.edtEmailLogin);
        edtSenha = findViewById(R.id.edtSenhaLogin);
        btnLogar = findViewById(R.id.btnEntrarLogin);
        txtIrCadastro = findViewById(R.id.txtIrCadastro);
        txtRecuperarSenha = findViewById(R.id.txtRecuperarSenha);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        // Ação click
        btnLogar.setOnClickListener(this);
        txtIrCadastro.setOnClickListener(this);
        txtRecuperarSenha.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnEntrarLogin){
            String email = edtEmail.getText().toString();
            String senha = edtSenha.getText().toString();

            RealizarLogin(email, senha);

        }else if(v.getId() == R.id.txtIrCadastro){
            startActivity(new Intent(this, Cadastrar.class));
        }else if(v.getId() == R.id.txtRecuperarSenha){
            String email = edtEmail.getText().toString();

            if(email.isEmpty()){
                Toast.makeText(this, "Erro - preencha o campo de e-mail para que seja feito a redefinição", Toast.LENGTH_SHORT).show();
            }else{
                UtilFirebaseAuth.redefinirSenha(mAuth, this, email);
            }
        }
    }

    private void RealizarLogin(String email, String senha){

        if(email.isEmpty() || senha.isEmpty()){
            Toast.makeText(this, "Erro - Preencha todos os campos", Toast.LENGTH_SHORT).show();
        }else {
            if (UtilFirebaseAuth.verificandoRede(this)) {
                mAuth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(getBaseContext(), "Sucesso - Usuario logado com sucesso", Toast.LENGTH_SHORT).show();
                        } else {
                            UtilFirebaseAuth.tratandoErros(getBaseContext(), task.getException().toString());
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Erro - Verifique sua conexão com a internet", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void verificacaoAutenticacao(){
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mUser = firebaseAuth.getCurrentUser();

                if(mUser == null){
                }else{

                    DatabaseReference reference = mDatabase.getReference().child("BD").child("Usuario").child(mAuth.getCurrentUser().getUid());

                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String user = snapshot.child("user").getValue(String.class);

                            Intent it = new Intent(getBaseContext(), EscolhaChat.class);
                            it.putExtra("usuarioLogado", user);

                            startActivityForResult(it, 1);
                            Toast.makeText(getBaseContext(), "Bem vindo de volta "+user, Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mAuthStateListener != null){
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
}