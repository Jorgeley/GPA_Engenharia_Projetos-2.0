package nasuakza.com.br.nasuakz.classes.provedorDados;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeMap;
import nasuakza.com.br.nasuakz.activities.AtvLogin;
import nasuakza.com.br.nasuakz.classes.xmls.XmlTarefasHoje;

/**
 Monta TreeMap de beans <Projeto, List<Tarefa>>
 herda de ProvedorDados e implementa ProvedorDadosInterface
  */
public class ProvedorDadosTarefasHoje extends ProvedorDados implements ProvedorDadosInterface{
    private Context contexto;

    public ProvedorDadosTarefasHoje(Context contexto, boolean forcarAtualizacao) {
        this.contexto = contexto;
        File arquivo = new File(contexto.getFilesDir()+"/"+ XmlTarefasHoje.getNomeArquivoXML());
        SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR"));
        Date dataArquivo = new Date();
        dataArquivo.setTime(arquivo.lastModified());//pega a data de modificaçao do arquivo XML
        Date hoje = new Date();
        Log.i("datas", formatoData.format(dataArquivo) +" e "+ formatoData.format(hoje));
        if (!formatoData.format(dataArquivo).equals(formatoData.format(hoje)) || !arquivo.exists() || forcarAtualizacao)
            Log.i("atualizando", "tarefas hoje");
            try {
                XmlTarefasHoje xmlTarefasHoje = new XmlTarefasHoje(this.contexto);
                xmlTarefasHoje.criaXmlProjetosHojeWebservice(AtvLogin.usuario, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        setProjetosTreeMapBean();
    }

    /** {@inheritDoc} **/
    @Override
    public TreeMap<String, List<String>> getTarefas(boolean inverteAgrupamento) {
        return super.getTarefas(inverteAgrupamento);
    }

    /** {@inheritDoc} **/
    @Override
    public void setProjetosTreeMapBean() {
        XmlTarefasHoje xml = new XmlTarefasHoje(this.contexto);
        super.projetosTreeMapBean = xml.leXmlProjetosTarefas();
    }
}