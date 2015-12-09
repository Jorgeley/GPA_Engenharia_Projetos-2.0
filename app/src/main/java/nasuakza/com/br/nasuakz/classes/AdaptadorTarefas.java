package nasuakza.com.br.nasuakz.classes;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

import nasuakza.com.br.nasuakz.R;
import nasuakza.com.br.nasuakz.beans.Projeto;
import nasuakza.com.br.nasuakz.beans.Tarefa;

/**
Adaptador do listView expansível
 */
public class AdaptadorTarefas extends BaseExpandableListAdapter {
    private Context contexto;
    private TreeMap<Tarefa, List<Projeto>> tarefasTreeMap;
    private Object[] tarefasArray;
    private Tarefa tarefa;
    //private List<Tarefa> tarefasProjetos;

    public AdaptadorTarefas(Context contexto, TreeMap<Tarefa, List<Projeto>> tarefasTreeMap) {
        this.contexto = contexto;
        this.tarefasTreeMap = tarefasTreeMap;
        /**
         *  keyset:pega as chaves do TreeMap(no caso objetos Projeto),
         *  transforma em array e pega na posiçao correta
         */
        this.tarefasArray = this.tarefasTreeMap.keySet().toArray();
        //this.tarefasProjetos = tarefasProjetos;
    }

    @Override
    public int getGroupCount() {
        return this.tarefasTreeMap.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        this.tarefa = (Tarefa) tarefasArray[groupPosition];
        /*Log.i("getChildrenCount",this.tarefa.getNome());
        Log.i("getChildrenCount", String.valueOf(this.tarefasTreeMap.get(this.tarefa)));*/
        return (this.tarefasTreeMap.get(this.tarefa) != null)
                    ? this.tarefasTreeMap.get(this.tarefa).size()
                    : 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        this.tarefa = (Tarefa) tarefasArray[groupPosition];
        return this.tarefa;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        this.tarefa = (Tarefa) tarefasArray[groupPosition];
        return (this.tarefasTreeMap.get(this.tarefa) != null)
                    ? this.tarefasTreeMap.get(this.tarefa).get(childPosition)
                    : null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,View convertView, ViewGroup parent) {
        Projeto projeto = (Projeto) getChild(groupPosition, 0);
        if (projeto != null) {
            Tarefa tarefa = (Tarefa) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater inflater =
                        (LayoutInflater) this.contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.parent_layout, parent, false);
            }
            TextView parentTextView = (TextView) convertView.findViewById(R.id.textViewParent);
            SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yy", new Locale("pt", "BR"));
            String data = formatoData.format(tarefa.getVencimento());//seta data
            String tarefaHTML = "<font face='sans-serif' color='#FFFFFF'>" + tarefa.getNome() + "</font>";
            //if (AtvBase.provedorDados instanceof ProvedorDadosTarefasEquipe)
                tarefaHTML += " <small><font face='sans-serif-thin' color='#E8E8E8'><i>("+tarefa.getUsuario()+")</i></font></small>";
            tarefaHTML +="<br><small><font face='sans-serif-thin' color='#EEEED1'><i>" + projeto.getNome() + " [" + data + "]</i></font></small>";
            Spanned projetoString = Html.fromHtml(tarefaHTML);
            parentTextView.setText(projetoString);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) this.contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.child_layout, parent, false);
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}