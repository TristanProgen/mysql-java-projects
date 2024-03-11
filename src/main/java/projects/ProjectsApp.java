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
	
	private ProjectService projectService = new ProjectService();
	
	private Project curProject;
	
	
	//@formatter:off
	private List<String> operations =  List.of(
			"1) Add a project",
			"2) List projects",
			"3) Select a project"
		
			
			
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
				default:
					System.out.println("\n" + selection + " is not a valid selection. Please try again.");
					
				}
				
			}
			catch(Exception e) {
				System.out.println("\nError: " + e + " Try again.");
			}
			
			
		}
		
		
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
