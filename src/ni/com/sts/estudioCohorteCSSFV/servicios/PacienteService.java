package ni.com.sts.estudioCohorteCSSFV.servicios;

import ni.com.sts.estudioCohorteCSSFV.modelo.Paciente;

public interface PacienteService  {

	public Paciente getPacienteById(Integer codExpediente) throws Exception;
	
}
