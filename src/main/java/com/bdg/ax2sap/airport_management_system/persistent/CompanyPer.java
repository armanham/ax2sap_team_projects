package com.bdg.ax2sap.airport_management_system.persistent;

import com.bdg.ax2sap.airport_management_system.persistent.common.BaseEntity;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "company")
public class CompanyPer extends BaseEntity {

    @Column(nullable = false, length = 24)
    private String name;

    @Column(name = "found_date", nullable = false, updatable = false)
    private Date foundDate;

    public CompanyPer() {
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Date getFoundDate() {
        return foundDate;
    }

    public void setFoundDate(final Date foundDate) {
        this.foundDate = foundDate;
    }
}