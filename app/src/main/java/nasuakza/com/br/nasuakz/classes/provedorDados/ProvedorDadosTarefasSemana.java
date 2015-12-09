package nasuakza.com.br.nasuakz.classes.provedorDados;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeMap;
import nasuakza.com.br.nasuakz.activities.AtvLogin;
import nasuakza.com.br.nasuakz.classes.xmls.XmlTarefasSemana;

/**
 Monta TreeMap de beans <Projeto, List<Tarefa>>
 herda de ProvedorDados e implementa ProvedorDadosInterface
  */
public class ProvedorDadosTarefasSemana extends ProvedorDados implements ProvedorDadosInterface{
    private Context contexto;

    public ProvedorDadosTarefasSemana(Context contexto, boolean forcarAtualizacao) {
        this.contexto = contexto;
        File arquivo = new File(contexto.getFilesDir()+"/"+ XmlTarefasSemana.getNomeArquivoXML());
        SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR"));
        Calendar dataArquivo = Calendar.getInstance();
        dataArquivo.setTimeInMillis(arquivo.lastModified());//pega a data de modificaçao do arquivo XML
        Calendar hoje = Calendar.getInstance();
        hoje.get(Calendar.WEEK_OF_YEAR);
        Log.i("semanas", hoje.get(Calendar.WEEK_OF_YEAR) + " e " + dataArquivo.get(Calendar.WEEK_OF_YEAR));
        if ( dataArquivo.get(Calendar.WEEK_OF_YEAR) <  hoje.get(Calendar.WEEK_OF_YEAR) || !arquivo.exists() || forcarAtualizacao )
            Log.i("atualizando", "tarefas semana");
            try {
                XmlTarefasSemana xmlTarefasSemana = new XmlTarefasSemana(this.contexto);
                xmlTarefasSemana.criaXmlProjetosSemanaWebservice(AtvLogin.usuario, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        setProjetosTreeMapBean();
    }

    /** {@inheritDoc} */
    @Override
    public TreeMap<String, List<String>> getTarefas(boolean inverteAgrupamento) {
        return super.getTarefas(inverteAgrupamento);
    }

    /** {@inheritDoc} */
    @Override
    public void setProjetosTreeMapBean() {
        XmlTarefasSemana xml = new XmlTarefasSemana(this.contexto);
        super.projetosTreeMapBean = xml.leXmlProjetosTarefas();
    }

}