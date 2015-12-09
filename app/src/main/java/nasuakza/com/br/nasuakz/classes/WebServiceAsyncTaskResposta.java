package nasuakza.com.br.nasuakz.classes;

/**
 * Interface da classe WebServiceAsyncTask responsavel por repassar a resposta do 'onPostExecute' da mesma
 */
public interface WebServiceAsyncTaskResposta {
    public void onGetSoapEnvelope(Object envelope);
}
