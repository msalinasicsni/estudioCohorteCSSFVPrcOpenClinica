package ni.com.sts.estudioCohorteCSSFV.servicios;

import java.util.List;

import ni.com.sts.estudioCohorteCSSFV.modelo.HojaConsulta;

public interface HojaConsultaService {

	public List<HojaConsulta> getHojasConsultaPendientesCarga() throws Exception;
	
	public void updateHojaConsulta(HojaConsulta hoja) throws Exception;
}
