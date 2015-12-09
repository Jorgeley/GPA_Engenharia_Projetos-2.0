package nasuakza.com.br.nasuakz.classes;

import android.os.Parcel;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import nasuakza.com.br.nasuakz.activities.AtvLogin;
import nasuakza.com.br.nasuakz.beans.Equipe;
import nasuakza.com.br.nasuakz.beans.Projeto;
import nasuakza.com.br.nasuakz.beans.Tarefa;
import nasuakza.com.br.nasuakz.beans.Usuario;

/**
 * Classe responsavel pela comunicaçao entre o app e o servidor
 */
public class WebService{

    //private static String SERVIDOR = "192.168.0.118:8888";
    //private static String SERVIDOR = "192.168.1.103:8888";
    private static String SERVIDOR = "www.grupo-gpa.com";
    //Namespace of the Webservice - can be found in WSDL
    //private static String NAMESPACE = "http://"+SERVIDOR+"/WEB/GPA/public/webservice/soap/";
    private static String NAMESPACE = "http://"+SERVIDOR+"/webservice/soap/";
    //Webservice URL - WSDL File location
    //private static String URL = "http://"+SERVIDOR+"/WEB/GPA/public/webservice/soap";//Make sure you changed IP address
    private static String URL = "http://"+SERVIDOR+"/webservice/soap";
    //SOAP Action URI again Namespace + Web method name
    //private static String SOAP_ACTION = "http://"+SERVIDOR+"/WEB/GPA/public/webservice/soap#";
    private static String SOAP_ACTION = "http://"+SERVIDOR+"/webservice/soap#";
    //usuario para todas as operaçoes no webservice
    private Usuario usuario;
    //flag enviada ao servidor do webservice indicando p/ retornar os projetos, mesmo estando atualizados
    private boolean forcarAtualizacao = false;

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getUsuario() {
        return this.usuario;
    }

    public boolean isForcarAtualizacao() {
        return this.forcarAtualizacao;
    }

    public void setForcarAtualizacao(boolean forcarAtualizacao) {
        this.forcarAtualizacao = forcarAtualizacao;
    }

    /**
     * faz o login via webservice no servidor e retorna o objeto Usuario
     * @param login
     * @param senha
     * @return Usuario
     */
    public static Usuario login(String login, String senha) {
        //requisição SOAP
        SoapObject requisicao = new SoapObject(NAMESPACE, "autentica");
        //setando parametros do método do webservice 'autentica'
        PropertyInfo loginWebservice = new PropertyInfo();
        PropertyInfo senhaWebservice = new PropertyInfo();
        loginWebservice.setName("login");
        loginWebservice.setValue(login);
        loginWebservice.setType(String.class);
        requisicao.addProperty(loginWebservice);
        senhaWebservice.setName("senha");
        senhaWebservice.setValue(senha);
        senhaWebservice.setType(String.class);
        requisicao.addProperty(senhaWebservice);
        //evelopando a requisição
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(requisicao);
        //requisição HTTP
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        //bean Usuario
        Usuario usuario = new Usuario(Parcel.obtain());
        try {//faz a chamada do método 'autentica' do webservice
            androidHttpTransport.call(SOAP_ACTION + "autentica", envelope);
            //pegando a resposta
            Vector<Vector<Vector<Object>>> resposta = (Vector<Vector<Vector<Object>>>) envelope.getResponse();
            usuario.setId((Integer) resposta.get(0).get(0).get(0));
            usuario.setNome((String) resposta.get(0).get(0).get(1));
            usuario.setEquipes(new HashSet<Equipe>());
            Vector<Vector<Object>> equipesObjeto = resposta.get(1);
            for (Vector<Object> equipeObjeto : equipesObjeto) {
                Equipe equipe = new Equipe(Parcel.obtain());
                equipe.setId((Integer) equipeObjeto.get(0));
                equipe.setNome((String) equipeObjeto.get(1));
                usuario.getEquipes().add(equipe);
            }
        } catch (Exception e) {
            //se não conseguir autenticar retorna null
            usuario = null;
            e.printStackTrace();
        }
        return usuario;
    }

    /**
     * sincroniza as tarefas do usuario
     * @param ultimaSincronizacao
     * @return array ids tarefas atualizadas, XML tarefas atualizadas e flags de quais XML's atualizar
     */
    public Object[] sincroniza(String ultimaSincronizacao) {
        //requisição SOAP
        SoapObject requisicao = new SoapObject(NAMESPACE, "sincroniza");
        //setando parametros do método do webservice 'sincroniza'
        requisicao.addProperty(this.getPropertyInfoUsuario());
        PropertyInfo ultimaSincronizacaoWebservice = new PropertyInfo();
        ultimaSincronizacaoWebservice.setName("ultimaSincronizacao");
        ultimaSincronizacaoWebservice.setValue(ultimaSincronizacao);
        ultimaSincronizacaoWebservice.setType(String.class);
        requisicao.addProperty(ultimaSincronizacaoWebservice);
        //Log.i("ultimaSincronizacao", ultimaSincronizacao);
        //evelopando a requisição
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(requisicao);
        //requisição HTTP
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        Vector<Integer> idsTarefas = null;
        Vector<Boolean> flagsSincroniza = null;
        String xml = null;
        try {//faz a chamada do método 'sincroniza' do webservice
            androidHttpTransport.call(SOAP_ACTION + "sincroniza", envelope);
            //pegando a resposta
            Vector<Object> resposta = (Vector<Object>) envelope.getResponse();
            if (resposta == null)
                return null;
            else {
                idsTarefas = (Vector<Integer>) resposta.get(0); //id's das tarefas atualizadas
                flagsSincroniza = (Vector<Boolean>) resposta.get(2); //flags de quais XML's atualizar
                xml = resposta.get(1).toString(); //XML contendo todas as tarefas atualizads
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Object[] respostas = new Object[]{idsTarefas, xml, flagsSincroniza};
        return respostas;
    }

    /**
     * Conclui (administrador) ou solicita conclusao (colaborador) da tarefa
     * @param tarefa
     * @param confirma
     * @return 'concluida' ou 'concluir'
     */
    public String concluiTarefa(Tarefa tarefa, String confirma) {
        //requisição SOAP
        SoapObject requisicao = new SoapObject(NAMESPACE, "concluiTarefa");
        //setando parametros do método do webservice 'projetosPessoais'
        requisicao.addProperty(this.getPropertyInfoUsuario());
        //setando parametro 'idTarefa' do método do webservice 'concluiTarefa'
        PropertyInfo idTarefaWebservice = new PropertyInfo();
        idTarefaWebservice.setName("idTarefa");
        idTarefaWebservice.setValue(tarefa.getId());
        idTarefaWebservice.setType(Integer.class);
        requisicao.addProperty(idTarefaWebservice);
        //setando parametro 'confirma' do método do webservice 'concluiTarefa'
        PropertyInfo confirmaWebservice = new PropertyInfo();
        confirmaWebservice.setName("confirma");
        confirmaWebservice.setValue(confirma);
        confirmaWebservice.setType(String.class);
        requisicao.addProperty(confirmaWebservice);
        //evelopando a requisição
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(requisicao);
        //requisição HTTP
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        String resposta = null;
        try {//faz a chamada do método 'projetosPessoais' do webservice
            androidHttpTransport.call(SOAP_ACTION + "concluiTarefa", envelope);
            //pegando a resposta
            resposta = (String) envelope.getResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resposta;
    }

    /**
     * pega o XML de projetosPessoais com tarefas do usuario
     * @return XML de projetosPessoais com as tarefas
     */
    public String projetosPessoais() {
        //requisição SOAP
        SoapObject requisicao = new SoapObject(NAMESPACE, "projetosPessoais");
        //setando parametros do método do webservice 'projetosPessoais'
        requisicao.addProperty(this.getPropertyInfoUsuario());
        requisicao.addProperty(this.getForcarAtualizacao());
        //evelopando a requisição
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(requisicao);
        //requisição HTTP
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        String xml = null;
        try {//faz a chamada do método 'projetosPessoais' do webservice
            androidHttpTransport.call(SOAP_ACTION + "projetosPessoais", envelope);
            //pegando a resposta
            xml = (String) envelope.getResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xml;
    }

    /**
     * pega o XML de projetosEquipes com tarefas do usuario
     * @return XML de projetosEquipes com as tarefas
     */
    public String projetosEquipes() {
        //requisição SOAP
        SoapObject requisicao = new SoapObject(NAMESPACE, "projetosEquipes");
        //setando parametros do método do webservice 'projetosEquipes'
        requisicao.addProperty(this.getPropertyInfoUsuario());
        requisicao.addProperty(this.getForcarAtualizacao());
        //evelopando a requisição
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(requisicao);
        //requisição HTTP
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        String xml = null;
        try {//faz a chamada do método 'projetosEquipes' do webservice
            androidHttpTransport.call(SOAP_ACTION + "projetosEquipes", envelope);
            //pegando a resposta
            xml = (String) envelope.getResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xml;
    }

    /**
     * pega o XML de projetos da data de hoje com tarefas do usuario
     * @return XML de projetos com as tarefas de hoje
     */
    public String projetosHoje() {
        //requisição SOAP
        SoapObject requisicao = new SoapObject(NAMESPACE, "projetosHoje");
        //setando parametros do método do webservice 'projetosHoje'
        requisicao.addProperty(this.getPropertyInfoUsuario());
        requisicao.addProperty(this.getForcarAtualizacao());
        //evelopando a requisição
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(requisicao);
        //requisição HTTP
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        String xml = null;
        try {//faz a chamada do método 'projetosHoje' do webservice
            androidHttpTransport.call(SOAP_ACTION + "projetosHoje", envelope);
            //pegando a resposta
            xml = (String) envelope.getResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xml;
    }

    /**
     * pega o XML de projetos com tarefas da semana atual
     * @return XML de projetos com as tarefas da semana
     */
    public String projetosSemana() {
        //requisição SOAP
        SoapObject requisicao = new SoapObject(NAMESPACE, "projetosSemana");
        //setando parametros do método do webservice 'projetosSemana'
        requisicao.addProperty(this.getPropertyInfoUsuario());
        requisicao.addProperty(this.getForcarAtualizacao());
        //evelopando a requisição
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(requisicao);
        //requisição HTTP
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        String xml = null;
        try {//faz a chamada do método 'projetosSemana' do webservice
            androidHttpTransport.call(SOAP_ACTION + "projetosSemana", envelope);
            //pegando a resposta
            xml = (String) envelope.getResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xml;
    }

    /**
     * busca via webservice as tarefas arquivadas (concluidas)
     * @return
     */
    public String tarefasArquivadas(){
        //requisição SOAP
        SoapObject requisicao = new SoapObject(NAMESPACE, "tarefasArquivadas");
        //setando parametros do método do webservice 'tarefasArquivadas'
        requisicao.addProperty(this.getPropertyInfoUsuario());
        requisicao.addProperty(this.getForcarAtualizacao());
        //evelopando a requisição
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(requisicao);
        //requisição HTTP
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        String xml = null;
        try {//faz a chamada do método 'tarefasArquivadas' do webservice
            androidHttpTransport.call(SOAP_ACTION + "tarefasArquivadas", envelope);
            //pegando a resposta
            xml = (String) envelope.getResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xml;
    }

    /**
     * grava comentario de uma tarefa do usuario
     * @param idTarefa
     * @param textoComentario
     * @return String comentario gravado e flags de quais XML's atualizar
     */
    public Object[] gravacomentario(int idTarefa, String textoComentario) {
        //requisição SOAP
        SoapObject requisicao = new SoapObject(NAMESPACE, "gravaComentario");
        //setando parametro 'usuario' do método do webservice 'gravacomentario'
        requisicao.addProperty(this.getPropertyInfoUsuario());
        //setando parametro 'idTarefa' do método do webservice 'gravacomentario'
        PropertyInfo idTarefaWebservice = new PropertyInfo();
        idTarefaWebservice.setName("idTarefa");
        idTarefaWebservice.setValue(idTarefa);
        idTarefaWebservice.setType(Integer.class);
        requisicao.addProperty(idTarefaWebservice);
        //setando parametro 'textoComentario' do método do webservice 'gravacomentario'
        PropertyInfo textoComentarioWebservice = new PropertyInfo();
        textoComentarioWebservice.setName("textoComentario");
        textoComentarioWebservice.setValue(textoComentario);
        textoComentarioWebservice.setType(String.class);
        requisicao.addProperty(textoComentarioWebservice);
        //evelopando a requisição
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(requisicao);
        //requisição HTTP
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        String comentario = null;
        Vector<Boolean> flagsSincroniza = null;
        try {//faz a chamada do método 'gravacomentario' do webservice
            androidHttpTransport.call(SOAP_ACTION + "gravaComentario", envelope);
            //pegando a resposta
            Vector<Object> resposta = (Vector<Object>) envelope.getResponse();
            comentario = (String) resposta.get(0);
            flagsSincroniza = (Vector<Boolean>) resposta.get(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Object[] respostas = new Object[]{comentario, flagsSincroniza};
        return respostas;
    }

    /**
     * Grava projeto via webservice
     * @param projeto
     * @return boolean resposta
     */
    public static boolean gravaProjeto(Projeto projeto){
        //requisição SOAP
        SoapObject requisicao = new SoapObject(NAMESPACE, "gravaProjeto");
        PropertyInfo nomeProjeto = new PropertyInfo();
        nomeProjeto.setName("nomeProjeto");
        nomeProjeto.setValue(projeto.getNome());
        nomeProjeto.setType(String.class);
        requisicao.addProperty(nomeProjeto);
        PropertyInfo descricaoProjeto = new PropertyInfo();
        descricaoProjeto.setName("descricaoProjeto");
        descricaoProjeto.setValue(projeto.getDescricao());
        descricaoProjeto.setType(String.class);
        requisicao.addProperty(descricaoProjeto);
        PropertyInfo vencimentoProjeto = new PropertyInfo();
        vencimentoProjeto.setName("vencimentoProjeto");
        vencimentoProjeto.setValue(projeto.getVencimento().toString());
        vencimentoProjeto.setType(String.class);
        requisicao.addProperty(vencimentoProjeto);
        PropertyInfo equipeProjeto = new PropertyInfo();
        equipeProjeto.setName("equipeProjeto");
        if (projeto.getEquipe() != null)
            equipeProjeto.setValue(projeto.getEquipe().getId());
        equipeProjeto.setType(Integer.class);
        requisicao.addProperty(equipeProjeto);
        PropertyInfo usuarioProjeto = new PropertyInfo();
        usuarioProjeto.setName("usuario");
        if (projeto.getUsuario() != null)
            usuarioProjeto.setValue(projeto.getUsuario().getId());
        usuarioProjeto.setType(Integer.class);
        requisicao.addProperty(usuarioProjeto);
        //evelopando a requisição
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(requisicao);
        //requisição HTTP
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        boolean resposta = false;
        try {//faz a chamada do método 'gravaprojeto' do webservice
            androidHttpTransport.call(SOAP_ACTION + "gravaProjeto", envelope);
            //pegando a resposta
            resposta = (boolean) envelope.getResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resposta;
    }

    /**
     * Grava tarefa via webservice
     * @param tarefa
     * @return boolean resposta
     */
    public static boolean gravaTarefa(Tarefa tarefa){
        //requisição SOAP
        SoapObject requisicao = new SoapObject(NAMESPACE, "gravaTarefa");
        PropertyInfo id = new PropertyInfo();
        id.setName("id");
        id.setValue(tarefa.getId());
        id.setType(Integer.class);
        requisicao.addProperty(id);
        PropertyInfo nome = new PropertyInfo();
        nome.setName("nome");
        nome.setValue(tarefa.getNome());
        nome.setType(String.class);
        requisicao.addProperty(nome);
        PropertyInfo descricao = new PropertyInfo();
        descricao.setName("descricao");
        descricao.setValue(tarefa.getDescricao());
        descricao.setType(String.class);
        requisicao.addProperty(descricao);
        PropertyInfo vencimento = new PropertyInfo();
        vencimento.setName("vencimento");
        vencimento.setValue(tarefa.getVencimento().toString());
        vencimento.setType(String.class);
        requisicao.addProperty(vencimento);
        PropertyInfo projeto = new PropertyInfo();
        projeto.setName("projeto");
        projeto.setValue(tarefa.getProjeto().getId());
        projeto.setType(Integer.class);
        requisicao.addProperty(projeto);
        PropertyInfo responsavel = new PropertyInfo();
        responsavel.setName("responsavel");
        responsavel.setValue(tarefa.getUsuario().getId());
        responsavel.setType(Integer.class);
        requisicao.addProperty(responsavel);
        //evelopando a requisição
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(requisicao);
        //requisição HTTP
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        boolean resposta = false;
        try {//faz a chamada do método 'gravatarefa' do webservice
            androidHttpTransport.call(SOAP_ACTION + "gravaTarefa", envelope);
            //pegando a resposta
            resposta = (boolean) envelope.getResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resposta;
    }

    public Vector<Boolean> excluiTarefa(Tarefa tarefa){
        //requisição SOAP
        SoapObject requisicao = new SoapObject(NAMESPACE, "excluiTarefa");
        PropertyInfo idTarefa = new PropertyInfo();
        idTarefa.setName("idTarefa");
        idTarefa.setValue(tarefa.getId());
        idTarefa.setType(Integer.class);
        requisicao.addProperty(idTarefa);
        requisicao.addProperty(getPropertyInfoUsuario());
        //evelopando a requisição
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(requisicao);
        //requisição HTTP
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        Vector<Boolean> resposta = null;
        try {//faz a chamada do método 'gravatarefa' do webservice
            androidHttpTransport.call(SOAP_ACTION + "excluiTarefa", envelope);
            //pegando a resposta
            resposta = (Vector<Boolean>) envelope.getResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resposta;
    }

    /**
     * Retorna lista de equipes, usado na atvProjeto
     * @return XML das equipes
     */
    public static String getEquipes() {
        List<Equipe> equipes = null;
        //requisição SOAP
        SoapObject requisicao = new SoapObject(NAMESPACE, "getEquipes");
        //evelopando a requisição
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(requisicao);
        //requisição HTTP
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        String resposta = null;
        try {//faz a chamada do método 'getEquipes' do webservice
            androidHttpTransport.call(SOAP_ACTION + "getEquipes", envelope);
            //pegando a resposta
            resposta = (String) envelope.getResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resposta;
    }

    /**
     * Retorna lista de projetos, usado na atvTarefa
     * @return XML dos projetos
     * @param ultimaSincronizacao
     */
    public String getProjetos(String ultimaSincronizacao) {
        //requisição SOAP
        SoapObject requisicao = new SoapObject(NAMESPACE, "getProjetos");
        requisicao.addProperty(this.getPropertyInfoUsuario());
        PropertyInfo ultimaSincronizacaoWebservice = new PropertyInfo();
        ultimaSincronizacaoWebservice.setName("ultimaSincronizacao");
        ultimaSincronizacaoWebservice.setValue(ultimaSincronizacao);
        ultimaSincronizacaoWebservice.setType(String.class);
        requisicao.addProperty(ultimaSincronizacaoWebservice);
        //evelopando a requisição
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(requisicao);
        //requisição HTTP
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        String resposta = null;
        try {//faz a chamada do método 'getProjetos' do webservice
            androidHttpTransport.call(SOAP_ACTION + "getProjetos", envelope);
            //pegando a resposta
            resposta = (String) envelope.getResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resposta;
    }

    /**
     * Retorna lista de usuarios, usado na atvTarefa
     * @return XML dos usuarios
     * @param ultimaSincronizacao
     */
    public static String getUsuarios(String ultimaSincronizacao) {
        //requisição SOAP
        SoapObject requisicao = new SoapObject(NAMESPACE, "getUsuarios");
        PropertyInfo ultimaSincronizacaoWebservice = new PropertyInfo();
        ultimaSincronizacaoWebservice.setName("ultimaSincronizacao");
        ultimaSincronizacaoWebservice.setValue(ultimaSincronizacao);
        ultimaSincronizacaoWebservice.setType(String.class);
        requisicao.addProperty(ultimaSincronizacaoWebservice);
        //evelopando a requisição
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(requisicao);
        //requisição HTTP
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        String resposta = null;
        try {//faz a chamada do método 'getUsuarios' do webservice
            androidHttpTransport.call(SOAP_ACTION + "getUsuarios", envelope);
            //pegando a resposta
            resposta = (String) envelope.getResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resposta;
    }

        /**
     * retorna o parametro 'usuario' para setar na requisiçao do webservice
     * @return PropertyInfo usuario
     */
    private PropertyInfo getPropertyInfoUsuario(){
        PropertyInfo idUsuarioWebservice = new PropertyInfo();
        idUsuarioWebservice.setName("usuario");
        idUsuarioWebservice.setValue(this.getUsuario().getId());
        idUsuarioWebservice.setType(Integer.class);
        return idUsuarioWebservice;
    }

    /**
     * retorna o parametro 'forcarAtualizacao' para setar na requisicao do webservice
     * @return PropertyInfo forcarAtualizacao
     */
    private PropertyInfo getForcarAtualizacao(){
        PropertyInfo forcarAtualizacao = new PropertyInfo();
        /**
         * no servidor do webservice o atributo 'novasTarefas' da classe 'AclUsuario' indica se ha
         * novas tarefas p/ serem enviadas, entao no forcarAtualizacao pode ser que nao carregue as tarefas
         * devido a esse atributo, portanto a flag abaixo 'forcarAtualizacao' indica que deve retornar as
         * tarefas independentemente do atributo 'novasTarefas' (miaaaaaaaaaauuuuuuuuuuu)
         */
        forcarAtualizacao.setName("forcarAtualizacao");
        forcarAtualizacao.setValue(this.isForcarAtualizacao());
        forcarAtualizacao.setType(Boolean.class);
        return forcarAtualizacao;
    }

}