package nasuakza.com.br.nasuakz.classes.provedorDados;

import android.content.Context;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import nasuakza.com.br.nasuakz.activities.AtvLogin;
import nasuakza.com.br.nasuakz.classes.xmls.XmlTarefasEquipe;

/**
 Monta TreeMap de beans <Projeto, List<Tarefa>>
 herda de ProvedorDados e implementa ProvedorDadosInterface
  */
public class ProvedorDadosTarefasEquipe extends ProvedorDados implements ProvedorDadosInterface {
    private Context contexto;

    public ProvedorDadosTarefasEquipe(Context contexto, boolean forcarAtualizacao) {
        this.contexto = contexto;
        File arquivo = new File(contexto.getFilesDir()+"/"+ XmlTarefasEquipe.getNomeArquivoXML());
        if (!arquivo.exists() || forcarAtualizacao)
            try {
                XmlTarefasEquipe xmlTarefasEquipe = new XmlTarefasEquipe(this.contexto);
                xmlTarefasEquipe.criaXmlProjetosEquipesWebservice(AtvLogin.usuario, true);
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
    public void setProjetosTreeMapBean(){
        XmlTarefasEquipe xml = new XmlTarefasEquipe(this.contexto);
        super.projetosTreeMapBean = xml.leXmlProjetosTarefas();
    }

}