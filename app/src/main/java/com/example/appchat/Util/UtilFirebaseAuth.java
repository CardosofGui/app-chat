package com.example.appchat.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.appchat.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class UtilFirebaseAuth {

    public static void tratandoErros(Context context, String erro){

        // Verificando erros
        if(erro.contains("least 6 characters")){
            Toast.makeText(context, "Digite uma senha maior que 5 Digitos", Toast.LENGTH_SHORT).show();
        }else if(erro.contains("address is badly")){
            Toast.makeText(context, "Digite um e-mail valido", Toast.LENGTH_SHORT).show();
        }else if(erro.contains("password is invalid")){
            Toast.makeText(context, "Senha invalida", Toast.LENGTH_SHORT).show();
        }else if(erro.contains("interrupted connection")){
            Toast.makeText(context, "Sem conexão com o servidor Firebase", Toast.LENGTH_SHORT).show();
        }else if(erro.contains("There is no user")){
            Toast.makeText(context, "Usuário não cadastrado", Toast.LENGTH_SHORT).show();
        }else if(erro.contains("address is already")){
            Toast.makeText(context, "Email já cadastrado", Toast.LENGTH_SHORT).show();
        }else if(erro.contains("INVALID_EMAIL")){
            Toast.makeText(context, "E-mail invalido", Toast.LENGTH_SHORT).show();
        }else if(erro.contains("EMAIL_NOT_FOUND")){
            Toast.makeText(context, "Email não cadastrado", Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(context, erro, Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean verificandoRede(Context context){
        // Verificando internet
        ConnectivityManager conexao = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conexao.getActiveNetworkInfo();

        // Fazendo verificação
        if(netInfo != null && netInfo.isConnected()){
            return true;
        }else{
            return false;
        }
    }

    public static void redefinirSenha(FirebaseAuth mAuth, final Context context, final String email){
        if(verificandoRede(context)){
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()){
                        Toast.makeText(context, "E-mail enviado com sucesso para "+email, Toast.LENGTH_SHORT).show();
                    }else{
                        UtilFirebaseAuth.tratandoErros(context, task.getException().toString());
                    }
                }
            });
        }else{
            Toast.makeText(context, "Erro - Verifique sua conexão WIFI ou DADOS MÓVEIS", Toast.LENGTH_SHORT).show();
        }
    }


    // Realizar login com email e senha



}
