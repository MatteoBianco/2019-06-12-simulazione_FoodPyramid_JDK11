package it.polito.tdp.food.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.food.db.FoodDAO;

public class Model {

	private FoodDAO dao;
	private Graph<Condiment, DefaultWeightedEdge> graph;
	private Map<Integer, Condiment> idMapCondiments;
	
	private List<Condiment> indipendentCondiments;
	private Double maxCalories;
	
	public Model() {
		this.dao = new FoodDAO();
		this.idMapCondiments = new HashMap<>();
	}
	
	public void createGraph(Double calories) {
		this.graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		Graphs.addAllVertices(this.graph, this.dao.getCondimentForCalories(idMapCondiments, calories));
		for(CondimentPair cp : this.dao.getCondimentPairsForCalories(idMapCondiments, calories)) {
			if(this.graph.containsVertex(cp.getC1()) && this.graph.containsVertex(cp.getC2())) {
				if(this.graph.getEdge(cp.getC1(), cp.getC2()) == null) 
					Graphs.addEdge(this.graph, cp.getC1(), cp.getC2(), cp.getWeight());
			}
		}
		System.out.println(graph);
	}
	
	public List<Condiment> listCondimentSortedByCalories() {
		List<Condiment> output = new ArrayList<>(this.graph.vertexSet());
		output.sort(new Comparator<Condiment> (){

			@Override
			public int compare(Condiment o1, Condiment o2) {
				return o1.getCondiment_calories().compareTo(o2.getCondiment_calories());
			}
			
		});
		return output;
	}
	
	public List<Condiment> getIndipendentCondiments(Condiment start) {
		this.indipendentCondiments = new ArrayList<>();
		this.maxCalories = 0.0;
		List<Condiment> parziale = new ArrayList<>();
		parziale.add(start);
		ricorsione(parziale, start.getCondiment_calories());
		return this.indipendentCondiments;
	}

	private void ricorsione(List<Condiment> parziale, Double actualCalories) {
		if(actualCalories > this.maxCalories) {
			this.maxCalories = actualCalories;
			this.indipendentCondiments = new ArrayList<>(parziale);
		}
		
		for(Condiment c : this.graph.vertexSet()) {
			if(! parziale.contains(c) ) {
				boolean trovato = false;
				for(Condiment added : parziale) {
					if(this.graph.getEdge(c, added) != null)
						trovato = true;
				}
				if(!trovato) {
					parziale.add(c);
					ricorsione(parziale, actualCalories+c.getCondiment_calories());
					parziale.remove(parziale.size()-1);
				}
			}
		}
 		
	}

	public Integer getVerticesSize() {
		return this.graph.vertexSet().size();
	}

	public Integer getEdgesSize() {
		return this.graph.edgeSet().size();
	}

	public Integer getFoodsForCondiment(Condiment c) {
		Integer output = 0;
		for(Condiment c2 : Graphs.neighborListOf(this.graph, c)) {
			output += (int) this.graph.getEdgeWeight(this.graph.getEdge(c, c2));
		}
		return output; 
	}
}
