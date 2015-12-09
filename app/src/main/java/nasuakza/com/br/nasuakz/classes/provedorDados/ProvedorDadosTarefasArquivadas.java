package nasuakza.com.br.nasuakz.classes.provedorDados;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

import nasuakza.com.br.nasuakz.activities.AtvLogin;
import nasuakza.com.br.nasuakz.classes.xmls.XmlTarefasArquivadas;
import nasuakza.com.br.nasuakz.classes.xmls.XmlTarefasSemana;

/**
 Monta TreeMap de beans <Projeto, List<Tarefa>>
 herda de ProvedorDados e implementa ProvedorDadosInterface
  */
public class ProvedorDadosTarefasArquivadas extends ProvedorDados implements ProvedorDadosInterface{
    private Context contexto;

    public ProvedorDadosTarefasArquivadas(Context contexto, boolean forcarAtualizacao) {
        this.contexto = contexto;
        File arquivo = new File(contexto.getFilesDir()+"/"+ XmlTarefasArquivadas.getNomeArquivoXML());
        if ( !arquivo.exists() || forcarAtualizacao )
            try {
                XmlTarefasArquivadas xmlTarefasArquivadas = new XmlTarefasArquivadas(this.contexto);
                xmlTarefasArquivadas.criaXmlTarefasArquivadasWebservice(AtvLogin.usuario, true);
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
        XmlTarefasArquivadas xml = new XmlTarefasArquivadas(this.contexto);
        super.projetosTreeMapBean = xml.leXmlProjetosTarefas();
    }

}