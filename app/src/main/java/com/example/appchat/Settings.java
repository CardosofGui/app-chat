package com.example.appchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Settings extends AppCompatActivity implements View.OnClickListener {

    private TextView txtUser;
    private BootstrapEditText edtEmailUser, edtSenhaUser;
    private BootstrapButton btnEditarSenha, btnEditarEmail, btnExcluir;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String UID;

    private AlertDialog dialog;
    private  AlertDialog.Builder dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Identificando IDs
        txtUser = findViewById(R.id.txtUser);
        edtEmailUser = findViewById(R.id.edtEmailUser);
        edtSenhaUser = findViewById(R.id.edtSenhaUser);
        btnEditarEmail = findViewById(R.id.btnAlterarEmail);
        btnEditarSenha = findViewById(R.id.btnAlterarSenha);
        btnExcluir = findViewById(R.id.btnExcluirUser);

        // Firebase
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        exibindoDadosUsuario();

        btnEditarEmail.setOnClickListener(this);
        btnEditarSenha.setOnClickListener(this);
        btnExcluir.setOnClickListener(this);

        UID = mAuth.getCurrentUser().getUid();


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btnExcluirUser:
                criarPopupExcluirPerfil();
                break;
            case R.id.btnAlterarEmail:
                criarPopupEditarPerfil(true);
                break;
            case R.id.btnAlterarSenha:
                criarPopupEditarPerfil(false);
                break;
        }
    }


    private void exibindoDadosUsuario(){
        String UID = mAuth.getCurrentUser().getUid();

        DatabaseReference mReference = mDatabase.getReference().child("BD").child("Usuario").child(UID);

        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Intent intentLocal = getIntent();

                String email = snapshot.child("email").getValue(String.class);
                String senha = snapshot.child("senha").getValue(String.class);
                String user = intentLocal.getStringExtra("usuarioLogado");


                txtUser.setText(user);
                edtEmailUser.setText(email);
                edtSenhaUser.setText(senha);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void criarPopupExcluirPerfil() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View apagarPerfil = getLayoutInflater().inflate(R.layout.popup_excluir_perfil, null);

        final BootstrapButton btnCancelar = apagarPerfil.findViewById(R.id.btnCancelar);
        final BootstrapButton btnConfirmar = apagarPerfil.findViewById(R.id.btnConfirmar);

        final FirebaseUser deletarUser = mAuth.getCurrentUser();

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(Settings.this, "botao clicado", Toast.LENGTH_SHORT).show();
                 deletarUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            Toast.makeText(Settings.this, "Sucesso - Conta deletada com sucesso", Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                            deletarDatabase();
                            startActivity(new Intent(getBaseContext(), MainActivity.class));
                            finish();
                        }else{
                            Toast.makeText(Settings.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialogBuilder.setView(apagarPerfil);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    private void deletarDatabase() {
        final DatabaseReference mReference = mDatabase.getReference().child("BD");

        mReference.child("Usuario").child(UID).removeValue();

        mReference.child("chat").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot data : snapshot.getChildren()){
                    if(data.child("user1").getValue(String.class).equals(txtUser.getText().toString()) || data.child("user2").getValue(String.class).equals(txtUser.getText().toString())){
                        String idChat = data.getKey();

                        mReference.child("chat").child(idChat).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void criarPopupEditarPerfil(boolean emailOuSenha) {
        dialogBuilder = new AlertDialog.Builder(this);
        final View editarPerfil = getLayoutInflater().inflate(R.layout.popup_editar_perfil, null);

        final BootstrapEditText atualizarCredencial = editarPerfil.findViewById(R.id.edtAtualizarCredencial);
        final BootstrapEditText edtSenhaPadrao = editarPerfil.findViewById(R.id.edtSenhaPadrao);
        BootstrapButton btnCancelar = editarPerfil.findViewById(R.id.btnCancelarEditar);
        BootstrapButton btnSalvar = editarPerfil.findViewById(R.id.btnSalvarEditar);
        final TextView txtEditar = editarPerfil.findViewById(R.id.txtEditar);

        if(emailOuSenha){
            atualizarCredencial.setHint(R.string.email_editar);
            txtEditar.setText("ALTERAR E-MAIL");

            btnSalvar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    atualizarEmail(atualizarCredencial.getText().toString(), edtSenhaPadrao.getText().toString());
                }
            });

        }else{
            atualizarCredencial.setHint(R.string.senha_editar);
            txtEditar.setText("ALTERAR SENHA");

            btnSalvar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    atualizarSenha(atualizarCredencial.getText().toString(), edtSenhaPadrao.getText().toString());
                }
            });
        }



        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });


        dialogBuilder.setView(editarPerfil);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    private void atualizarEmail(final String novoEmail, String senhaVerificar){

        if(senhaVerificar.equals(edtSenhaUser.getText().toString())){
            mUser.updateEmail(novoEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(Settings.this, "Sucesso - E-mail alterado com sucesso", Toast.LENGTH_SHORT).show();

                        String UID = mAuth.getCurrentUser().getUid();
                        DatabaseReference mReference = mDatabase.getReference().child("BD").child("Usuario").child(UID);

                        mReference.child("email").setValue(novoEmail);

                        mAuth.signOut();
                        finish();
                        startActivity(new Intent(getBaseContext(), MainActivity.class));

                    }else{
                        Toast.makeText(Settings.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            Toast.makeText(this, "Erro - Senha incorreta", Toast.LENGTH_SHORT).show();
        }


    }

    private void atualizarSenha(final String novaSenha, String senhaVerificar){

        if(senhaVerificar.equals(edtSenhaUser.getText().toString())){
            mUser.updatePassword(novaSenha).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(Settings.this, "Sucesso - Senha alterado com sucesso", Toast.LENGTH_SHORT).show();

                        String UID = mAuth.getCurrentUser().getUid();
                        DatabaseReference mReference = mDatabase.getReference().child("BD").child("Usuario").child(UID);

                        mReference.child("senha").setValue(novaSenha);

                        mAuth.signOut();
                        finish();
                        startActivity(new Intent(getBaseContext(), MainActivity.class));
                    }else{
                        Toast.makeText(Settings.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            Toast.makeText(Settings.this, "Erro - senha incorreta", Toast.LENGTH_SHORT).show();
        }
    }

}