package projects.dao;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import projects.entity.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import projects.exception.DbException;
import provided.util.DaoBase;

public class ProjectDao extends DaoBase {
	
	public static final String CATEGORY_TABLE = "category";
	public static final String MATERIAL_TABLE = "material";
	public static final String PROJECT_TABLE = "project";
	public static final String PROJECT_CATEGORY_TABLE = "project_category";
	public static final String STEP_TABLE = "step";
	
	
	public Project insertProject(Project project) {
		//@formatter:off
		String sql = ""
				+ "INSERT INTO " + PROJECT_TABLE +  " "
				+ "(project_name, estimated_hours, actual_hours, difficulty, notes) "
				+ "VALUES "
				+ "(?, ?, ?, ?, ?)";
				
		//@formatter:on
		
		/** Steps to insert data in to DB
		 * 1) open a connection 
		 * 2) start the transaction 
		 * 3) commit the transaction 
		 * 
		 */
		
		
		// get a connection object 
		try(Connection conn = DbConnection.getConnection()){
			
			// start the transaction
			startTransaction(conn);
			
			// Build prepared statement
			try(PreparedStatement stmt = conn.prepareStatement(sql)){
				
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);
				
				// Sends the SQL "INSERT INTO" command 
				stmt.executeUpdate();
				
				// retrieves the last Id inserted ( the project id) 
				Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
				
				
				//Commits the transaction 
				commitTransaction(conn);
				
				// sets the Id retrieved to be the projectId value on the project object
				project.setProjectId(projectId);
				return project;
				
				
			}
			catch(Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
		
				
			}
		}
		catch(SQLException e) {
			throw new DbException(e);
			
		}
	}


	public List<Project> fetchAllProjects() {
		//@formatter:off
		String sql = ""
				+ "SELECT * FROM " + PROJECT_TABLE 
				+ " ORDER BY project_id";
		//@formatter:on
		
		try(Connection conn = DbConnection.getConnection()){
			startTransaction(conn);
			
			try(PreparedStatement stmt = conn.prepareStatement(sql)){
				try(ResultSet rs = stmt.executeQuery(sql)){
					
					List<Project> projects = new LinkedList<Project>();
					
					while(rs.next()) {
						
						projects.add(extract(rs, Project.class));
						
					}
					
					return projects;
					
				}
				
				
			}
			catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);	
			}
		}
		catch(SQLException e) {
			throw new DbException(e);
		}
	}


	public Optional<Project> fetchProjectById(Integer projectId) {
		String sql = "SELECT * FROM " + PROJECT_TABLE + " WHERE project_id = ? ";
		
		try(Connection conn =DbConnection.getConnection()){
			startTransaction(conn);
			
			try{
				Project project = null;
				
				try (PreparedStatement stmt = conn.prepareStatement(sql)){
					setParameter(stmt,1, projectId, Integer.class);
					
					try(ResultSet rs = stmt.executeQuery()){
						
						if(rs.next()) {
							project = extract(rs,Project.class);
						}
						
					}
					
				}
				if(Objects.nonNull(project)) {
					project.getMaterials().addAll(fetchMaterialsForProjectId(conn, projectId));
					project.getSteps().addAll(fetchStepsForProjectId(conn, projectId));
					project.getCategories().addAll(fetchCategoriesForProjectId(conn, projectId));
					
				}
				
				commitTransaction(conn);
				
				return Optional.ofNullable(project);
				
			}
			catch(Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		}
		catch(SQLException e) {
			throw new DbException(e);
		}
	}


	private List<Material> fetchMaterialsForProjectId(Connection conn, Integer projectId) throws SQLException {
		String sql = "SELECT * FROM " + MATERIAL_TABLE + " WHERE project_id = ? ";
		
		try(PreparedStatement stmt = conn.prepareStatement(sql)){
			setParameter(stmt, 1, projectId, Integer.class);
			
			try(ResultSet rs = stmt.executeQuery()){
				List<Material> materials = new LinkedList<Material>();
				
				while(rs.next()) {
					materials.add(extract(rs, Material.class));
				}
				return materials;
			}
			
		}
		
	}


	private List<Step> fetchStepsForProjectId(Connection conn, Integer projectId) throws SQLException {

		String sql = "SELECT * FROM " + STEP_TABLE + " WHERE project_id = ? ";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {

			setParameter(stmt, 1, projectId, Integer.class);

			try (ResultSet rs = stmt.executeQuery()) {
				List<Step> steps = new LinkedList<Step>();

				while (rs.next()) {
					steps.add(extract(rs, Step.class));
				}

				return steps;

			}

		}
	}


	private List<Category> fetchCategoriesForProjectId(Connection conn, Integer projectId) throws SQLException {
		//@formatter:off
		String sql = ""
				+ "SELECT c.* FROM " + CATEGORY_TABLE + " c "
				+ "JOIN " + PROJECT_CATEGORY_TABLE + " pc USING (category_id) "
				+ "WHERE project_id = ? ";
		//@formatter:on
		try(PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);
			
			try(ResultSet rs = stmt.executeQuery()){
				List<Category> categories = new LinkedList<Category>();
				
				while (rs.next()) {
					categories.add(extract(rs, Category.class));
					
				}
				return categories;
			}
		}
		
		
	}

}
