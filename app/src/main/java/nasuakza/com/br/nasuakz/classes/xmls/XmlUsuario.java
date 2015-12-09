package nasuakza.com.br.nasuakz.classes.xmls;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import nasuakza.com.br.nasuakz.activities.AtvLogin;
import nasuakza.com.br.nasuakz.classes.WebService;

/**
 * Chama o metodo do Webservice que retorna o XML dos usuarios
 */
public class XmlUsuario extends Xml implements XmlInterface {
    private static String ultimaSincronizacao;

    public XmlUsuario(Context contexto) {
        super(contexto);
        setNomeArquivoXML();File arquivo = new File(contexto.getFilesDir() + "/" + this.getNomeArquivoXML());
        SimpleDateFormat formatoData = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", new Locale("pt", "BR"));
        Date data = new Date();
        data.setTime(arquivo.lastModified());//pega a data de modificaçao do arquivo XML
        this.ultimaSincronizacao = formatoData.format(data);
    }

    //nome do arquivo para gravar o xml
    private final static String nomeArquivoXML = "usuarios.xml";

    public static String getNomeArquivoXML() {
        return nomeArquivoXML;
    }

    @Override
    public void setArquivoXML() {

    }

    /** {@inheritDoc} */
    @Override
    public void setNomeArquivoXML() {
        super.nomeArquivoXML = this.nomeArquivoXML;
    }

    /**
     * Faz download do XML via webservice e salva localmente
     * @return true: houve atualizaçao, false: nao houve atualizaçao
     * @throws IOException
     */
    public boolean criaXmlUsuariosWebservice(boolean forcarAtualizacao) throws IOException {
        /**
         * TODO nao deixar o webservice ser chamado sem restricao
         */
        WebService webService = new WebService();
        webService.setForcarAtualizacao(forcarAtualizacao);
        webService.setUsuario(AtvLogin.usuario);
        String xml = webService.getUsuarios(this.ultimaSincronizacao);
        if (xml != null) {
            escreveXML(xml);
            return true;
        }else
            return false;
    }

    /**
     * Reescreve o arquivo XML passado como parametro
     * @param xml
     * @throws IOException
     */
    public void criaXmlUsuariosWebservice(String xml) throws IOException {
        escreveXML(xml);
    }

}