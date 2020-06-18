package it.polito.tdp.food.model;

public class CondimentPair {

	private Condiment c1;
	private Condiment c2;
	private Integer weight;
	
	public CondimentPair(Condiment c1, Condiment c2, Integer weight) {
		super();
		this.c1 = c1;
		this.c2 = c2;
		this.weight = weight;
	}

	public Condiment getC1() {
		return c1;
	}

	public Condiment getC2() {
		return c2;
	}

	public Integer getWeight() {
		return weight;
	}
	
	
}
