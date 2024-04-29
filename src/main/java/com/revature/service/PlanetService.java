package com.revature.service;

import java.util.List;

import com.revature.models.Planet;
import com.revature.repository.PlanetDao;

public class PlanetService {

	private PlanetDao dao;

	public PlanetService(PlanetDao dao){
		this.dao = dao;
	}

	public List<Planet> getAllPlanets() {
		return dao.getAllPlanets();
	}

	public Planet getPlanetByName(int ownerId, String planetName) {
		// TODO Auto-generated method stub
		return null;
	}

	public Planet getPlanetById(int ownerId, int planetId) {
		// TODO Auto-generated method stub
		return null;
	}

	public Planet createPlanet(int ownerId, Planet planet) {
		String planetName = planet.getName();
		int nameLength = planetName.length();
		if (nameLength <= 30 && nameLength > 0)
		{
			Planet existingPlanet = dao.getPlanetByName(planetName);
			if (existingPlanet == null)
			{
				System.out.println("Planet not found, creating new.");
				return dao.createPlanet(planet);
			}
			else
			{
				System.out.println("Planet found, skipping.");
			}
			//return existingPlanet;	//Should this return empty or existing?
		}
		return new Planet();
	}

	public boolean deletePlanetById(int ownerId, int planetId) {
		// TODO Auto-generated method stub
		return false;
	}
}
