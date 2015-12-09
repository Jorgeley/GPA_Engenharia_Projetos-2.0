package nasuakza.com.br.nasuakz.beans;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.Date;

public class Tarefa implements Comparable, Parcelable{
    private Integer id;
    private String nome;
    private String descricao;
    private String comentario;
    private Date vencimento;
    private String status;
    private Usuario usuario;
    private Projeto projeto;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Projeto getProjeto() {
        return projeto;
    }

    public void setProjeto(Projeto projeto) {
        this.projeto = projeto;
    }

    @Override
    public boolean equals(Object another) {
        return (this.id == ((Tarefa)another).getId());
    }

    @Override
    public int compareTo(Object o) {
        if (this.id < ((Tarefa)o).getId())
            return -1;
        else if (this.id == ((Tarefa)o).getId())
            return 0;
        else
            return 1;
    }

    public Tarefa(Parcel in) {
        id = in.readByte() == 0x00 ? null : in.readInt();
        nome = in.readString();
        descricao = in.readString();
        comentario = in.readString();
        long tmpVencimento = in.readLong();
        vencimento = tmpVencimento != -1 ? new Date(tmpVencimento) : null;
        status = in.readString();
        usuario = (Usuario) in.readValue(Usuario.class.getClassLoader());
        projeto = (Projeto) in.readValue(Projeto.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(id);
        }
        dest.writeString(nome);
        dest.writeString(descricao);
        dest.writeString(comentario);
        dest.writeLong(vencimento != null ? vencimento.getTime() : -1L);
        dest.writeString(status);
        dest.writeValue(usuario);
        dest.writeValue(projeto);
    }

    @SuppressWarnings("unused")
    public static final Creator<Tarefa> CREATOR = new Creator<Tarefa>() {
        @Override
        public Tarefa createFromParcel(Parcel in) {
            return new Tarefa(in);
        }

        @Override
        public Tarefa[] newArray(int size) {
            return new Tarefa[size];
        }
    };
}