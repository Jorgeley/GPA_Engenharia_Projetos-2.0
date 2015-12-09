package nasuakza.com.br.nasuakz.classes;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.util.Calendar;

/**
 * Executa no boot do Android e seta o sincronismo
 * das tarefas via webservice de 10 em 10 minutos
 */
public class AgendaServico extends BroadcastReceiver {
    private final static int intervaloSincronismo = 60000; //milisegundos: 1minuto

    @Override
    public void onReceive(Context context, Intent intent) {
        //define o momento para disparar o sincronismo
        Calendar calendario = Calendar.getInstance();
        calendario.setTimeInMillis(System.currentTimeMillis()); //pega o tempo atual
        calendario.add(Calendar.MINUTE, 1); //1 minuto depois do tempo atual
        //alarme que disparara o sincronismo
        AlarmManager alarme = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long tempo = calendario.getTimeInMillis();
        //intent para receber o alarme
        Intent intentAlarme = new Intent("EXECUTA_ALARME"); //setado no AndroidManifest
        PendingIntent intentPendente = PendingIntent.getBroadcast(context, 0, intentAlarme, 0);
        //agenda o sincronismo para daqui 1 minuto a partir de agora, repetindo de 10 em 10 minutos
        alarme.setRepeating(AlarmManager.RTC_WAKEUP,
                            tempo, //executar alarme apos 10 minutos a partir de agora
                            intervaloSincronismo , //repetir de 10 em 10 minutos
                            intentPendente //intent que recebera o alarme
        );
    }

}