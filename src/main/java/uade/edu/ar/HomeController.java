package uade.edu.ar;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import controlador.Controlador;
import exceptions.ReclamoException;
import exceptions.UsuarioException;
import views.EdificioView;
import views.PersonaView;
import views.ReclamoView;
import views.UsuarioView;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		return "home";
	}
	
	/** OK */
	//http://websystique.com/java/json/jackson-convert-java-map-to-from-json/
	@RequestMapping(value = "/VerReclamo", method = RequestMethod.GET)
	@ResponseBody
	public String verReclamo(@RequestParam(value="id", required=true) int id) throws ReclamoException {
		try {
			String response;
			String aux;
			ObjectMapper mapper = new ObjectMapper();
			HashMap<String, String> hash_map = new HashMap<String, String>();
			ReclamoView rv = Controlador.getInstancia().buscarReclamo(id);
			EdificioView ev = Controlador.getInstancia().buscarEdificioReclamo(rv.getCodigoEdificio());
			PersonaView pv = Controlador.getInstancia().buscarPersonaReclamo(rv.getDocumentoPersona());
			hash_map.put("edificio", ev.getNombre());
			hash_map.put("persona", pv.getNombre());
			response = mapper.writeValueAsString(rv); 
			response = response.substring(0, response.length() - 1);
			aux = mapper.writeValueAsString(hash_map);
			aux = aux.substring(1, aux.length());
			response += "," + aux; 
			return response;
		} catch (Exception e) {
			throw new ReclamoException("No se pudo recuperar el Reclamo");
		}
	}
	
	/** OK */
	@RequestMapping(value = "/VerReclamos", method = RequestMethod.GET)
	@ResponseBody
	public String verReclamos(@RequestParam(value="id", required=true) int id) throws ReclamoException {
		try {
			ObjectMapper mapper = new ObjectMapper();			
			List<ReclamoView> reclamos = new ArrayList<ReclamoView>();
			reclamos = Controlador.getInstancia().buscarReclamosAsociados(id);
			return mapper.writeValueAsString(reclamos);
		} catch (Exception e) {
			throw new ReclamoException("No se pudo recuperar los Reclamos asociados");
		}
	}
	
	/** TO DELETE */
	@RequestMapping(value = "/VerTodosLosReclamos", method = RequestMethod.GET, headers = "Accept=application/json")
	@ResponseBody
	public String verTodosLosReclamos() throws ReclamoException {
		try {
			ObjectMapper mapper = new ObjectMapper();
			List<ReclamoView> reclamos = new ArrayList<ReclamoView>();
			
			reclamos = Controlador.getInstancia().buscarTodosLosReclamos();
			
			return mapper.writeValueAsString(reclamos);
		} catch (Exception e) {
			throw new ReclamoException("No se pudo recuperar los Reclamos asociados");
		}
	}
	
	/** TO DO */
	@RequestMapping(value = "/AltaReclamo", method = RequestMethod.POST, headers = "Accept=application/json")
	@ResponseBody
	public String altaReclamo(@RequestBody String reclamoJson) throws ReclamoException {
		try {
			ReclamoView rw = new ReclamoView();
			ObjectMapper mapper = new ObjectMapper();
			rw = mapper.readValue(reclamoJson, ReclamoView.class);
			try {
				return mapper.writeValueAsString(Controlador.getInstancia().crearReclamo(rw));
			} catch (ReclamoException e) {
				throw new ReclamoException("No se pudo guardar el Reclamo");
			}
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ""; //Cambiar por devolver el id de reclamo
	}
	
	/*@RequestMapping(value = "/ActualizarEstadoReclamo", method = RequestMethod.GET)
	public String reclamo(Locale locale, Model model) {
		
	}*/
	
	@RequestMapping(value = "/AltaUsuario", method = RequestMethod.POST, headers = "Accept=application/json")
	@ResponseBody
	public String altaUsuario(@RequestBody String usuarioJson) throws UsuarioException {
		try {
			UsuarioView uw = new UsuarioView();
			ObjectMapper mapper = new ObjectMapper();
			uw = mapper.readValue(usuarioJson, UsuarioView.class);
			try {
				return mapper.writeValueAsString(Controlador.getInstancia().registrarUsuario(uw));
			} catch (Exception e) {
				throw new UsuarioException("No se pudo registrar el Usuario");
			}
		} catch (Exception e) {
			throw new UsuarioException("No se pudo registrar el Usuario");
		}
	}
	
	@RequestMapping(value = "/AutenticarUsuario", method = RequestMethod.POST, headers = "Accept=application/json")
	@ResponseBody
	public String autenticacionUsuario(@RequestBody String usuarioJson) throws UsuarioException {
		try {
			UsuarioView uw = new UsuarioView();
			ObjectMapper mapper = new ObjectMapper();
			uw = mapper.readValue(usuarioJson, UsuarioView.class);
			try {
				return mapper.writeValueAsString(Controlador.getInstancia().autenticarUsuario(uw));
			} catch (Exception e) {
				throw new UsuarioException("No se pudo autenticar el Usuario");
			}
		} catch (Exception e) {
			throw new UsuarioException("No se pudo autenticar el Usuario");
		}
	}
}
