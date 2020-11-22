package com.example.appchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class MenuChatGlobal extends AppCompatActivity {

    private LinearLayout LinearGrupos;
    private String usuarioLogado;

    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_chat_global);

        // Identificando IDs
        Intent it = getIntent();
        usuarioLogado = it.getStringExtra("usuarioLogado");

        // Firabase
        mDatabase = FirebaseDatabase.getInstance();

        exibirGrupos();
    }

    private void exibirGrupos() {

        DatabaseReference reference = mDatabase.getReference().child("BD").child("chatGrupoPublico");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot data : snapshot.getChildren()){

                    String nomeChat = data.child("nomeChat").getValue(String.class);
                    String idChat = data.getKey();


                    gerarGrupos(nomeChat, idChat);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void gerarGrupos(String nomeChat, final String idChat) {

        LinearGrupos = findViewById(R.id.linearGruposPublico);

        BootstrapButton btnGrupo = new BootstrapButton(this);
        ScrollView.LayoutParams btnParamsTeste = new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.WRAP_CONTENT);
        btnParamsTeste.setMargins(0, 25, 0, 15);
        btnGrupo.setLayoutParams(btnParamsTeste);

        btnGrupo.setRounded(true);
        btnGrupo.setText(nomeChat);

        btnGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent it = new Intent(getBaseContext(), ChatGrupoPublico.class);
                it.putExtra("idChat", idChat);
                it.putExtra("usuarioLogado", usuarioLogado);

                startActivity(it);
            }
        });

        LinearGrupos.addView(btnGrupo);
    }
}