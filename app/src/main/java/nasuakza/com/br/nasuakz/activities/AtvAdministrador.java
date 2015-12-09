package nasuakza.com.br.nasuakz.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import nasuakza.com.br.nasuakz.R;

/**
 * Activity do administrador de projetosPessoais
 */
public class AtvAdministrador extends AtvBase{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atv_administrador);
        this.setViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = super.criaMenu(menu);
        inflater.inflate(R.menu.atv_administrador, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.gerenciar_equipes:
            case R.id.gerenciar_usuarios:
                startActivity(new Intent(AtvAdministrador.this, AtvUsuarios.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}