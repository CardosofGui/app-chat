package com.example.appchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class MenuChatGlobal extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout LinearGrupos;
    private String usuarioLogado;
    private BootstrapButton btnCriarChat;

    private FirebaseDatabase mDatabase;

    private AlertDialog dialog;
    private  AlertDialog.Builder dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_chat_global);

        setTitle("CHAT GLOBAL");

        // Identificando IDs
        Intent it = getIntent();
        usuarioLogado = it.getStringExtra("usuarioLogado");
        btnCriarChat = findViewById(R.id.btnCriarChatGlobal);

        // Firabase
        mDatabase = FirebaseDatabase.getInstance();

        exibirGrupos();

        btnCriarChat.setOnClickListener(this);
    }

    private void exibirGrupos() {

        DatabaseReference reference = mDatabase.getReference().child("BD").child("chatGrupoPublico");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot data : snapshot.getChildren()){

                    String nomeChat = data.child("nomeChat").getValue(String.class);
                    String descChat = data.child("descChat").getValue(String.class);
                    String idChat = data.getKey();


                    gerarGrupos(nomeChat, idChat, descChat);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void gerarGrupos(final String nomeChat, final String idChat, String descChat) {

        LinearGrupos = findViewById(R.id.linearGruposPublico);

        LinearLayout linearGrupo = new LinearLayout(this);
        TextView nomeDoChat = new TextView(this);
        TextView descDoChat = new TextView(this);

        LinearLayout.LayoutParams linearGrupoParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearGrupo.setOrientation(LinearLayout.VERTICAL);
        linearGrupoParam.setMargins(10, 10, 10, 10);
        linearGrupo.setPadding(25, 20, 25, 20);
        linearGrupo.setLayoutParams(linearGrupoParam);
        linearGrupo.setBackground(getDrawable(R.drawable.criar_grupos));

        LinearLayout.LayoutParams txtMensagemParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        nomeDoChat.setLayoutParams(txtMensagemParams);
        nomeDoChat.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        nomeDoChat.setText(nomeChat.toUpperCase());
        nomeDoChat.setTextSize((float) 24);
        nomeDoChat.setTypeface(Typeface.DEFAULT_BOLD);
        nomeDoChat.setTextColor(getColor(R.color.nomeChat));

        descDoChat.setLayoutParams(txtMensagemParams);
        descDoChat.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        descDoChat.setText(descChat);
        descDoChat.setTextSize((float) 14);
        descDoChat.setTextColor(getColor(R.color.nomeChat));


        linearGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent it = new Intent(getBaseContext(), ChatGrupoPublico.class);
                it.putExtra("idChat", idChat);
                it.putExtra("usuarioLogado", usuarioLogado);
                it.putExtra("nomeChat", nomeChat);

                startActivity(it);
            }
        });

        linearGrupo.addView(nomeDoChat);
        linearGrupo.addView(descDoChat);
        LinearGrupos.addView(linearGrupo);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.btnCriarChatGlobal:
                criarPopupCriaChat();
                break;
        }
    }

    private void criarPopupCriaChat() {

        dialogBuilder = new AlertDialog.Builder(this);
        final View criarChat = getLayoutInflater().inflate(R.layout.popup_cria_chat, null);


        final BootstrapButton btnCriaChatConfirmar = criarChat.findViewById(R.id.btnCriarChatConfirmar);
        final BootstrapEditText edtNomeChat = criarChat.findViewById(R.id.edtNomeChat);
        final BootstrapEditText edtDescChat = criarChat.findViewById(R.id.edtDescricaoChat);

        btnCriaChatConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nomeChat = edtNomeChat.getText().toString();
                String descChat = edtDescChat.getText().toString();

                if(nomeChat.isEmpty() || descChat.isEmpty()){
                    Toast.makeText(MenuChatGlobal.this, "Erro - Preencha todos os campos", Toast.LENGTH_SHORT).show();
                }else{
                    DatabaseReference mReferece = mDatabase.getReference().child("BD").child("chatGrupoPublico");

                    String key = mReferece.push().getKey();

                    mReferece.child(key).child("nomeChat").setValue(nomeChat);
                    mReferece.child(key).child("descChat").setValue(descChat);

                    gerarGrupos(nomeChat, key, descChat);

                    dialog.cancel();
                }

            }
        });


        dialogBuilder.setView(criarChat);
        dialog = dialogBuilder.create();
        dialog.show();

    }
}