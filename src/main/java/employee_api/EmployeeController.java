package employee_api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
	
	private final EmployeeRepository repository; 
	
	public EmployeeController (EmployeeRepository repository) {
		this.repository = repository;
	}
	
	@GetMapping
	public List<Employee> getAllEmployees(){
		return repository.findAll();
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Employee createEmployee(@RequestBody Employee employee) {
		return repository.save(employee);
	}
	
	@GetMapping("/{id}")
	public Employee getEmployee(@PathVariable Long id) {
		return repository.findById(id).orElseThrow(
				()-> new RuntimeException("Employee not found"));
	}
}
