package model.pojo;

public class Referto {
    private int idReferto;
    private String diagnosi;
    private String prognosi;
    private String prescrizioni;
    private String dataCreazione;
    private int idVisita;

    public Referto() {}

    public Referto(int idReferto, String diagnosi, String prognosi, String prescrizioni, String dataCreazione, int idVisita) {
        this.idReferto = idReferto;
        this.diagnosi = diagnosi;
        this.prognosi = prognosi;
        this.prescrizioni = prescrizioni;
        this.dataCreazione = dataCreazione;
        this.idVisita = idVisita;
    }

    public int getIdReferto() { return idReferto; }
    public void setIdReferto(int idReferto) { this.idReferto = idReferto; }

    public String getDiagnosi() { return diagnosi; }
    public void setDiagnosi(String diagnosi) { this.diagnosi = diagnosi; }

    public String getPrognosi() { return prognosi; }
    public void setPrognosi(String prognosi) { this.prognosi = prognosi; }

    public String getPrescrizioni() { return prescrizioni; }
    public void setPrescrizioni(String prescrizioni) { this.prescrizioni = prescrizioni; }

    public String getDataCreazione() { return dataCreazione; }
    public void setDataCreazione(String dataCreazione) { this.dataCreazione = dataCreazione; }

    public int getIdVisita() { return idVisita; }
    public void setIdVisita(int idVisita) { this.idVisita = idVisita; }
}