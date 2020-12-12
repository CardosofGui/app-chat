package com.example.appchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.example.appchat.Data.Usuario;
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

public class Cadastrar extends AppCompatActivity implements View.OnClickListener {

    private BootstrapEditText edtUsuarioCadastro, edtEmailCadastro, edtSenhaCadastro, edtRepetirSenhaCadastro;
    private BootstrapButton btnCadastrar;
    private AwesomeTextView txtIrLogin;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar2);
        getSupportActionBar().hide();


        // Identificando IDs
        edtUsuarioCadastro = findViewById(R.id.edtUsuarioCadastro);
        edtEmailCadastro = findViewById(R.id.edtEmailCadastro);
        edtSenhaCadastro = findViewById(R.id.edtSenhaCadastro);
        edtRepetirSenhaCadastro = findViewById(R.id.edtRepetirSenhaCadastro);
        btnCadastrar = findViewById(R.id.btnEntrarCadastro);
        txtIrLogin = findViewById(R.id.txtIrLogin);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        // Ação click
        btnCadastrar.setOnClickListener(this);
        txtIrLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnEntrarCadastro){

            String email = edtEmailCadastro.getText().toString().trim();
            String senha = edtSenhaCadastro.getText().toString().trim();
            String senhaRepetir = edtRepetirSenhaCadastro.getText().toString().trim();
            String usuario = edtUsuarioCadastro.getText().toString().trim();

            if(email.isEmpty() || senha.isEmpty() || senhaRepetir.isEmpty() || usuario.isEmpty()){
                Toast.makeText(this, "Erro - Preencha todos os campos", Toast.LENGTH_SHORT).show();
            }else{
                if(senha.equals(senhaRepetir)){
                    CadastrarUsuario(email, senha, usuario);
                }else{
                    Toast.makeText(this, "Erro - Ambas as senhas devem ser iguais", Toast.LENGTH_SHORT).show();
                }
            }

        }else if(v.getId() == R.id.txtIrLogin){
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    private void CadastrarUsuario(final String email, final String senha, final String usuario){

        mAuth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    CadastrarUsuarioBD(usuario, email, senha);
                }else{
                    UtilFirebaseAuth.tratandoErros(getBaseContext(), task.getException().toString());
                }
            }
        });
    }

    private void CadastrarUsuarioBD(final String usuario, final String email, final String senha) {

        DatabaseReference mReference = mDatabase.getReference().child("BD").child("Usuario");


        mUser = mAuth.getCurrentUser();
        String key = mUser.getUid();

        Usuario usuarioCreate = new Usuario(usuario, email, senha);

        mReference.child(key).setValue(usuarioCreate);

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}