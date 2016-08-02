package com.eeda123.ui.gateOut;


public class SearchResultItem {
	private String gateOutOrderNo;
	private String waveOrderNo;
	private String salesOrderNo;
	private double amount;
	private String shelves;
	private int seq;

	public SearchResultItem(String gateOutOrderNo, String waveOrderNo, String salesOrderNo, double amount, String shelves, int seq) {
	    this.gateOutOrderNo = gateOutOrderNo;
	    this.waveOrderNo = waveOrderNo;
	    this.salesOrderNo=salesOrderNo;
		this.amount = amount;
		this.shelves = shelves;
		this.seq = seq;
	}

    public String getGateOutOrderNo() {
        return gateOutOrderNo;
    }

    public void setGateOutOrderNo(String gateOutOrderNo) {
        this.gateOutOrderNo = gateOutOrderNo;
    }

    public String getSalesOrderNo() {
        return salesOrderNo;
    }

    public void setSalesOrderNo(String salesOrderNo) {
        this.salesOrderNo = salesOrderNo;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getShelves() {
        return shelves;
    }

    public void setShelves(String shelves) {
        this.shelves = shelves;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getWaveOrderNo() {
        return waveOrderNo;
    }

    public void setWaveOrderNo(String waveOrderNo) {
        this.waveOrderNo = waveOrderNo;
    }

	
}