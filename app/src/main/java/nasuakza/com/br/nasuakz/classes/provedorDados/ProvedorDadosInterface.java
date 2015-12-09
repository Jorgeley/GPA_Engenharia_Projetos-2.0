package nasuakza.com.br.nasuakz.classes.provedorDados;

/** interface para as classes filhas de ProvedorDados */
public interface ProvedorDadosInterface {

    /**
     * Busca o TreeMap de beans Projeto e Tarefa do XML e seta
     * no objeto projetosTreeMapBean
     */
    public abstract void setProjetosTreeMapBean();
}
