package nasuakza.com.br.nasuakz.beans;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Equipe implements Comparable, Parcelable{
    private int id;
    private String nome;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.nome;
    }

    @Override
    public boolean equals(Object another) {
        return (this.id == ((Equipe)another).getId());
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public int compareTo(Object o) {
        if (this.id < ((Equipe)o).getId())
            return -1;
        else if (this.id == ((Equipe)o).getId())
            return 0;
        else
            return 1;
    }

    public Equipe(Parcel in) {
        id = in.readInt();
        nome = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(nome);
    }

    @SuppressWarnings("unused")
    public static final Creator<Equipe> CREATOR = new Creator<Equipe>() {
        @Override
        public Equipe createFromParcel(Parcel in) {
            return new Equipe(in);
        }

        @Override
        public Equipe[] newArray(int size) {
            return new Equipe[size];
        }
    };
}