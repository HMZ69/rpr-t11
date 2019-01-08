package ba.unsa.etf.rpr;

public class Drzava {
    private int id;
    private String naziv;
    private Grad glavni_grad;

    public Drzava() {}

    public Drzava(int id, String naziv, Grad glavni_grad) {
        this.id = id;
        this.naziv = naziv;
        this.glavni_grad = glavni_grad;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public Grad getGlavni_grad() {
        return glavni_grad;
    }

    public void setGlavni_grad(Grad glavni_grad) {
        this.glavni_grad = glavni_grad;
    }

    @Override
    public String toString() {
        return "Drzava{" + "naziv=" + naziv + ", glavni_grad=" + glavni_grad.getNaziv() + '}';
    }
}
