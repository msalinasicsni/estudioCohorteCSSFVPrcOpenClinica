package ni.com.sts.estudioCohorteCSSFV.datos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.log4j.Logger;

import ni.com.sts.estudioCohorteCSSFV.modelo.Paciente;
import ni.com.sts.estudioCohorteCSSFV.servicios.PacienteService;
import ni.com.sts.estudioCohorteCSSFV.util.ConnectionDAO;
import ni.com.sts.estudioCohorteCSSFV.util.UtilLog;
import ni.com.sts.estudioCohorteCSSFV.util.UtilProperty;

public class PacienteDA extends ConnectionDAO implements PacienteService {

	private final Logger logger = Logger.getLogger(this.getClass());
	private Connection conn=null;
	private PreparedStatement  pstm = null;
	private CompositeConfiguration config;
   
	public PacienteDA(){
		config = UtilProperty.getConfigurationfromExternalFile("estudioCohorteCSSFVOPC.properties");
		UtilLog.setLog(config.getString("estudioCohorteCSSFVOPC.log"));
	}

	@Override
	public Paciente getPacienteById(Integer codExpediente) throws Exception {
		Paciente paciente = null;
		conn = getConection();
		ResultSet rs = null;
		try{
			String query = "select cod_expediente, nombre1, nombre2, apellido1, apellido2, sexo, fecha_nac, edad, estudiante, turno, escuela, " +
					"tutor_nombre1, tutor_nombre2, tutor_apellido1, tutor_apellido2, direccion, telefono, telefono2, telefono3, retirado " +
					"from paciente "+
					"where cod_expediente = ? ";
			
			pstm = conn.prepareStatement(query);
			pstm.setInt(1, codExpediente); //hoja_consulta.estado = cerrado

			rs = pstm.executeQuery();
			if (rs.next()){
				paciente = new Paciente();
				paciente.setCodExpediente(rs.getInt("cod_expediente"));
				paciente.setNombre1(rs.getString("nombre1"));
				paciente.setNombre2(rs.getString("nombre2"));
				paciente.setApellido1(rs.getString("apellido1"));
				paciente.setApellido2(rs.getString("apellido2"));
		    	paciente.setSexo(rs.getString("sexo").charAt(0));
		    	paciente.setFechaNac(rs.getDate("fecha_nac"));
		    	paciente.setEdad(rs.getShort("edad"));
		    	paciente.setEstudiante(rs.getString("estudiante") != null ? rs.getString("estudiante").charAt(0) : null);
		    	paciente.setTurno(rs.getString("turno") != null ? rs.getString("turno").charAt(0) : null);
		    	paciente.setEscuela(rs.getShort("escuela"));
		    	paciente.setTutorNombre1(rs.getString("tutor_nombre1"));
		    	paciente.setTutorNombre2(rs.getString("tutor_nombre2"));
		    	paciente.setTutorApellido1(rs.getString("tutor_apellido1"));
		    	paciente.setTutorApellido2(rs.getString("tutor_apellido2"));
		    	paciente.setDireccion(rs.getString("direccion"));
		    	paciente.setTelefono(rs.getString("telefono"));
		    	paciente.setTelefono2(rs.getString("telefono2"));
		    	paciente.setTelefono3(rs.getString("telefono3"));
		    	paciente.setRetirado(rs.getString("retirado").charAt(0));
			}
			
		} catch (Exception e) {
	 		e.printStackTrace();
	 		logger.error("Se ha producido un error al consultar paciente:: PacientesDA" + "\n" + e.getMessage(),e);
	 		throw new Exception(e);
		} finally {
	        try {
	            if (rs != null) {
	                rs.close();
	            }
	            if (pstm != null) {
	                pstm.close();
	            }
	            if (conn != null) {
	                conn.close();
	            }
	
	        } catch (SQLException ex) {
				logger.error(" No se pudo cerrar conexión ", ex);
	        }
	    }
		return paciente;
	}

}
