package nasuakza.com.br.nasuakz.classes.provedorDados;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import nasuakza.com.br.nasuakz.beans.Projeto;
import nasuakza.com.br.nasuakz.beans.Tarefa;

/**
 * Provê os dados dos beans Projeto e Tarefa
 */
public abstract class ProvedorDados{
    protected TreeMap<Projeto, List<Tarefa>> projetosTreeMapBean = new TreeMap<Projeto, List<Tarefa>>();

    /**gera TreeMapTesteEstático de projetosPessoais contendo tarefas como nós
     * @return TreeMap String, ListString dos projetosPessoais com as tarefas (estático)
     */
    public TreeMap<String, List<String>> getDadosTreeMapTeste() {
        TreeMap<String, List<String>> projetosTreeMap = new TreeMap<String, List<String>>();
        //cria ArrayList de tarefas
        List<String> terraplanagem = new ArrayList<String>();
        List<String> asfaltamento = new ArrayList<String>();
        List<String> condominio = new ArrayList<String>();
        //adiciona tarefas no ArrayList
        for (int i = 0; i < ArrayDadosTarefas.terraplanagem.length; i++) {
            terraplanagem.add(ArrayDadosTarefas.terraplanagem[i]);
        }
        Collections.sort(terraplanagem);//ordena
        for (int i = 0; i < ArrayDadosTarefas.asfaltamento.length; i++) {
            asfaltamento.add(ArrayDadosTarefas.asfaltamento[i]);
        }
        Collections.sort(asfaltamento);
        for (int i = 0; i < ArrayDadosTarefas.condominio.length; i++) {
            condominio.add(ArrayDadosTarefas.condominio[i]);
        }
        Collections.sort(condominio);
        //adiciona tarefas no projeto
        projetosTreeMap.put("Terraplanagem Aeroporto", terraplanagem);
        projetosTreeMap.put("Asfaltamento Periferia", asfaltamento);
        projetosTreeMap.put("Condominio Centro", condominio);

        return projetosTreeMap;
    }

    public TreeMap<Projeto, List<Tarefa>> getTreeMapBeanProjetosTarefas() {
        return this.projetosTreeMapBean;
    }

    @Deprecated
    /**Busca os beans Projeto e Tarefa do Xml e transforma em TreeMap de String
     * inverteAgrupamento:
     * @param inverteAgrupamento se True inverte o agrupamento do TreeMap agrupando por tarefas
     *                          se False deixa agrupamento por projetosPessoais e tarefas com nós dos projetosPessoais
     * @return TreeMap String, ListString dos projetosPessoais com as tarefas **/
    public TreeMap<String, List<String>> getTarefas(boolean inverteAgrupamento){
        //TreeMap dos projetosPessoais convertidos de beans para strings
        TreeMap<String, List<String>> projetosTreeMapString = new TreeMap<String, List<String>>();
        //para cada Projeto com sua lista de Tarefa no TreeMap de beans...
        for (Map.Entry<Projeto, List<Tarefa>> projetosTarefasBean : this.projetosTreeMapBean.entrySet()){
            Projeto projetoBean = projetosTarefasBean.getKey();//pega o projeto atual
            List<Tarefa> tarefasBean = projetosTarefasBean.getValue();//pega a lista de tarefas do projeto
            if (inverteAgrupamento) {//se for pra inverter o agrupamento...
                String nomeProjeto = projetoBean.getNome();
                //...para cada tarefa adiciona no TreeMap de String a tarefa e seu projeto, sem sublista...
                for (Tarefa tarefa : tarefasBean) {
                    SimpleDateFormat formataData = new SimpleDateFormat("dd/MM/yyy", new Locale("pt","BR"));
                    String data = formataData.format(tarefa.getVencimento());
                    List<String> projetosString = new ArrayList<String>();
                    projetosString.add(0, nomeProjeto + " [" + data + ']');
                    projetosTreeMapString.put(tarefa.getNome(), projetosString);
                }
            }else{//...senão, adiciona no TreeMap de String o projeto com sua sublista de tarefas
                List<String> tarefasString = new ArrayList<String>();
                for (Tarefa tarefa : tarefasBean)
                    tarefasString.add(tarefa.getNome());
                projetosTreeMapString.put(projetoBean.getNome(), tarefasString);
            }
        }
        return projetosTreeMapString;
    }

}