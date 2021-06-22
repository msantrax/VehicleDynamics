/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opus;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class Canais implements Serializable {
    private static final long serialVersionUID = 1L;
    //@Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    //@Basic(optional = false)
    
    private String nome;
    
    private boolean digital;
    
    
//    @Column(nome = "ID", nullable = false)
    private Integer id;
//    @Column(nome = "MASCARA", length = 10)
    private String mascara;
//    @Column(nome = "UNIDADE", length = 10)
    private String unidade;
//    @Column(nome = "CANAL", length = 2)
    private String canal;
//    @Column(nome = "TIPO", length = 2)
    private String tipo;
//    @Column(nome = "A0", length = 10)
    private String a0;
//    @Column(nome = "A1", length = 10)
    private String a1;
//    @Column(nome = "A2", length = 10)
    private String a2;
//    @Column(nome = "A3", length = 10)
    private String a3;
//    @Column(nome = "A4", length = 10)
    private String a4;
//    @Column(nome = "A5", length = 10)
    private String a5;
//    @Column(nome = "ROFFSET", length = 10)
    private String roffset;
//    @Column(nome = "RSLOPE", length = 10)
    private String rslope;
//    @Column(nome = "FLAG1")
    private Integer flag1;
//    @Column(nome = "FLAG2")
    private Integer flag2;
//    @Column(nome = "MAXIMO")
    private Integer maximo;
//    @Column(nome = "MINIMO")
    private Integer minimo;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
//    @Column(nome = "AMORT", precision = 52)
    private Double amort;
//    @Column(nome = "PENA")
    private Integer pena;
//    @Column(nome = "RING")
    private Integer ring;
//    @Lob
//    @Column(nome = "SCRIPT", length = 32700)
    private String script;

//    @Transient
    private List listeners = Collections.synchronizedList(new LinkedList());
    
    public Canais() {
    }

    public Canais(Integer id) {
        this.id = id;
    }

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
        String oldnome = nome;
        this.nome = nome ;
        fire("nome", oldnome, nome);
    }
    
     /**
     * @return the digital
     */
    public boolean isDigital() {
        return digital;
    }

    /**
     * @param digital the digital to set
     */
    public void setDigital(boolean digital) {
        this.digital = digital;
    }
  
    public String getMascara() {
        return mascara;
    }

    public void setMascara(String mascara) {
        String oldmascara = mascara;
        this.mascara = mascara ;
        fire("mascara", oldmascara, mascara);
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        String oldunidade = unidade;
        this.unidade = unidade ;
        fire("unidade", oldunidade, unidade);
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
       
        
        String oldcanal = canal;
        this.canal = canal ;
        fire("canal", oldcanal, canal);
    }

    public String getTipo() {
        
        if (tipo.equals("SN")) return "Scan Normal";
        if (tipo.equals("SO")) return "Scan Oculto";
        if (tipo.equals("CN")) return "Calculado Normal";
        if (tipo.equals("CO")) return "Calculado Oculto";    
        return tipo;
    }

    
    public String getRawType (){
        return tipo;
    }
    
    public void setTipo(String tipo) {
        
        if (tipo.equals("Scan Normal")) tipo = "SN";
        if (tipo.equals("Scan Oculto")) tipo = "SO";
        if (tipo.equals("Calculado Normal")) tipo = "CN";
        if (tipo.equals("Calculado Oculto")) tipo = "CO";
        
        String oldtipo = tipo;
        this.tipo = tipo;
        fire("tipo", oldtipo, tipo);
    }

    public String getA0() {
        return a0;
    }

    public void setA0(String a0) {
        String olda0 = a0;
        this.a0 = a0 ;
        fire("a0", olda0, a0);
    }

    public String getA1() {
        return a1;
    }

    public void setA1(String a1) {
        String olda1 = a1;
        this.a1 = a1 ;
        fire("a1", olda1, a1);
    }

    public String getA2() {
        return a2;
    }

    public void setA2(String a2) {
        String olda2 = a2;
        this.a2 = a2 ;
        fire("a2", olda2, a2);
    }

    public String getA3() {
        return a3;
    }

    public void setA3(String a3) {
        String olda3 = a3;
        this.a3 = a3 ;
        fire("a3", olda3, a3);
    }

    public String getA4() {
        return a4;
    }

    public void setA4(String a4) {
        String olda4 = a4;
        this.a4 = a4 ;
        fire("a4", olda4, a4);
    }

    public String getA5() {
        return a5;
    }

    public void setA5(String a5) {
        String olda5 = a5;
        this.a5 = a5 ;
        fire("a5", olda5, a5);
    }

    public String getRoffset() {
        return roffset;
    }

    public void setRoffset(String roffset) {
        String oldroffset = roffset;
        this.roffset = roffset ;
        fire("roffset", oldroffset, roffset);
    }

    public String getRslope() {
        return rslope;
    }

    public void setRslope(String rslope) {
        String oldrslope = rslope;
        this.rslope = rslope ;
        fire("rslope", oldrslope, rslope);
    }

    public Integer getFlag1() {
        return flag1;
    }

    public void setFlag1(Integer flag1) {
        this.flag1 = flag1;
    }

    public Integer getFlag2() {
        return flag2;
    }

    public void setFlag2(Integer flag2) {
        this.flag2 = flag2;
    }

    public Integer getMaximo() {
        return maximo;
    }

    public void setMaximo(Integer maximo) {
        Integer oldmaximo = maximo;
        this.maximo = maximo ;
        fire("maximo", oldmaximo, maximo);
    }

    public Integer getMinimo() {
        return minimo;
    }

    public void setMinimo(Integer minimo) {
        Integer oldminimo = minimo;
        this.minimo = minimo ;
        fire("minimo", oldminimo, maximo);
    }

    public Double getAmort() {
        return amort;
    }

    public void setAmort(Double amort) {
        Double oldamort = amort;
        this.amort = amort ;
        fire("amort", oldamort, amort);
    }

    public Integer getPena() {
        return pena;
    }

    public void setPena(Integer pena) {
        Integer oldpena = pena;
        this.pena = pena ;
        fire("pena", oldpena, pena);
    }

    public Integer getRing() {
        return ring;
    }

    public void setRing(Integer ring) {
        Integer oldring = ring;
        this.ring = ring ;
        fire("ring", oldring, ring);
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        String oldscript = script;
        this.script = script ;
        fire("script", oldscript, script);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Canais)) {
            return false;
        }
        Canais other = (Canais) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.opus.vegadbpu.Canais[ id=" + id + " ]";
    }
    
    
     // ======================================== Property Sheet adds ===========================================

    public void addPropertyChangeListener (PropertyChangeListener pcl) {
        listeners.add (pcl);
    }

    public void removePropertyChangeListener (PropertyChangeListener pcl) {
        listeners.remove (pcl);
    }

    private void fire (String propertyName, Object old, Object nue) {
        //Passing 0 below on purpose, so you only synchronize for one atomic call:
        PropertyChangeListener[] pcls = (PropertyChangeListener[]) listeners.toArray(new PropertyChangeListener[0]);
        for (int i = 0; i < pcls.length; i++) {
            pcls[i].propertyChange(new PropertyChangeEvent (this, propertyName, old, nue));
        }
    }

   
    
}
