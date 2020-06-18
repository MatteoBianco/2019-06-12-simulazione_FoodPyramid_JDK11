package it.polito.tdp.food.db;

public class TestDAO {

	public static void main(String[] args) {
		FoodDAO dao = new FoodDAO();
		
		System.out.println(dao.listAllFood());
	}

}