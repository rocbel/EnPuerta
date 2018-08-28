package com.puerta18.enclase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.puerta18.model.Socio;

@Controller
public class SociosController {

	@Autowired
	private Environment env;

	// landing page, muestra un formulario de busqueda 
	// y tambien muestra los resultados con un parametro no requerido 
	@GetMapping("/")
		public String landing(@RequestParam (required=false) String palabraClave ,Model template, @ModelAttribute("some") String some) throws SQLException { // required es para que a veces esta o no

			if(palabraClave == null || palabraClave.equals("")) {
				 // me devuelve un string vacio
				return "index";
			}
			
			
			Connection connection;
			
			connection = DriverManager.getConnection(
					env.getProperty("spring.datasource.url"),
					env.getProperty("spring.datasource.username"),
					env.getProperty("spring.datasource.password"));
			
			PreparedStatement consulta=				
		
					connection.prepareStatement("SELECT * FROM socios WHERE unaccent (lower (nombre)) LIKE ? OR  (lower (apellido)) LIKE ? OR dni = ? ORDER BY nombre "); 
			
			 consulta.setString (1, "%" + palabraClave + "%" ); 
			 consulta.setString (2, "%" + palabraClave + "%" );
			 consulta.setString (3, palabraClave);
			
			
			
			

			ResultSet resultados = consulta.executeQuery (); 
			
			ArrayList<Socio> losSocios = new ArrayList<Socio> ();												
			
			
			while ( resultados.next()  ) { //ciclo
				
				int id = resultados.getInt ("id");
				String nombre = resultados.getString("nombre");
				String apellido = resultados.getString("apellido");
				String dni = resultados.getString("dni"); 
				String email = resultados.getString ("email");
				boolean presente = resultados.getBoolean ("presente");
				Socio elSocio = new Socio (id,nombre,apellido,dni,email,presente); 
				
				losSocios.add(elSocio);
							
			} 

			template.addAttribute("socios", losSocios);
			
			connection.close();
			
						
			System.out.println("some=" + some);
			return "index";
		}

	
	@GetMapping("/socios/nuevo") // formulario de alta vacio
	public String nuevo() {
		return "";
	}
	
	@GetMapping("/socios/nuevo/procesar") // inserta nuevos socios
	public String insertarNuevo() {
		return "";
	}
	
	@GetMapping("/socios/checkin/{id}") // 
	public String socioIN(@PathVariable int id, RedirectAttributes redirectAttrs) throws SQLException {
		
			Connection connection; // Usar el import de java.sql
		
			connection = DriverManager.getConnection(
				env.getProperty("spring.datasource.url"),
				env.getProperty("spring.datasource.username"),
				env.getProperty("spring.datasource.password"));
		
			PreparedStatement consulta = 
				connection.prepareStatement("UPDATE  socios SET presente = true  WHERE id = ? "); // UPDATE cambia informacion
				
		consulta.setInt( 1, id );

		consulta.execute ();
		
		PreparedStatement consulta2 = 
				connection.prepareStatement("INSERT INTO checks (id_socio,momento,tipo) VALUES (?,NOW(),'in') "); // UPDATE cambia informacion
				
		consulta2.setInt( 1, id );

		consulta2.execute ();
		
					connection.close();
					
					
		redirectAttrs.addFlashAttribute("some", "thing");			
		
		return "redirect:/";
		
		
	}
	
	@GetMapping("/socios/checkout/{id}") // 
	public String socioout(@PathVariable int id, RedirectAttributes redirectAttrs) throws SQLException {
		
		Connection connection; // Usar el import de java.sql
	
		connection = DriverManager.getConnection(
			env.getProperty("spring.datasource.url"),
			env.getProperty("spring.datasource.username"),
			env.getProperty("spring.datasource.password"));
	
		PreparedStatement consulta = 
			connection.prepareStatement("UPDATE  socios SET presente = false  WHERE id = ? "); // UPDATE cambia informacion
			
	consulta.setInt( 1, id );

	consulta.execute ();
	
	PreparedStatement consulta2 = 
			connection.prepareStatement("INSERT INTO checks (id_socio,momento,tipo) VALUES (?,NOW(),'out') "); // UPDATE cambia informacion
			
	consulta2.setInt( 1, id );

	consulta2.execute ();
	
				
	
				connection.close();
				
				redirectAttrs.addFlashAttribute("some", "thing");		
	
	return "redirect:/";
	
	
}
	
	// estas rutas mas adelante vamos a protegerlas con usuario y contraseña
	// @GetMapping("/socios/mostrar/{id}") // muestra el detalle completo de un socio
	// @GetMapping("/socios/listado")      // muestra el listado completo sin paginacion, por ahora
	
	// @GetMapping("/socios/modificar/{id}")
	// @GetMapping("/socios/modificar/procesar/{id}")
}
