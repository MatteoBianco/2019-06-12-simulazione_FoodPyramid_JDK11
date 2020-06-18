package it.polito.tdp.food.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.food.model.Food;
import it.polito.tdp.food.model.Condiment;
import it.polito.tdp.food.model.CondimentPair;

public class FoodDAO {

	public List<Food> listAllFood(){
		String sql = "SELECT * FROM food" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<Food> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					list.add(new Food(res.getInt("food_id"),
							res.getInt("food_code"),
							res.getString("display_name"), 
							res.getInt("portion_default"), 
							res.getDouble("portion_amount"),
							res.getString("portion_display_name"),
							res.getDouble("calories")
							));
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}

	}
	
	public List<Condiment> getCondimentForCalories(Map<Integer, Condiment> idMapCondiments, Double calories){
		String sql = "SELECT * " + 
				"FROM condiment " + 
				"WHERE condiment_calories < ? " ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			st.setDouble(1, calories);
			
			List<Condiment> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				if(! idMapCondiments.containsKey(res.getInt("condiment_id"))) {
					try {
						Condiment c = new Condiment(res.getInt("condiment_id"),
								res.getInt("food_code"),
								res.getString("display_name"), 
								res.getString("condiment_portion_size"), 
								res.getDouble("condiment_calories")
								);
						list.add(c);
						idMapCondiments.put(c.getCondiment_id(), c);
					} catch (Throwable t) {
						t.printStackTrace();
					}
				}
				else {
					list.add(idMapCondiments.get(res.getInt("condiment_id")));
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}

	}
	
	public List<CondimentPair> getCondimentPairsForCalories(Map<Integer, Condiment> idMapCondiments, Double calories){
		String sql = "SELECT c1.condiment_id, c2.condiment_id, COUNT(DISTINCT fc1.food_code) AS weight " + 
				"FROM condiment AS c1, condiment AS c2, food_condiment AS fc1, food_condiment AS fc2 " + 
				"WHERE c1.condiment_id < c2.condiment_id "
				+ "AND c1.condiment_calories < ? AND c2.condiment_calories < ? "
				+ "AND c1.food_code = fc1.condiment_food_code AND c2.food_code = fc2.condiment_food_code "
				+ "AND fc1.food_code = fc2.food_code " + 
				"GROUP BY c1.condiment_id, c2.condiment_id " ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			st.setDouble(1, calories);
			
			st.setDouble(2, calories);
			
			List<CondimentPair> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				if(! idMapCondiments.containsKey(res.getInt("c1.condiment_id")) || 
						! idMapCondiments.containsKey(res.getInt("c2.condiment_id"))) {
					throw new RuntimeException("Errore! Prima creare opportunamente i vertici, poi gli archi!");
				}
				else {
					CondimentPair cp = new CondimentPair(idMapCondiments.get(res.getInt("c1.condiment_id")),
							idMapCondiments.get(res.getInt("c2.condiment_id")), res.getInt("weight"));
					list.add(cp);
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}

	}

}

