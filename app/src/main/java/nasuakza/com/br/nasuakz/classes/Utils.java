package nasuakza.com.br.nasuakz.classes;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.ViewFlipper;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import nasuakza.com.br.nasuakz.R;


/**
 * Utilidades para o projeto
 */
public class Utils{
    public static Context contexto;

    /** @param contexto Contexto da activity
     */
    public Utils(Context contexto){
        this.contexto = contexto;
    }

    /** Método sobrecarregado para mostrar/ocultar a barra de progresso
     * @param barraProgresso
     * @param mostra
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void barraProgresso(final ProgressBar barraProgresso, final boolean mostra) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int animacaoCurta = contexto.getResources().getInteger(android.R.integer.config_shortAnimTime);
            barraProgresso.setVisibility(mostra ? View.VISIBLE : View.GONE);
            barraProgresso.animate().setDuration(animacaoCurta).alpha(
                    mostra ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    barraProgresso.setVisibility(mostra ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            barraProgresso.setVisibility(mostra ? View.VISIBLE : View.GONE);
        }
    }

    /** Método sobrecarregado para mostrar/ocultar a barra de progresso
     * @param contexto
     * @param barraProgresso
     * @param mostra
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static void barraProgresso(Context contexto, final ProgressBar barraProgresso, final boolean mostra) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int animacaoCurta = contexto.getResources().getInteger(android.R.integer.config_shortAnimTime);
            if (barraProgresso != null) {
                barraProgresso.setVisibility(mostra ? View.VISIBLE : View.GONE);
                barraProgresso.animate().setDuration(animacaoCurta).alpha(
                        mostra ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        barraProgresso.setVisibility(mostra ? View.VISIBLE : View.GONE);
                    }
                });
            }
        } else {
            if (barraProgresso != null)
                barraProgresso.setVisibility(mostra ? View.VISIBLE : View.GONE);
        }
    }

    /**método sobrecarregado para criar adaptador para Spinner
     * @param lista Array de strings
     * @return  adaptador de String para Spinner
     */
    public ArrayAdapter setAdaptador(String[] lista){
        ArrayAdapter adaptador = new ArrayAdapter<String>(contexto, android.R.layout.simple_spinner_item, lista){
            @Override
            public boolean isEnabled(int posicao){
                return (posicao == 0) ? false : true;
            }
        };
        return adaptador;
    }

    /**método sobrecarregado para criar adaptador para Spinner
     * @param contexto
     * @param lista Array de strings
     * @return  adaptador de String para Spinner
     */
    public static ArrayAdapter setAdaptador(Context contexto, String[] lista){
        ArrayAdapter adaptador = new ArrayAdapter<String>(contexto, android.R.layout.simple_spinner_item, lista){
            @Override
            public boolean isEnabled(int posicao){
                return (posicao == 0) ? false : true;
            }
        };
        return adaptador;
    }

    /** Cria cx de diálogo para DatePicker
     * implemente a interface para pegar a data escolhida
     */
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        Listener listener;

        public interface Listener{
            /** pega a data escolhida
             * @return data */
            public void getData(String data);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            //pega data atual
            final Calendar calendario = Calendar.getInstance();
            int dia = calendario.get(Calendar.DAY_OF_MONTH);
            int mes = calendario.get(Calendar.MONTH);
            int ano = calendario.get(Calendar.YEAR);
            listener = (Listener) getActivity();
            return new DatePickerDialog(getActivity(), this, ano, mes, dia);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            //pega data escolhida no DatePicker
            Calendar calendario = Calendar.getInstance();
            calendario.set(year, month, day);
            //formata a data
            SimpleDateFormat dataFormatada = new SimpleDateFormat("dd/MM/yyyy");
            String data = dataFormatada.format(calendario.getTime());
            if (listener != null)
                listener.getData(data);
        }
    }

    /**
     *  detecta movimentos de fling (deslizar entre telas)
     * @param event Evento onTouch da activity
     * @param viewFlipper viewFlipper que fará o trabalho de deslizar a tela
     * @param layoutEsquerda O view layout para deslizar para esquerda
     * @param layoutDireita O view layout para deslizar para direita
     * @return ok
     */
    public static boolean onTouchEvent(MotionEvent event, ViewFlipper viewFlipper, View layoutEsquerda, View layoutDireita){
        /*Log.i("classes",
                "MotionEvent: "+event.getClass().toString()+"\n"
                +"ViewFlipper: "+viewFlipper.getClass().toString()+"\n"
                +"ViewEsquerda: "+layoutEsquerda.getClass().toString()+"\n"
                +"ViewDireita: "+layoutDireita.getClass().toString());*/
        float x1 = 0, x2, y1, y2; //coordenadas de touchEvent
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:{
                x1 = event.getX();
                y1 = event.getY();
                break;
            }
            case MotionEvent.ACTION_UP: {
                x2 = event.getX();
                y2 = event.getY();
                //desliza layouts da esquerda pra direita
                if (x1 < x2) {
                    //se já está no layout dashboard então não precisa deslizar
                    if (viewFlipper.getCurrentView().getId() == layoutEsquerda.getId())
                        break;
                    viewFlipper.setInAnimation(contexto, R.anim.entra_esquerda);
                    viewFlipper.setOutAnimation(contexto, R.anim.sai_direita);
                    viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(layoutEsquerda));//showNext()
                }else if (x1 > x2) {//desliza layouts da direita pra esquerda
                    //se já está no layout tarefas então não precisa deslizar
                    if (viewFlipper.getCurrentView().getId() == layoutDireita.getId())
                        break;
                    deslizaLayoutDireita(viewFlipper, layoutDireita);
                }
                break;
            }
                /*//de cima para baixo
                if (y1 < y2){
                    Toast.makeText(this, "UP to Down Swap Performed", Toast.LENGTH_LONG).show();
                }
                //de baixo pra cima
                if (y1 > y2){
                    Toast.makeText(this, "Down to UP Swap Performed", Toast.LENGTH_LONG).show();
                }
                break;*/
        }
        return false;
    }

    /**
     * Desliza um layout para a direita
     * @param viewFlipper O viewFlipper que fará o trabalho de deslizar
     * @param layoutDireita O layout para o qual deslizar para a direita
     */
    public static void deslizaLayoutDireita(ViewFlipper viewFlipper, View layoutDireita){
        viewFlipper.setInAnimation(contexto, R.anim.entra_direita);
        viewFlipper.setOutAnimation(contexto, R.anim.sai_esquerda);
        viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(layoutDireita));//showPrevious();
    }

    /**
     * Desliza um layout para a direita
     * @param viewFlipper O viewFlipper que fará o trabalho de deslizar
     * @param layoutEsquerda O layout para o qual deslizar para a esquerda
     */
    public static void deslizaLayoutEsquerda(ViewFlipper viewFlipper, View layoutEsquerda){
        viewFlipper.setInAnimation(contexto, R.anim.entra_esquerda);
        viewFlipper.setOutAnimation(contexto, R.anim.sai_direita);
        viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(layoutEsquerda));//showPrevious();
    }

}