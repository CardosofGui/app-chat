package com.example.appchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class EscolhaChat extends AppCompatActivity implements View.OnClickListener {

    private BootstrapButton btnChatPessoal, btnChatGrupoGlobal, btnChatGrupoPrivado;
    private TextView txtBemVindo;

    private FirebaseAuth mAuth;

    private String usuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escolha_chat);

        // Identificando IDs
        btnChatPessoal = findViewById(R.id.btnChatPessoal);
        btnChatGrupoGlobal = findViewById(R.id.btnChatGrupoGlobal);
        btnChatGrupoPrivado = findViewById(R.id.btnChatGrupoPrivado);
        txtBemVindo = findViewById(R.id.txtBemVindo);

        // Passando usuarioLogado
        Intent it = getIntent();
        usuarioLogado = it.getStringExtra("usuarioLogado");
        txtBemVindo.setText("Olá "+usuarioLogado+", Seja bem vindo!");

        // Ação click
        btnChatPessoal.setOnClickListener(this);
        btnChatGrupoPrivado.setOnClickListener(this);
        btnChatGrupoGlobal.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater infalter = getMenuInflater();
        infalter.inflate(R.menu.menu, menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.itemConfiguracao:
                break;
            case R.id.itemSair:
                mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnChatPessoal){
            Intent it = new Intent(this, Contatos.class);
            it.putExtra("usuarioLogado", usuarioLogado);
            startActivity(it);
        }else if(v.getId() == R.id.btnChatGrupoGlobal){
            Intent it = new Intent(this,  MenuChatGlobal.class);
            it.putExtra("usuarioLogado", usuarioLogado);
            startActivity(it);
        }else if(v.getId() == R.id.btnChatGrupoPrivado){
            Toast.makeText(this, "Em breve", Toast.LENGTH_SHORT).show();
        }
    }
}