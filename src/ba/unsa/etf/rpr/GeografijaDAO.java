package ba.unsa.etf.rpr;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;

public class GeografijaDAO {
    private static GeografijaDAO instance = null;
    private Connection conn;

    private static GeografijaDAO initialize() throws SQLException {
        return instance = new GeografijaDAO();
    }

    private void initializeTabele() throws SQLException {
        PreparedStatement ps = conn.prepareStatement("CREATE TABLE grad (\n"
                + "	    id int PRIMARY KEY,\n"
                + "	    naziv text,\n"
                + "	    broj_stanovnika int,\n"
                + "     drzava int ,\n"
                // + "     FOREIGN KEY (drzava) REFERENCES drzava(id)"
                + ");");
        ps.execute();

        ps = conn.prepareStatement("CREATE TABLE drzava (\n"
                + "	    id int PRIMARY KEY,\n"
                + "	    naziv text,\n"
                + "     glavni_grad int,\n"
                + "     FOREIGN KEY (glavni_grad) REFERENCES grad(id)"
                + ");");
        ps.execute();
    }

    private void insertGradove() throws SQLException {
        String sql = "INSERT INTO grad(id,naziv,broj_stanovnika,drzava) VALUES(?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setInt(1,1);
        ps.setString(2,"Pariz");
        ps.setInt(3, 2206488);
        ps.setInt(4,1);
        ps.executeUpdate();

        ps.setInt(1, 2);
        ps.setString(2, "London");
        ps.setInt(3, 8825000);
        ps.setInt(4, 2);
        ps.executeUpdate();

        ps.setInt(1, 3);
        ps.setString(2, "Beč");
        ps.setInt(3, 1867582);
        ps.setInt(4, 3);
        ps.executeUpdate();

        ps.setInt(1, 4);
        ps.setString(2, "Mančester");
        ps.setInt(3, 545500);
        ps.setInt(4, 2);
        ps.executeUpdate();

        ps.setInt(1, 5);
        ps.setString(2, "Grac");
        ps.setInt(3, 325021);
        ps.setInt(4, 3);
        ps.executeUpdate();
    }

    private void insertDrzave() throws SQLException {
        String sql = "INSERT INTO drzava(id,naziv,glavni_grad) VALUES(?,?,?)";
        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setInt(1, 1);
        ps.setString(2, "Francuska");
        ps.setInt(3,  1);
        ps.executeUpdate();

        ps.setInt(1, 2);
        ps.setString(2, "Engleska");
        ps.setInt(3,  2);
        ps.executeUpdate();

        ps.setInt(1, 3);
        ps.setString(2, "Austrija");
        ps.setInt(3,  3);
        ps.executeUpdate();
    }

    private GeografijaDAO() throws SQLException {
        File file = new File("baza.db");
        boolean postoji = file.exists();

        conn = DriverManager.getConnection("jdbc:sqlite:baza.db");
        initializeTabele();

        if (!postoji) {
            insertDrzave();
            insertGradove();
        }
    }

    public static GeografijaDAO getInstance() throws SQLException {
        if (instance == null)
            initialize();
        return instance;
    }

    public static void removeInstance() {
        instance = null;
    }

    public Grad glavniGrad(String drzava) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("select * from drzava where naziv=?");
        ps.setString(1, drzava);
        ResultSet result = ps.executeQuery();

        Drzava novaDrzava = new Drzava();
        Grad noviGrad = new Grad();

        novaDrzava.setId(result.getInt("id"));
        novaDrzava.setNaziv(result.getString("naziv"));
        novaDrzava.setGlavni_grad(noviGrad);

        ps = conn.prepareStatement("select * from grad where drzava=?");
        ps.setInt(1, result.getInt("id"));
        result = ps.executeQuery();

        noviGrad.setId(result.getInt("id"));
        noviGrad.setBroj_stanovnika(result.getInt("broj_stanovnika"));
        noviGrad.setNaziv(result.getString("naziv"));
        noviGrad.setDrzava(novaDrzava);

        return noviGrad;
    }

    public void obrisiDrzavu(String drzava) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("select id from drzava where naziv=?");
        ps.setString(1, drzava);
        ResultSet result = ps.executeQuery();

        ps = conn.prepareStatement("delete from drzava where naziv=?");
        ps.setString(1, drzava);
        ps.executeUpdate();

        ps = conn.prepareStatement("delete from grad where drzava=?");
        ps.setInt(1, result.getInt("id"));
        ps.executeUpdate();
    }

    public ArrayList<Grad> gradovi() throws SQLException {
        ArrayList<Grad> rvalue = new ArrayList<Grad>();

        PreparedStatement ps = conn.prepareStatement("select * from grad order by broj_stanovnika desc");
        ResultSet result = ps.executeQuery();

        while (result.next()) {
            Grad noviGrad = new Grad();
            noviGrad.setId(result.getInt("id"));
            noviGrad.setNaziv(result.getString("naziv"));
            noviGrad.setBroj_stanovnika(result.getInt("broj_stanovnika"));

            ps = conn.prepareStatement("select * from drzava where id=?");
            ps.setInt(1, result.getInt("drzava"));
            ResultSet result2 = ps.executeQuery();
            noviGrad.setDrzava(new Drzava(result2.getInt("id"), result2.getString("naziv"), noviGrad));
            rvalue.add(noviGrad);
        }

        return rvalue;
    }

    public void dodajGrad(Grad grad) throws SQLException {
        int zadnjiId = getZadnjiId("grad");
        PreparedStatement ps = conn.prepareStatement("select id from drzava where naziv=?");
        ps.setString(1, grad.getDrzava().getNaziv());
        ResultSet result = ps.executeQuery();

        ps = conn.prepareStatement("INSERT INTO grad(id,naziv,broj_stanovnika,drzava) VALUES(?,?,?,?)");

        ps.setInt(1, ++zadnjiId);
        ps.setString(2, grad.getNaziv());
        ps.setInt(3, grad.getBroj_stanovnika());
        if (!result.isClosed()) {
            ps.setInt(4, result.getInt("id"));
        }
        ps.executeUpdate();
    }

    public void dodajDrzavu(Drzava drzava) throws SQLException {
        int zadnjiId = getZadnjiId("drzava");

        PreparedStatement ps = conn.prepareStatement("select id from grad where naziv=?");
        ps.setString(1, drzava.getGlavni_grad().getNaziv());
        ResultSet result = ps.executeQuery();

        if (result.isClosed())
            return;

        ps = conn.prepareStatement("INSERT INTO drzava(id,naziv,glavni_grad) VALUES(?,?,?)");

        ps.setInt(1, ++zadnjiId);
        ps.setString(2, drzava.getNaziv());
        ps.setInt(3, result.getInt("id"));
        ps.executeUpdate();
    }

    public void izmijeniGrad(Grad grad) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("update grad set naziv=?,broj_stanovnika=?,drzava=? where id=?");
        ps.setString(1, grad.getNaziv());
        ps.setInt(2, grad.getBroj_stanovnika());
        ps.setInt(3, grad.getDrzava().getId());
        ps.setInt(4, grad.getId());
        ps.executeUpdate();
    }

    public Drzava nadjiDrzavu(String drzava) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("select * from drzava where naziv=?");
        ps.setString(1, drzava);
        ResultSet result = ps.executeQuery();

        if (result.isClosed())
            return null;

        Drzava novaDrzava = new Drzava();
        Grad noviGrad = new Grad();

        novaDrzava.setId(result.getInt("id"));
        novaDrzava.setNaziv(result.getString("naziv"));
        novaDrzava.setGlavni_grad(noviGrad);

        ps = conn.prepareStatement("select * from grad where id=?");
        ps.setInt(1, result.getInt("glavni_grad"));
        ResultSet result2 = ps.executeQuery();

        noviGrad.setId(result2.getInt("id"));
        noviGrad.setNaziv(result2.getString("naziv"));
        noviGrad.setBroj_stanovnika(result2.getInt("broj_stanovnika"));
        noviGrad.setDrzava(novaDrzava);

        return novaDrzava;
    }

    private int getZadnjiId(String t) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("select id from " + t + " order by id desc");
        ResultSet result = ps.executeQuery();

        return result.getInt("id");
    }
}
