package nasuakza.com.br.nasuakz.beans;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.HashSet;
import java.util.Set;

/**
 * Bean Usuario
 */
public class Usuario implements Comparable, Parcelable {
    private int id;
    private String nome;
    private String perfil;//TODO arumar esse gato
    private Set<Equipe> equipes;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public Set<Equipe> getEquipes() {
        return equipes;
    }

    @Override
    public String toString() {
        return this.nome;
    }
    @Override
    public boolean equals(Object o) {
        return (this.id == ((Usuario)o).getId());
    }

    @Override
    public int compareTo(Object o) {
        if (this.id < ((Usuario)o).getId())
            return -1;
        else if (this.id == ((Usuario)o).getId())
            return 0;
        else
            return 1;
    }

    public Usuario(Parcel in) {
        id = in.readInt();
        nome = in.readString();
        perfil = in.readString();
        equipes = (Set) in.readValue(Set.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(nome);
        dest.writeString(perfil);
        dest.writeValue(equipes);
    }

    @SuppressWarnings("unused")
    public static final Creator<Usuario> CREATOR = new Creator<Usuario>() {
        @Override
        public Usuario createFromParcel(Parcel in) {
            return new Usuario(in);
        }

        @Override
        public Usuario[] newArray(int size) {
            return new Usuario[size];
        }
    };

    public void setEquipes(HashSet<Equipe> equipes) {
        this.equipes = equipes;
    }
}