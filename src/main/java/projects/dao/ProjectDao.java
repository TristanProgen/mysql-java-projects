package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import projects.entity.Project;
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
		 * 1) get a connection 
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

}
