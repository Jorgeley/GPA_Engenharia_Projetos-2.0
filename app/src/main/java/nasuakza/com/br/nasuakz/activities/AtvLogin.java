package nasuakza.com.br.nasuakz.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import nasuakza.com.br.nasuakz.R;
import nasuakza.com.br.nasuakz.beans.Equipe;
import nasuakza.com.br.nasuakz.beans.Usuario;
import nasuakza.com.br.nasuakz.classes.AgendaServico;
import nasuakza.com.br.nasuakz.classes.ServicoTarefas;
import nasuakza.com.br.nasuakz.classes.Utils;
import nasuakza.com.br.nasuakz.classes.WebService;

/**
 * Activity inicial, Tela de Login
 */
public class AtvLogin extends Activity{
    public static Usuario usuario; //objeto global
    public static Equipe equipeAdm;
    private AutoCompleteTextView TxtEmail;
    private EditText EdtSenha;
    private LoginTask AtaskLogin = null;
    private String nomeArquivo = "credenciais";
    private File arquivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atv_login);
        Utils.contexto = this;
        this.TxtEmail = (AutoCompleteTextView) findViewById(R.id.email);
        this.EdtSenha = (EditText) findViewById(R.id.password);
        this.PrgLogin = (ProgressBar) findViewById(R.id.PRGlogin);
        this.arquivo = new File(AtvLogin.this.getFilesDir() +"/"+ nomeArquivo);
    }

    @Override
    protected void onResume() {
        if (this.arquivo.exists()){
            try {
                FileInputStream arquivo = this.openFileInput(this.nomeArquivo);
                BufferedReader buffer = new BufferedReader(new InputStreamReader(arquivo));
                //Log.i(buffer.readLine(),buffer.readLine());
                this.AtaskLogin = new LoginTask(buffer.readLine(),buffer.readLine());
                this.AtaskLogin.execute((Void) null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onResume();
    }

    /**
     * setado diretamente na propriedade OnClick do BTNlogin
     * @param v
     */
    public void onClickLogin(View v) {
        String login = this.TxtEmail.getText().toString();
        String senha = this.EdtSenha.getText().toString();
        this.AtaskLogin = new LoginTask(login, senha);
        this.AtaskLogin.execute((Void) null);
    }

    /**
     * Faz o login em segundo plano
     */
    private ProgressBar PrgLogin;
    public class LoginTask extends AsyncTask<Void, Void, Boolean> {
        private final String login;

        @Override
        protected void onPreExecute() {
            Utils.barraProgresso(AtvLogin.this, PrgLogin, true);
        }

        private final String senha;

        LoginTask(String login, String senha) {
            this.login = login;
            this.senha = senha;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //TODO nao enviar senhas sem segurança
            usuario = WebService.login(login, senha);//login via webservice
            if (usuario != null) {
                AgendaServico agendaServico = new AgendaServico();
                agendaServico.onReceive(AtvLogin.this, new Intent());
                return true;
            } else
                return false;
        }

        private void gravaArquivo(){
            try {
                FileOutputStream arquivo = AtvLogin.this.openFileOutput(AtvLogin.this.nomeArquivo, 0);
                arquivo.write(this.login.getBytes());
                arquivo.write("\n".getBytes());
                arquivo.write(this.senha.getBytes());
                arquivo.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(final Boolean successo) {
            AtaskLogin = null;
            if (successo) {
                if (!AtvLogin.this.arquivo.exists()) {
                    this.gravaArquivo();
                }else{
                    FileInputStream arquivo = null;
                    try {
                        arquivo = AtvLogin.this.openFileInput(AtvLogin.this.nomeArquivo);
                        BufferedReader buffer = new BufferedReader(new InputStreamReader(arquivo));
                        if ( buffer.readLine()!=this.login || buffer.readLine()!=this.senha)
                            this.gravaArquivo();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                /* OUT OF MEMORY!!!
                WebService.tarefas(usuario.getId());*/
                equipeAdm = new Equipe(Parcel.obtain());
                equipeAdm.setId(1);
                if (usuario.getEquipes().contains(equipeAdm)) {
                    usuario.setPerfil("adm");
                    startActivity(new Intent(AtvLogin.this, AtvAdministrador.class));
                }else {
                    usuario.setPerfil("col");
                    startActivity(new Intent(AtvLogin.this, AtvColaborador.class));
                }
                final ServicoTarefas servicoTarefas = new ServicoTarefas();
                servicoTarefas.setContexto(AtvLogin.this);
                servicoTarefas.setNotificacoes(true);//nao criar notificaçoes
                new AsyncTask<Void, Void, Void>(){
                    @Override
                    protected Void doInBackground(Void... voids) {
                        servicoTarefas.run();
                        return null;
                    }
                }.execute();
                Toast.makeText(AtvLogin.this, "Bem vindo "+String.valueOf("["+usuario.getPerfil()+"]"+usuario.getNome()), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(AtvLogin.this, "Usuário ou senha inválidos", Toast.LENGTH_LONG).show();
            }
            Utils.barraProgresso(AtvLogin.this, PrgLogin, false);
        }
    }

}