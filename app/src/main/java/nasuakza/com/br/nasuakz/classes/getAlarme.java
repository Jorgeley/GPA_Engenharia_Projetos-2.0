package nasuakza.com.br.nasuakz.classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import nasuakza.com.br.nasuakz.activities.AtvBase;
import nasuakza.com.br.nasuakz.activities.AtvLogin;

/**
 * cria o serviço em segundo plano responsavel por sincronizar as tarefas
 */
public class getAlarme extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Utils.barraProgresso(context, AtvBase.prgTarefas, true);
        if (AtvLogin.usuario != null)
            context.startService(new Intent(context, ServicoTarefas.class));
    }
}
