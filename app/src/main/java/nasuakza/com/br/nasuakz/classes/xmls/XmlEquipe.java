package nasuakza.com.br.nasuakz.classes.xmls;

import android.content.Context;
import java.io.IOException;
import nasuakza.com.br.nasuakz.classes.WebService;

/**
 * Chama o metodo do Webservice que retorna o XML das das equipes
 */
public class XmlEquipe extends Xml implements XmlInterface {

    public XmlEquipe(Context contexto) {
        super(contexto);
        setNomeArquivoXML();
    }

    //nome do arquivo para gravar o xml
    private final static String nomeArquivoXML = "equipes.xml";

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
    public static boolean criaXmlEquipesWebservice(boolean forcarAtualizacao) throws IOException {
        /**
         * TODO nao deixar o webservice ser chamado sem restricao
         */
        WebService webService = new WebService();
        webService.setForcarAtualizacao(forcarAtualizacao);
        String xml = webService.getEquipes();
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
    public void criaXmlEquipesWebservice(String xml) throws IOException {
        escreveXML(xml);
    }

}