package ni.com.sts.estudioCohorteCSSFV.servicios;

import java.util.List;

import ni.com.sts.estudioCohorteCSSFV.modelo.HojaConsulta;
import ni.com.sts.estudioCohorteCSSFV.modelo.HojaInfluenza;
import ni.com.sts.estudioCohorteCSSFV.modelo.HojaZika;
import ni.com.sts.estudioCohorteCSSFV.modelo.SeguimientoInfluenza;
import ni.com.sts.estudioCohorteCSSFV.modelo.SeguimientoZika;

public interface HojaConsultaService {

	public List<HojaConsulta> getHojasConsultaPendientesCarga() throws Exception;
	
	public void updateHojaConsulta(HojaConsulta hoja) throws Exception;
	
	// hoja influenza
	public List<HojaInfluenza> getHojasInfluenzaPendientesCarga() throws Exception;
	
	public List<SeguimientoInfluenza> getSeguimientoInfluenzaBySec(int secHojaInfluenza) throws Exception;
	
	public void updateHojaInfluenza(HojaInfluenza hojaInfluenza) throws Exception;
	
	public void updateHojaInfluenzaRepeatKey(HojaInfluenza hojaInfluenza) throws Exception;

	// hoja zika
	public List<SeguimientoZika> getSeguimientoZikaBySec(int secHojaZika) throws Exception;

	public List<HojaZika> getHojasZikaPendientesCarga() throws Exception;
	
	public void updateHojaZika(HojaZika hojaZika) throws Exception;
	
	public void updateHojaZikaRepeatKey(HojaZika hojaZika) throws Exception;
}
