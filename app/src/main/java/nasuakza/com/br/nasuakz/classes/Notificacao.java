package nasuakza.com.br.nasuakz.classes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import nasuakza.com.br.nasuakz.R;

/**
 * Cria notificaçao para atualizacoes de tarefas
 */
public class Notificacao {

    /**
     * Cria notificaçao para atualizaçoes das tarefas conforme a API correta do Android
     * @param contexto
     * @param titulo
     * @param mensagem
     * @param icone
     * @param id
     * @param intent
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static void create(Context contexto, CharSequence titulo, CharSequence mensagem, int icone, int id, Intent intent){
        //intent para abrir qdo clicar na notificaçao
        PendingIntent pendingIntent = PendingIntent.getActivity(contexto, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notificacao = null;
        int api = Build.VERSION.SDK_INT; //level da API
        if (api >= 11) {
            Builder builder = new Builder(contexto)
                    .setContentTitle(titulo)
                    .setContentText(mensagem)
                    .setSmallIcon(icone)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);
            if (api >= 17) //android 4.2
                notificacao = builder.build();
            else //android 3.x
                notificacao = builder.getNotification();
        }else{ //android 2.2
            /*
            @deprecated
            notificacao = new Notification(icone, titulo, System.currentTimeMillis());
            notificacao.setLatestEventInfo(contexto, titulo, mensagem, pendingIntent);*/
            notificacao = new Notification.Builder(contexto)
                    .setContentTitle(titulo)
                    .setContentText(mensagem)
                    .setSmallIcon(icone)
                    .build(); // disponivel API level >= 11
        }
        NotificationManager notificacaoManager = (NotificationManager) contexto.getSystemService(Activity.NOTIFICATION_SERVICE);
        notificacaoManager.notify(id, notificacao);//id: identifica a notificaçao
    }

    /**
     * Cancela a notificaçao pelo id da mesma
     * @param contexto
     * @param id
     */
    public static void cancell(Context contexto, int id){
        NotificationManager notificacaoManager = (NotificationManager) contexto.getSystemService(Activity.NOTIFICATION_SERVICE);
        notificacaoManager.cancel(id);
    }

}