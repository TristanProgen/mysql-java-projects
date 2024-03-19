package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp {
	
	// Object to take user input for the app's 
	private Scanner scanner = new Scanner(System.in);
	// Object to pass data methods to the DAO layer
	private ProjectService projectService = new ProjectService();
	// Object to represent the current project selected by the user 
	private Project curProject;
	
	
	//@formatter:off
	private List<String> operations =  List.of(
			"1) Add a project",
			"2) List projects",
			"3) Select a project",
			"4) Update project details",
			"5) Delete a Project"
			
		
			);
	//@formatter:on

	public static void main(String[] args) {
		new ProjectsApp().processUserSelections();
		
		
		

	}
	
	
	private void processUserSelections() {
		boolean done = false;
		
		while(!done) {
			try {
				int selection = getUserSelection();
				
				switch(selection) {
				
				case -1:
					done = exitMenue();
					break;
				case 1:
					createProject();
					break;
				case 2:
					listProjects();
					break;
				case 3:
					selectProject();
					break;
					
				case 4:
					updateProjectDetails();
					break;
					
				case 5:
					deleteProject();
					break;
					
				default:
					System.out.println("\n" + selection + " is not a valid selection. Please try again.");
					
				}
				
			}
			catch(Exception e) {
				System.out.println("\nError: " + e + " Try again.");
			}
			
			
		}
		
		
	}
	
	private void deleteProject() {
		
		
		listProjects();
		
		Integer projectId = getIntInput("Enter the id of the project you wiish to delete");
		
		
		projectService.deleteProject(projectId);
		
		System.out.println("Project " + projectId + " was sucesfully deleted.");
		
		if(Objects.nonNull(curProject) && curProject.getProjectId().equals(projectId)) {
			curProject = null;
			
		}
		
	}


	private void updateProjectDetails() {
		
		
		// Checking if we have a project selected 
		
		if(Objects.isNull(curProject)) {
			
		System.out.println("\n Please secect a current project first");
		
		return;
		
		}
		
		// Updating the currently selected project 
		
		System.out.println("\n\n******************** Updatating Project Details ********************\n");
		
		Project project = new Project();
		
		
		
		//@formatter:off
		String projectName = getStringInput(
				"\nCurrent Project Name: "
				+ "[" + curProject.getProjectName() + "]" 
	            + "\nPress \"ENTER\" to keep the current value the same" 
				+ "\n\t\t\t  OR   "
	            + "\nPlease input the new project name followed by the \"ENTER\" key");
		
		
		BigDecimal estimatedHours = getDecimalInput(
				"\n\nCurrent estimated hours: " 
				+ "[" + curProject.getEstimatedHours() + "]"
				+ "\nPress \"ENTER\" key to keep the current value the same" 
				+ "\n\t\t\t  OR   "
	            + "\nPlease input the new estimated hours followed by the \"ENTER\" key");
		
		BigDecimal actualHours = getDecimalInput(
				"\n\nCurrent actual hours: "
				+ "[" + curProject.getActualHours() + "]"
				+ "\nPress \"ENTER\" key to keep the current value the same" 
				+ "\n\t\t\t  OR   "
			    + "\nPlease input the new actual hours followed by the \"ENTER\" key");
		
		Integer difficulty = validateDifficultyInput(
				"\n\nCurrent project difficutly : "
				+ "[" + curProject.getDifficulty() + "]"
				+ "\n Please input the new project difficulty (1-5)"
				);
		
		String notes = getStringInput(
				"\n\nCurrent Notes: "
				+ "[" + curProject.getNotes() + "]"
				+ "\nPress \"ENTER\" key to keep the current value the same" 
				+ "\n\t\t\t  OR   "
				+ "\nPlease input the new Notes followed by the \"ENTER\" key" 
				+ "\n" + "\n");
		
		//@formatter:on
		
		project.setProjectId(curProject.getProjectId());
		
		project.setProjectName(
				Objects.isNull(projectName) ? curProject.getProjectName() : projectName
				);
		
		project.setEstimatedHours(
				Objects.isNull(estimatedHours) ? curProject.getEstimatedHours() : estimatedHours);
		
		project.setActualHours(
				Objects.isNull(actualHours) ? curProject.getActualHours() : actualHours);
		
		project.setDifficulty(
				Objects.isNull(difficulty) ? curProject.getDifficulty() : difficulty);
		
		project.setNotes(Objects.isNull(notes) ? curProject.getNotes() : notes);
		
		
		projectService.modifyProjectDetails(project);
		
		curProject = projectService.fetchProjectById(curProject.getProjectId());
		
		
	}


	private void selectProject() {
		listProjects();
		Integer projectId = getIntInput("Enter a project ID to select a project");
		
		curProject = null;
		
		curProject = projectService.fetchProjectById(projectId);
		
		if(Objects.isNull(curProject)) {
			System.out.println("Invalid Project Id Selected");
		}
		
	}


	private void listProjects() {
		curProject = null;
		List<Project> projects = projectService.fetchAllProjects();
		
		System.out.println("\nProjects:");
		
		projects.forEach(project -> System.out.println("\t" + project.getProjectId()
							+ " : " + project.getProjectName()));
		
	}


	private void createProject() {
		String projectName = getStringInput("Enter the project name");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours");
		Integer difficulty = validateDifficultyInput("Enter the project difficulty (1-5)"); 
		String notes = getStringInput("Enter the project notes");
		
		Project project = new Project();
		
		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);
		
		Project dbProject = projectService.addProject(project);
		System.out.println("You have sucesfully created project: " + dbProject);
		
	}
	
	// check for not null not a char and is an int between 1 and 5 inclusive 
	private Integer validateDifficultyInput(String prompt) {
		String input = getStringInput(prompt);
		boolean intTest = isInteger(input);
		
		

		while(Objects.isNull(input)
				|| intTest == false
				|| Integer.valueOf(input) < 1 
				|| Integer.valueOf(input) > 5
				
				 ) {
			
			System.out.println("Please enter a valid difficulty");
			input = getStringInput(prompt);
			intTest = isInteger(input);
			
		}
		
		return Integer.valueOf(input);
		
			
	}

	private boolean isInteger(String input) {
		if( Objects.isNull(input)) {
			return false;
		}
		else {
			for (int i = 0; i < input.length(); i++) {
		        if (!Character.isDigit(input.charAt(i))) {
		            return false;
		        }
		    }
		    return true;
		}
	}
	
	
	private boolean exitMenue() {
		System.out.println("Exiting the Menue");
		return true;
	}
	
	
	private int getUserSelection() {
		printOperations();
		
		Integer input = getIntInput("Enter a menue selection");
		
		return Objects.isNull(input) ? -1 : input;
	}
	
	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);
		if(Objects.isNull(input)) {
			return null;
			
		}
		
		try {
			return Integer.valueOf(input);
		}
		catch(NumberFormatException e) {
			throw new DbException(input + " is not a valid number.");
			
		}
		
	}
	
	private BigDecimal getDecimalInput(String prompt) {
		String input = getStringInput(prompt);
		if(Objects.isNull(input)) {
			return null;
			
		}
		
		try {
			return new BigDecimal(input).setScale(2);
		}
		catch(NumberFormatException e) {
			throw new DbException(input + "is not a valid decimal number.");
			
		}
		
	}
	
	private String getStringInput(String prompt) {
		System.out.print(prompt +": ");
		String input = scanner.nextLine();
		
		return input.isBlank() ? null : input.trim();
		
	}
	private void printOperations() {
		
		System.out.println("\nTheses are the available selections. Press the Enter key to quit:");
		
		operations.forEach(line -> System.out.println(line));
		
		if(Objects.isNull(curProject)) {
			System.out.println("\n You are not working with a project.");			
		}
		else {
			System.out.println("\n You are workign with project: " + curProject);
		}
		
	}

}
