package nasuakza.com.br.nasuakz.classes;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

import nasuakza.com.br.nasuakz.R;
import nasuakza.com.br.nasuakz.beans.Projeto;
import nasuakza.com.br.nasuakz.beans.Tarefa;

/**
Adaptador do listView expansível
 */
public class AdaptadorProjetos extends BaseExpandableListAdapter {
    private Context contexto;
    private TreeMap<Projeto, List<Tarefa>> projetosTreeMap;
    private Object[] projetosArray;
    private Projeto projeto;
    //private List<Tarefa> tarefasProjetos;

    public AdaptadorProjetos(Context contexto, TreeMap<Projeto, List<Tarefa>> projetosTreeMap) {
        this.contexto = contexto;
        this.projetosTreeMap = projetosTreeMap;
        this.projetosArray = this.projetosTreeMap.keySet().toArray();
        //this.tarefasProjetos = tarefasProjetos;
    }

    @Override
    public int getGroupCount() {
        return this.projetosTreeMap.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        this.projeto = (Projeto) projetosArray[groupPosition];
        /*Log.i("getChildrenCount",this.projeto.getNome());
        Log.i("getChildrenCount", String.valueOf(this.projetosTreeMap.get(this.projeto)));*/
        return this.projetosTreeMap.get(this.projeto).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        this.projeto = (Projeto) projetosArray[groupPosition];
        /**
         *  keyset:pega as chaves do TreeMap(no caso objetos Projeto),
         *  transforma em array e pega na posiçao correta
         */
        return this.projeto;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        this.projeto = (Projeto) projetosArray[groupPosition];
        return this.projetosTreeMap.get(this.projeto).get(childPosition);
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
        Projeto projeto = (Projeto) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) this.contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.parent_layout, parent, false);
        }
        TextView parentTextView = (TextView) convertView.findViewById(R.id.textViewParent);
        parentTextView.setText(projeto.getNome());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        //Log.i("test", "parent view: " + parent.getTag());

        Tarefa tarefa = (Tarefa) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) this.contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.child_layout, parent, false);
        }
        TextView childTextView = (TextView) convertView.findViewById(R.id.textViewChild);
        childTextView.setText(tarefa.getNome());
        //convertView.setVisibility(View.INVISIBLE);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}