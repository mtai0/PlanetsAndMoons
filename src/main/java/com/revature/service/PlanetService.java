package com.revature.service;

import java.util.List;

import com.revature.controller.PlanetController;
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
		return dao.getPlanetByName(ownerId, planetName);
	}

	public Planet getPlanetById(int ownerId, int planetId) {
		return dao.getPlanetById(ownerId, planetId);
	}

	public Planet createPlanet(int ownerId, Planet planet) {
		String planetName = planet.getName();
		int nameLength = planetName.length();
		if (nameLength <= 30 && nameLength > 0)
		{
			Planet existingPlanet = dao.getPlanetByName(ownerId, planetName);
			if (existingPlanet == null)
			{
				return dao.createPlanet(planet);
			}
			return existingPlanet;	//Should this return empty or existing?
		}
		return new Planet();
	}

	public boolean deletePlanetById(int ownerId, int planetId) {
		return dao.deletePlanetById(ownerId, planetId);
	}
}
