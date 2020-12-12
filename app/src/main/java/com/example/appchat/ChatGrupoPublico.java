package com.example.appchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatGrupoPublico extends AppCompatActivity implements View.OnClickListener {

    private ScrollView scrollMensagens;
    private EditText edtMensagem;
    private Button btnEnviarMensagem;

    private FirebaseDatabase mDatabase;
    private ChildEventListener atualizarMensagens;

    private String usuarioLogado;
    private String usuarioSolicitado;
    private String chatSelecionado;

    private AlertDialog dialog;
    private  AlertDialog.Builder dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_grupo_publico);

        setTitle(getIntent().getStringExtra("nomeChat").toUpperCase());

        // Identificando IDs
        edtMensagem = findViewById(R.id.edtMensagem);
        btnEnviarMensagem = findViewById(R.id.btnEnviarMensagem);
        scrollMensagens = findViewById(R.id.scrollMensagens);

        // Firebase
        mDatabase = FirebaseDatabase.getInstance();

        // Intent
        Intent intentlocal = getIntent();
        usuarioLogado = intentlocal.getStringExtra("usuarioLogado");
        chatSelecionado = intentlocal.getStringExtra("idChat");

        // Ação click
        btnEnviarMensagem.setOnClickListener(this);

        // Gerar mensagens
    }

    @Override
    protected void onStart() {
        super.onStart();
        atualizarMensagensNovas();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnEnviarMensagem){
            String mensagem = edtMensagem.getText().toString();
            if(!mensagem.isEmpty()){
                adicionarMensagem(mensagem);
            }
            edtMensagem.setText("");
        }
    }

    private void adicionarMensagem(String mensagem){

        DatabaseReference reference = mDatabase.getReference().child("BD").child("chatGrupoPublico").child(chatSelecionado);

        String key = mDatabase.getReference(chatSelecionado).push().getKey();
        reference.child(key).child("mensagem").setValue(mensagem);
        reference.child(key).child("user").setValue(usuarioLogado);
    }


    private void gerarTxtViews(String mensagem, String usuario, final String key){

        LinearLayout LayouMensagens = findViewById(R.id.linear_mensagens);

        final LinearLayout LayoutMensagem = new LinearLayout(this);
        LinearLayout.LayoutParams LayouMensagemParams = new LinearLayout.LayoutParams(500, ViewGroup.LayoutParams.WRAP_CONTENT);
        LayoutMensagem.setOrientation(LinearLayout.VERTICAL);
        LayouMensagemParams.setMargins(10, 10, 10, 10);
        LayoutMensagem.setPadding(25, 20, 25, 20);
        LayoutMensagem.setLayoutParams(LayouMensagemParams);

        TextView txtUser = new TextView(this);
        LinearLayout.LayoutParams txtUserParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        txtUser.setLayoutParams(txtUserParams);
        txtUser.setTypeface(Typeface.DEFAULT_BOLD);

        final TextView txtMensagem = new TextView(this);
        LinearLayout.LayoutParams txtMensagemParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        txtMensagem.setLayoutParams(txtMensagemParams);
        txtMensagem.setText(mensagem);

        if(usuario.equals(usuarioLogado)){
            LayouMensagemParams.gravity = Gravity.RIGHT;
            LayoutMensagem.setBackground(getDrawable(R.drawable.mensagem_enviada));
            txtUser.setText("Você");

            LayoutMensagem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    criarPopupEditarMensagem(key, LayoutMensagem, txtMensagem);
                    return false;
                }
            });
        }else{
            LayouMensagemParams.gravity = Gravity.LEFT;
            LayoutMensagem.setBackground(getDrawable(R.drawable.mensagem_recebida));
            txtUser.setText(usuario);
        }


        LayoutMensagem.addView(txtUser);
        LayoutMensagem.addView(txtMensagem);
        LayouMensagens.addView(LayoutMensagem);
        scrollMensagens.scrollTo(0, 999999999);
    }

    private void atualizarMensagensNovas(){

        DatabaseReference reference = mDatabase.getReference().child("BD").child("chatGrupoPublico").child(chatSelecionado);

        atualizarMensagens = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                if(snapshot.child("mensagem").exists() && snapshot.child("user").exists()){
                    String mensagem = snapshot.child("mensagem").getValue(String.class);
                    String user = snapshot.child("user").getValue(String.class);
                    String key = snapshot.getKey();

                    gerarTxtViews(mensagem, user, key);
                }
                scrollMensagens.scrollTo(0, 999999999);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.child("mensagem").exists() && snapshot.child("user").exists()){
                    String mensagem = snapshot.child("mensagem").getValue(String.class);
                    String user = snapshot.child("user").getValue(String.class);
                    String key = snapshot.getKey();


                    gerarTxtViews(mensagem, user, key);
                }
                scrollMensagens.scrollTo(0, 999999999);
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

        reference.addChildEventListener(atualizarMensagens);
    }

    private void criarPopupEditarMensagem(final String key, final LinearLayout LayoutMensagem, TextView txtMensagem) {
        dialogBuilder = new AlertDialog.Builder(this);
        final View editarMensagem = getLayoutInflater().inflate(R.layout.popup_editar_excluir_mensagem, null);

        final BootstrapEditText edtMensagem = editarMensagem.findViewById(R.id.edtEdicaoMensagem);
        final BootstrapButton btnExcluir = editarMensagem.findViewById(R.id.btnConfirmarExclusao);

        edtMensagem.setText(txtMensagem.getText().toString());

        btnExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutMensagem.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
                apagarMensagem(key);
                dialog.cancel();
            }
        });


        dialogBuilder.setView(editarMensagem);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    private void apagarMensagem(String key) {

        final DatabaseReference mReference = mDatabase.getReference().child("BD").child("chatGrupoPublico").child(chatSelecionado).child(key);

        mReference.child("mensagem").removeValue();
    }

    @Override
    protected void onResume() {
        LinearLayout LayouMensagens = findViewById(R.id.linear_mensagens);
        LayouMensagens.removeAllViews();

        super.onResume();
    }
}