package nasuakza.com.br.nasuakz.classes;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import org.xmlpull.v1.XmlPullParserException;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import nasuakza.com.br.nasuakz.R;
import nasuakza.com.br.nasuakz.activities.AtvBase;
import nasuakza.com.br.nasuakz.activities.AtvLogin;
import nasuakza.com.br.nasuakz.activities.AtvTarefa;
import nasuakza.com.br.nasuakz.beans.Projeto;
import nasuakza.com.br.nasuakz.beans.Tarefa;
import nasuakza.com.br.nasuakz.classes.xmls.Xml;
import nasuakza.com.br.nasuakz.classes.xmls.XmlTarefasArquivadas;
import nasuakza.com.br.nasuakz.classes.xmls.XmlTarefasEquipe;
import nasuakza.com.br.nasuakz.classes.xmls.XmlTarefasHoje;
import nasuakza.com.br.nasuakz.classes.xmls.XmlTarefasPessoais;
import nasuakza.com.br.nasuakz.classes.xmls.XmlTarefasSemana;

/**
 * serviço agendado pela classe AgendaServico para ser executado a cada minuto
 * verifica via webservice se houve atualizaçoes nas tarefas do usuario, se sim, baixa
 * XML atualizado e cria notificaçao para cada tarefa atualizada
 */
public class ServicoTarefas extends Service implements Runnable{
    private Context contexto;
    private boolean notificacoes;

    public ServicoTarefas() {
        this.notificacoes = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; //sem interatividade com o serviço
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (AtvLogin.usuario != null)
            new Thread(this).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void run(){
        //arquivo XML contendo as tarefas atualizadas
        File arquivo = new File(this.getContexto().getFilesDir() + "/" + Xml.getNomeArquivoXMLatualizadas());
        SimpleDateFormat formatoData = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", new Locale("pt", "BR"));
        Date data = new Date();
        data.setTime(arquivo.lastModified());//pega a data de modificaçao do arquivo XML
        String ultimaSincronizacao = formatoData.format(data);
        Vector<Vector<Object>> respostasSincroniza;//ids das tarefas atualizadas e flags de quais XML's atualizar
        TreeMap<Projeto, List<Tarefa>> projetosTarefas; //treeMap de beans projetos contendo beans tarefas em cada
        try {
            Xml xml = new Xml(this.getContexto());
            //chama o webservice que verifica se ha tarefas novas de acordo com a data de modificaçao do XML local
            respostasSincroniza = xml.sincronizaXmlTudoWebservice(AtvLogin.usuario, ultimaSincronizacao);
            if (respostasSincroniza != null) {
                if (this.getNotificacoes()) {
                    //monta treeMap de beans projetos contendo beans tarefas em cada
                    projetosTarefas = xml.leXmlProjetosTarefas(respostasSincroniza.get(0)); //indice 0 contem ids das tarefas atualizadas
                    if (!projetosTarefas.isEmpty()) {
                        //Log.i("projetosTarefas", String.valueOf(projetosTarefas));
                        for (Map.Entry<Projeto, List<Tarefa>> projetoTarefas : projetosTarefas.entrySet()) {
                            //para cada tarefa nova cria uma notificaçao contendo a mesma
                            for (Tarefa tarefa : projetoTarefas.getValue()) {
                                if (!tarefa.getStatus().equals("arquivada")) {
                                    Bundle bundleTarefa = new Bundle();
                                    bundleTarefa.putParcelable("projeto", projetoTarefas.getKey());
                                    bundleTarefa.putParcelable("tarefa", tarefa);
                                    Intent atvTarefa = new Intent(this.getContexto(), AtvTarefa.class);
                                    atvTarefa.putExtras(bundleTarefa);
                                    String msg = null;
                                    switch (tarefa.getStatus()) {
                                        case "aberta":
                                            msg = " [atualizada]";
                                            break;
                                        case "concluir":
                                            msg = " [confirmar conclusao?]";
                                            break;
                                        case "rejeitada":
                                            msg = " [rejeitada conclusao!]";
                                            break;
                                        case "concluida":
                                            msg = " [concluida!]";
                                            break;
                                        case "excluir":
                                            msg = " [excluida!]";
                                            //atvTarefa = null;
                                            break;
                                    }
                                    Notificacao.create(this.getContexto(),
                                            "GPA",
                                            tarefa.getNome() + msg,
                                            R.drawable.logo_notificacao,
                                            tarefa.getId(), //se for igual os extras nao atualizam
                                            atvTarefa
                                    );
                                    Date hoje = new Date();
                                    Long diferenca = tarefa.getVencimento().getTime() - hoje.getTime();
                                    Log.i("diferença", String.valueOf(diferenca));
                                    if (diferenca <= 432000000){ //5*24*60*60*1000 = 432000000 milisegundos (5 dias)
                                        SimpleDateFormat formatoDataDiferenca = new SimpleDateFormat("dd", new Locale("pt", "BR"));
                                        Notificacao.create(this.getContexto(),
                                                "GPA",
                                                "-"+formatoDataDiferenca.format(diferenca)+" dias p/ vencimento: "+tarefa.getNome(),
                                                R.drawable.logo_notificacao,
                                                tarefa.getId(), //se for igual os extras nao atualizam
                                                atvTarefa
                                        );
                                    }
                                    //sinaliza para a atvBase atualizar o listView
                                    AtvBase.atualizaListView = true;
                                }
                            }
                        }
                    }
                }
                //faz a atualizaçao dos XML's baseando-se nas flags enviadas pelo webservice
                if (respostasSincroniza.get(1) != null) {
                    //indice [1][0] contem flag para sincronizar XML tarefas pessoais
                    Boolean sincronizaPessoais = (Boolean) respostasSincroniza.get(1).get(0);
                    //indice [1][1] contem flag para sincronizar XML tarefas equipes
                    Boolean sincronizaEquipes = (Boolean) respostasSincroniza.get(1).get(1);
                    //indice [1][2] contem flag para sincronizar XML tarefas hoje
                    Boolean sincronizaHoje = (Boolean) respostasSincroniza.get(1).get(2);
                    //indice [1][3] contem flag para sincronizar XML tarefas semana
                    Boolean sincronizaSemana = (Boolean) respostasSincroniza.get(1).get(3);
                    //indice [1][4] contem flag para sincronizar XML tarefas arquivadas
                    Boolean sincronizaArquivadas = (Boolean) respostasSincroniza.get(1).get(4);
                    if (sincronizaPessoais) {
                        XmlTarefasPessoais xmlTarefasPessoais = new XmlTarefasPessoais(this.getContexto());
                        xmlTarefasPessoais.criaXmlProjetosPessoaisWebservice(AtvLogin.usuario, true);
                    }
                    if (sincronizaEquipes) {
                        XmlTarefasEquipe xmlTarefasEquipe = new XmlTarefasEquipe(this.getContexto());
                        xmlTarefasEquipe.criaXmlProjetosEquipesWebservice(AtvLogin.usuario, true);
                    }
                    if (sincronizaHoje) {
                        XmlTarefasHoje xmlTarefasHoje = new XmlTarefasHoje(this.getContexto());
                        xmlTarefasHoje.criaXmlProjetosHojeWebservice(AtvLogin.usuario, true);
                    }
                    if (sincronizaSemana) {
                        XmlTarefasSemana xmlTarefasSemana = new XmlTarefasSemana(this.getContexto());
                        xmlTarefasSemana.criaXmlProjetosSemanaWebservice(AtvLogin.usuario, true);
                    }
                    if (sincronizaArquivadas) {
                        XmlTarefasArquivadas xmlTarefasArquivadas = new XmlTarefasArquivadas(this.getContexto());
                        xmlTarefasArquivadas.criaXmlTarefasArquivadasWebservice(AtvLogin.usuario, true);
                    }
                }
            }
        } catch (ConnectException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        //porque Deus quis assim...
        Handler refresh = new Handler(Looper.getMainLooper());
        refresh.post(new Runnable() {
            public void run(){
                if (AtvBase.prgTarefas != null)
                    AtvBase.prgTarefas.setVisibility(View.GONE);
            }
        });
    }

    public void setContexto(Context contexto){
        this.contexto = contexto;
    }

    public Context getContexto(){
        if (this.contexto == null)
            return getApplicationContext();
        else
            return this.contexto;
    }

    public void setNotificacoes(boolean notificacoes) {
        this.notificacoes = notificacoes;
    }

    public boolean getNotificacoes() {
        return this.notificacoes;
    }
}