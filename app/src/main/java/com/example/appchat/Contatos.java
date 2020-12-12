package com.example.appchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.api.attributes.BootstrapBrand;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Contatos extends AppCompatActivity implements View.OnClickListener {

    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;

    private EditText edtAdicionarAmigo;
    private Button btnAdicionarAmigo;

    private String usuarioLogado;
    private String chatSelecionado;

    private  ChildEventListener atualizarContato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contatos);

        setTitle("CONTATOS");

        edtAdicionarAmigo = findViewById(R.id.edtAdicionarAmigo);
        btnAdicionarAmigo = findViewById(R.id.btnAdicionarAmigo);


        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Intent intentLocal = getIntent();
        usuarioLogado = intentLocal.getStringExtra("usuarioLogado");


        btnAdicionarAmigo.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        setAtualizarContato();
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnAdicionarAmigo){
            String amigo = edtAdicionarAmigo.getText().toString();

            adicionarAmigo(amigo);

            edtAdicionarAmigo.setText("");
        }
    }




    private void adicionarAmigo(final String amigo){

        final DatabaseReference reference = mDatabase.getReference().child("BD");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                boolean verificacaoUsuarioAdicionado = true;
                boolean verificacaoUsuarioExistente = true;

                if(snapshot.child("chat").exists()){
                    for(DataSnapshot data2 : snapshot.child("chat").getChildren()){
                        if(data2.child("user1").getValue(String.class).equals(usuarioLogado) && data2.child("user2").getValue(String.class).equals(amigo) || data2.child("user1").getValue(String.class).equals(amigo) && data2.child("user2").getValue(String.class).equals(usuarioLogado)){
                            Toast.makeText(Contatos.this, "Usuario já adicionado", Toast.LENGTH_SHORT).show();
                            verificacaoUsuarioAdicionado = false;
                            break;
                        }else{
                            verificacaoUsuarioAdicionado = true;
                        }
                    }
                }

                for (DataSnapshot data : snapshot.child("Usuario").getChildren()) {
                    if(data.child("user").getValue(String.class).equals(amigo)){
                        verificacaoUsuarioExistente = true;
                        break;
                    }else{
                        verificacaoUsuarioExistente = false;
                    }
                }

                if(verificacaoUsuarioAdicionado == true && verificacaoUsuarioExistente == true){
                    if(amigo.equals(usuarioLogado)){
                        Toast.makeText(Contatos.this, "Você não pode adicionar você mesmo", Toast.LENGTH_SHORT).show();
                    }else{
                        String key = reference.child("chat").push().getKey();

                        reference.child("chat").child(key).child("user1").setValue(usuarioLogado);
                        reference.child("chat").child(key).child("user2").setValue(amigo);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void gerarTxtView(final String usuarioADD, final String chatSelecionar){

        LinearLayout linearContatos = findViewById(R.id.contatos);
        AwesomeTextView txtContato = new AwesomeTextView(this);

        ScrollView.LayoutParams txtContatoParams = new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.WRAP_CONTENT);
        txtContatoParams.setMargins(0,20,0,20);

        txtContato.setLayoutParams(txtContatoParams);
        txtContato.setPadding(25,25,25,25);
        txtContato.setBackgroundColor(Color.parseColor("#717171"));
        txtContato.setText(usuarioADD);
        txtContato.setClickable(true);
        txtContato.setTypeface(Typeface.DEFAULT_BOLD);
        txtContato.setTextColor(Color.parseColor("#000000"));



        txtContato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent it = new Intent(getBaseContext(), ChatFriend.class);
                it.putExtra("usuarioLogado", usuarioLogado);
                it.putExtra("amigoChat", usuarioADD);
                it.putExtra("chatSelecionado", chatSelecionar);


                startActivity(it);
                finish();
            }
        });

        linearContatos.addView(txtContato);
    }

    private void setAtualizarContato(){

        DatabaseReference reference = mDatabase.getReference().child("BD").child("chat");

        atualizarContato = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                if(snapshot.child("user1").getValue(String.class).equals(usuarioLogado)){
                    String key = snapshot.getKey();
                    String amigo = snapshot.child("user2").getValue(String.class);

                    gerarTxtView(amigo, key);
                }else if(snapshot.child("user2").getValue(String.class).equals(usuarioLogado)){
                    String key = snapshot.getKey();
                    String amigo = snapshot.child("user1").getValue(String.class);

                    gerarTxtView(amigo, key);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.child("user1").getValue(String.class).equals(usuarioLogado)){
                    String key = snapshot.getKey();
                    String amigo = snapshot.child("user2").getValue(String.class);

                    gerarTxtView(amigo, key);
                }else if(snapshot.child("user2").getValue(String.class).equals(usuarioLogado)){
                    String key = snapshot.getKey();
                    String amigo = snapshot.child("user1").getValue(String.class);

                    gerarTxtView(amigo, key);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        reference.addChildEventListener(atualizarContato);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(atualizarContato != null){
            mDatabase.getReference().removeEventListener(atualizarContato);
        }
    }

}