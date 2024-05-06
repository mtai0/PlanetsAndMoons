package com.revature.service;

import java.util.List;

import com.revature.models.Moon;
import com.revature.repository.MoonDao;

public class MoonService {

	private MoonDao dao;

	public MoonService(MoonDao dao) {
		this.dao = dao;
	}

	public List<Moon> getAllMoons(int ownerId) {
		return dao.getAllMoons(ownerId);
	}

	public Moon getMoonByName(int ownerId, String moonName) {
		return dao.getMoonByName(ownerId, moonName);
	}

	public Moon getMoonById(int ownerId, int moonId) {
		return dao.getMoonById(ownerId, moonId);
	}

	public Moon createMoon(int ownerId, Moon m) {
		String moonName = m.getName();
		if (!moonName.isEmpty() && moonName.length() <= 30) {
			return dao.createMoon(ownerId, m);
		}
		return new Moon();
	}

	public boolean deleteMoonById(int ownerId, int moonId) {
		return dao.deleteMoonById(ownerId, moonId);
	}

	public List<Moon> getMoonsFromPlanet(int ownerId, int myPlanetId) {
		return dao.getMoonsFromPlanet(ownerId, myPlanetId);
	}
}
