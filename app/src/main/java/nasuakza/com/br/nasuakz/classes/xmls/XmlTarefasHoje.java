package nasuakza.com.br.nasuakz.classes.xmls;

import android.content.Context;
import android.util.Log;
import org.xmlpull.v1.XmlSerializer;
import java.io.FileNotFoundException;
import java.io.IOException;

import nasuakza.com.br.nasuakz.beans.Usuario;
import nasuakza.com.br.nasuakz.classes.WebService;

/**
 * Chama o metodo do Webservice que retorna o XML das tarefas de hoje
 */
public class XmlTarefasHoje extends Xml implements XmlInterface{

    public XmlTarefasHoje(Context contexto) {
        super(contexto);
        setNomeArquivoXML();
    }

    //nome do arquivo para gravar o xml
    private final static String nomeArquivoXML = "tarefasHoje.xml";

    public static String getNomeArquivoXML() {
        return nomeArquivoXML;
    }

    /** {@inheritDoc} */
    @Override
    public void setNomeArquivoXML() {
        super.nomeArquivoXML = this.nomeArquivoXML;
    }

    /** {@inheritDoc} */
    @Override
    public void setArquivoXML() {
        criaXmlProjetosHojeTeste();
    }

    /**
     * Faz download do XML via webservice e salva localmente
     * @param usuario
     * @return true: houve atualizaçao, false: nao houve atualizaçao
     * @throws IOException
     */
    public static boolean criaXmlProjetosHojeWebservice(Usuario usuario, boolean forcarAtualizacao) throws IOException {
        /**
         * TODO nao deixar o webservice ser chamado sem restricao
         */
        WebService webService = new WebService();
        webService.setUsuario(usuario);
        webService.setForcarAtualizacao(forcarAtualizacao);
        String xml = webService.projetosHoje();
        if (xml != null) {
            escreveXML(xml);
            return true;
        }else
            return false;
    }

    /**
     * Reescreve o arquivo XML passado como parametro, esse metodo e usado pelo Dialog
     * 'gravar comentario' na 'AtvTarefa'
     * @param xml
     * @throws IOException
     */
    public void criaXmlProjetosHojeWebservice(String xml) throws IOException {
        escreveXML(xml);
    }

    /**
    Cria XML exemplo e grava no dir do projeto
     */
    public void criaXmlProjetosHojeTeste() {
        try {
            this.arquivoXML = super.contexto.openFileOutput(super.nomeArquivoXML, 0);
        } catch (FileNotFoundException e) {
            Log.e("erro IO", e.getMessage());
        }
        XmlSerializer serializadorXML = android.util.Xml.newSerializer();
        try {
            serializadorXML.setOutput(this.arquivoXML, "UTF-8");
            serializadorXML.startDocument(null, Boolean.valueOf(true));
            serializadorXML.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            serializadorXML.startTag(null, "GPA");
            serializadorXML.startTag(null, "projetosPessoais-hoje");
            for (int projeto=1; projeto<5; projeto++) {
                serializadorXML.startTag(null, "projeto");
                serializadorXML.attribute(null, "nome", "Projeto Hoje Exemplo " + String.valueOf(projeto));
                for (int tarefa=1; tarefa<5; tarefa++) {
                    serializadorXML.startTag(null, "tarefa");
                    serializadorXML.text("Tarefa Hoje Exemplo " + tarefa + projeto);
                    serializadorXML.endTag(null, "tarefa");;
                }
                serializadorXML.endTag(null, "projeto");
            }
            serializadorXML.endTag(null, "projetosPessoais-hoje");
            serializadorXML.endTag(null, "GPA");
            serializadorXML.endDocument();
            serializadorXML.flush();
            this.arquivoXML.close();
        } catch (Exception e) {
            Log.e("erro serializerXML", e.getMessage());
        }
    }
}