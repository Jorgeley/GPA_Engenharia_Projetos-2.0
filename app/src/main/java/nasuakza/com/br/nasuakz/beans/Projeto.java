package nasuakza.com.br.nasuakz.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Projeto implements Comparable, Parcelable {
    private Integer id;
    private String nome;
    private String descricao;
    private Date vencimento;
    private Equipe equipe;
    private Usuario usuario;

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

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public Equipe getEquipe() {
        return equipe;
    }

    public void setEquipe(Equipe equipe) {
        this.equipe = equipe;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public boolean equals(Object another) {
        return (this.id == ((Projeto)another).getId());
    }

    @Override
    public int compareTo(Object o) {
        if (this.id < ((Projeto)o).getId())
            return -1;
        else if (this.id == ((Projeto)o).getId())
            return 0;
        else
            return 1;
    }

    @Override
    public String toString() {
        return this.nome;
    }


    public Projeto(Parcel in) {
        id = in.readByte() == 0x00 ? null : in.readInt();
        nome = in.readString();
        descricao = in.readString();
        long tmpVencimento = in.readLong();
        vencimento = tmpVencimento != -1 ? new Date(tmpVencimento) : null;
        equipe = (Equipe) in.readValue(Equipe.class.getClassLoader());
        usuario = (Usuario) in.readValue(Usuario.class.getClassLoader());
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
        dest.writeLong(vencimento != null ? vencimento.getTime() : -1L);
        dest.writeValue(equipe);
        dest.writeValue(usuario);
    }

    @SuppressWarnings("unused")
    public static final Creator<Projeto> CREATOR = new Creator<Projeto>() {
        @Override
        public Projeto createFromParcel(Parcel in) {
            return new Projeto(in);
        }

        @Override
        public Projeto[] newArray(int size) {
            return new Projeto[size];
        }
    };
}