package ni.com.sts.estudioCohorteCSSFV.datos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.log4j.Logger;

import ni.com.sts.estudioCohorteCSSFV.modelo.UsuariosView;
import ni.com.sts.estudioCohorteCSSFV.servicios.UsuariosService;
import ni.com.sts.estudioCohorteCSSFV.util.ConnectionDAO;
import ni.com.sts.estudioCohorteCSSFV.util.UtilLog;
import ni.com.sts.estudioCohorteCSSFV.util.UtilProperty;

public class UsuariosDA extends ConnectionDAO implements UsuariosService {

	private final Logger logger = Logger.getLogger(this.getClass());
	private Connection conn=null;
	private PreparedStatement  pstm = null;
	private CompositeConfiguration config;
   
	public UsuariosDA(){
		config = UtilProperty.getConfigurationfromExternalFile("estudioCohorteCSSFVOPC.properties");
		UtilLog.setLog(config.getString("estudioCohorteCSSFVOPC.log"));
	}

	@Override
	public UsuariosView obtenerUsuarioById(Integer id) throws Exception {
		UsuariosView usuario = null;
		conn = getConection();
		ResultSet rs = null;
		try{
			String query = "select id, nombre, usuario, codigopersonal from usuarios_view where id = ? ";
			
			pstm = conn.prepareStatement(query);
			pstm.setInt(1, id); //id usuario

			rs = pstm.executeQuery();
			if (rs.next()){
				usuario = new UsuariosView();
				usuario.setId(rs.getInt("id"));
				usuario.setCodigoPersonal(rs.getString("codigoPersonal"));
				usuario.setNombre(rs.getString("nombre"));
				usuario.setUsuario(rs.getString("usuario"));
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
		return usuario;
	}

}
