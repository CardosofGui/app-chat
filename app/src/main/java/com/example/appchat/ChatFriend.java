package com.example.appchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.Reference;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ChatFriend extends AppCompatActivity implements View.OnClickListener {

    private ScrollView scrollMensagens;
    private EditText edtMensagem;
    private Button btnEnviarMensagem;

    private FirebaseDatabase mDatabase;
    private ChildEventListener atualizarMensagens;

    private String usuarioLogado;
    private String usuarioSolicitado;
    private String chatSelecionado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_friend);

        // Identificando IDs
        edtMensagem = findViewById(R.id.edtMensagem);
        btnEnviarMensagem = findViewById(R.id.btnEnviarMensagem);
        scrollMensagens = findViewById(R.id.scrollMensagens);


        // Firebase
        mDatabase = FirebaseDatabase.getInstance();

        // Intent
        Intent intentlocal = getIntent();

        usuarioLogado = intentlocal.getStringExtra("usuarioLogado");
        usuarioSolicitado = intentlocal.getStringExtra("amigoChat");
        chatSelecionado = intentlocal.getStringExtra("chatSelecionado");

        setTitle(usuarioSolicitado);

        // Ação click
        btnEnviarMensagem.setOnClickListener(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
        atualizarMensagensNovas();
        scrollMensagens.scrollTo(0, 999999999);
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

        DatabaseReference reference = mDatabase.getReference().child("BD").child("chat").child(chatSelecionado);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        String time = format.format(calendar.getTime());

        String key = mDatabase.getReference(chatSelecionado).push().getKey();
        reference.child(key).child("mensagem").setValue(mensagem);
        reference.child(key).child("user").setValue(usuarioLogado);
        reference.child(key).child("horario").setValue(time);
    }


    private void gerarTxtViews(String mensagem, String usuario, String horario){



        LinearLayout LayouMensagens = findViewById(R.id.linear_mensagens);

        LinearLayout LayoutMensagem = new LinearLayout(this);
        LinearLayout.LayoutParams LayouMensagemParams = new LinearLayout.LayoutParams(500, ViewGroup.LayoutParams.WRAP_CONTENT);
        LayoutMensagem.setOrientation(LinearLayout.VERTICAL);
        LayouMensagemParams.setMargins(10, 10, 10, 10);
        LayoutMensagem.setPadding(25, 20, 25, 20);
        LayoutMensagem.setLayoutParams(LayouMensagemParams);

        TextView txtUser = new TextView(this);
        LinearLayout.LayoutParams txtUserParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        txtUser.setLayoutParams(txtUserParams);
        txtUser.setTypeface(Typeface.DEFAULT_BOLD);

        TextView txtMensagem = new TextView(this);
        LinearLayout.LayoutParams txtMensagemParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        txtMensagem.setLayoutParams(txtMensagemParams);
        txtMensagem.setText(mensagem);

        TextView txtHorario = new TextView(this);
        LinearLayout.LayoutParams txtHorarioParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        txtHorario.setLayoutParams(txtHorarioParams);
        txtHorario.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
        txtHorario.setText(horario);
        txtHorario.setTextSize((float) 12);

        if(usuario.equals(usuarioLogado)){
            LayouMensagemParams.gravity = Gravity.RIGHT;
            LayoutMensagem.setBackground(getDrawable(R.drawable.mensagem_enviada));
            txtUser.setText("Você");
        }else{
            LayouMensagemParams.gravity = Gravity.LEFT;
            LayoutMensagem.setBackground(getDrawable(R.drawable.mensagem_recebida));
            txtUser.setText(usuario);
        }


        LayoutMensagem.addView(txtUser);
        LayoutMensagem.addView(txtMensagem);
        LayoutMensagem.addView(txtHorario);
        LayouMensagens.addView(LayoutMensagem);
        scrollMensagens.scrollTo(0, 999999999);
    }

    private void atualizarMensagensNovas(){

        DatabaseReference reference = mDatabase.getReference().child("BD").child("chat").child(chatSelecionado);

        atualizarMensagens = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                if(snapshot.child("mensagem").exists() && snapshot.child("user").exists() && snapshot.child("horario").exists()){
                    String mensagem = snapshot.child("mensagem").getValue(String.class);
                    String user = snapshot.child("user").getValue(String.class);
                    String horario = snapshot.child("horario").getValue(String.class);

                    gerarTxtViews(mensagem, user, horario);
                    scrollMensagens.scrollTo(0, 999999999);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.child("mensagem").exists() && snapshot.child("user").exists() && snapshot.child("horario").exists()){
                    String mensagem = snapshot.child("mensagem").getValue(String.class);
                    String user = snapshot.child("user").getValue(String.class);
                    String horario = snapshot.child("horario").getValue(String.class);

                    gerarTxtViews(mensagem, user, horario);
                    scrollMensagens.scrollTo(0, 999999999);
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

        reference.addChildEventListener(atualizarMensagens);
    }
}