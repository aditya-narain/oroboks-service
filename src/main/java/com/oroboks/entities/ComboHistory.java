package com.oroboks.entities;



import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Table representing Availaibility of Combo
 * @author Aditya Narain
 */
@Entity
@NamedQueries({
    @NamedQuery(name="ComboHistory.getWeekCombosFromCurrent", query="select combos from ComboHistory combos where combos.comboServingDate BETWEEN :currentDate AND :targetDate AND combos.comboId IN (:combosList)"),
    @NamedQuery(name="comboHistory.getCombosHistory", query = "select combos from ComboHistory combos where combos.comboId = :comboId")
})
@Table(name = "ORO_COMBO_HISTORY")
public class ComboHistory extends BaseEntity {

    /**
     * Default Serial version
     */
    private static final long serialVersionUID = -3697665549463683610L;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "COMBO_UUID")
    private Combo comboId;

    @NotNull
    @Column(name = "COMBO_AVAIL_DATE")
    private Date comboServingDate;

    /**
     * Default JPA constructor
     */
    public ComboHistory(){
	/*
	 * Empty JPA constructor
	 */
    }

    /**
     * @param comboId
     * @param comboServingDate
     */
    public ComboHistory(Combo comboId, Date comboServingDate){
	if(comboId == null){
	    throw new IllegalArgumentException("comboid cannot be null");
	}
	if(comboServingDate == null){
	    throw new IllegalArgumentException("comboServingDate cannot be null");
	}
	this.comboId = comboId;
	this.comboServingDate = comboServingDate;
    }

    /**
     * Gets the combo id
     * @return non-null combo id
     */
    public Combo getComboId() {
	return comboId;
    }

    /**
     * Returns dates combo has been served or is going to be served.
     * @return non-null, non-empty dates combo has been served or is going to be served.
     */
    public Date getComboServingDate() {
	return comboServingDate;
    }


}
