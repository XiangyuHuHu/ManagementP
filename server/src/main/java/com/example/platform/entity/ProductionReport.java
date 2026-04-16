package com.example.platform.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "production_report")
public class ProductionReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reportId;
    private String reportType;
    private String reportDate;
    private String shiftId;
    private Double productionAmount;
    private Double productionRate;
    private Double equipmentAvailability;
    private Double energyConsumption;
    private Double materialConsumption;
    private String operator;
    private String remark;
    private String status;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    public String getShiftId() {
        return shiftId;
    }

    public void setShiftId(String shiftId) {
        this.shiftId = shiftId;
    }

    public Double getProductionAmount() {
        return productionAmount;
    }

    public void setProductionAmount(Double productionAmount) {
        this.productionAmount = productionAmount;
    }

    public Double getProductionRate() {
        return productionRate;
    }

    public void setProductionRate(Double productionRate) {
        this.productionRate = productionRate;
    }

    public Double getEquipmentAvailability() {
        return equipmentAvailability;
    }

    public void setEquipmentAvailability(Double equipmentAvailability) {
        this.equipmentAvailability = equipmentAvailability;
    }

    public Double getEnergyConsumption() {
        return energyConsumption;
    }

    public void setEnergyConsumption(Double energyConsumption) {
        this.energyConsumption = energyConsumption;
    }

    public Double getMaterialConsumption() {
        return materialConsumption;
    }

    public void setMaterialConsumption(Double materialConsumption) {
        this.materialConsumption = materialConsumption;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
