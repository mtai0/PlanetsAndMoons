package com.revature.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.revature.models.Planet;
import com.revature.models.User;
import com.revature.utilities.ConnectionUtil;

public class PlanetDao {
    
    public List<Planet> getAllPlanets() {
		ArrayList<Planet> planetList = new ArrayList<Planet>();
		try(Connection connection = ConnectionUtil.createConnection()) {
			String sql = "select * from planets";
			PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.executeQuery();
			ResultSet rs = ps.executeQuery();
			while (rs.next())
			{
				Planet newPlanet = new Planet();
				newPlanet.setId(rs.getInt(1));
				newPlanet.setName(rs.getString("name"));
				newPlanet.setOwnerId(rs.getInt("ownerId"));
				planetList.add(newPlanet);
			}
		}catch (SQLException e){
			System.out.println(e);
			return planetList;
		}
		return planetList;
	}

	public Planet getPlanetByName(String planetName) {
		try(Connection connection = ConnectionUtil.createConnection()) {
			String sql = "select * from planets where name = ?";
			PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, planetName);
			ps.executeQuery();
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				Planet newPlanet = new Planet();
				newPlanet.setId(rs.getInt(1));
				newPlanet.setName(rs.getString("name"));
				newPlanet.setOwnerId(rs.getInt("ownerId"));
				return newPlanet;
			}
			else
			{
				System.out.println("No planet with name " + planetName + " found");
			}
		}catch (SQLException e){
			System.out.println(e);
		}
		return null;
	}

	public Planet getPlanetById(int planetId) {
		// TODO: implement
		return null;
	}

	public Planet createPlanet(Planet p) {
		Planet newPlanet = new Planet();
		try(Connection connection = ConnectionUtil.createConnection()) {
			String sql = "insert into planets (name, ownerId) values (?, ?)";
			PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, p.getName());
			ps.setInt(2, p.getOwnerId());
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();

			newPlanet.setId(rs.getInt(1));
			newPlanet.setName((p.getName()));
			newPlanet.setOwnerId(p.getOwnerId());
		}catch (SQLException e){
			System.out.println(e);
		}
		return newPlanet;
	}

	public boolean deletePlanetById(int planetId) {
		// TODO: implement
		return false;
	}
}
