package ni.com.sts.estudioCohorteCSSFV.thread;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.log4j.Logger;
import org.openclinica.ws.beans.GenderType;


import ni.com.sts.estudioCohorteCSSFV.datos.HistEjecucionProcesoDA;
import ni.com.sts.estudioCohorteCSSFV.datos.HojaConsultaDA;
import ni.com.sts.estudioCohorteCSSFV.datos.PacienteDA;
import ni.com.sts.estudioCohorteCSSFV.datos.ParametrosDA;
import ni.com.sts.estudioCohorteCSSFV.modelo.HistEjecucionProcesoAutomatico;
import ni.com.sts.estudioCohorteCSSFV.modelo.HojaConsulta;
import ni.com.sts.estudioCohorteCSSFV.modelo.Paciente;
import ni.com.sts.estudioCohorteCSSFV.openClinica.EventScheduleParams;
import ni.com.sts.estudioCohorteCSSFV.openClinica.ServiciosOpenClinica;
import ni.com.sts.estudioCohorteCSSFV.servicios.HistEjecucionProcesoService;
import ni.com.sts.estudioCohorteCSSFV.servicios.HojaConsultaService;
import ni.com.sts.estudioCohorteCSSFV.servicios.PacienteService;
import ni.com.sts.estudioCohorteCSSFV.servicios.ParametroService;
import ni.com.sts.estudioCohorteCSSFV.util.InfoResultado;
import ni.com.sts.estudioCohorteCSSFV.util.StackTraceUtl;
import ni.com.sts.estudioCohorteCSSFV.util.UtilDate;
import ni.com.sts.estudioCohorteCSSFV.util.UtilLog;
import ni.com.sts.estudioCohorteCSSFV.util.UtilProperty;

public class CargaAutomaticaOpenClinicaThread extends Thread {
	private final Logger logger = Logger.getLogger(this.getClass());
	private CompositeConfiguration config;
	private ParametroService parametroService = new ParametrosDA();
	private HojaConsultaService hojaConsultaService = new HojaConsultaDA();
	private HistEjecucionProcesoService histEjecucionProcesoService = new HistEjecucionProcesoDA();
	private PacienteService pacienteService = new PacienteDA();
	
	@SuppressWarnings("static-access")
	public void run(){
		while(true){
			try{
				config = UtilProperty.getConfigurationfromExternalFile("estudioCohorteCSSFVOPC.properties");
				UtilLog.setLog(config.getString("estudioCohorteCSSFVOPC.log"));
				logger.info(this.getName()+" - inicia");			
				//consultar si no se a ejecutado proceso el dia de hoy,si ya se ejecuto se duerme 1 hora
				//obtener parámetro con hora de ejecución
				//validar si ya es hora de ejecutar proceso
				//Si se ejecuta proceso registrar inicio de proceso, sino dormir 5 minutos
				//consultar hojas de consulta a procesar
				// por cada hoja procesada actualizar registro
				//registrar fin de proceso			
				HistEjecucionProcesoAutomatico ejecucionProcesoHoy = histEjecucionProcesoService.getEjecucionProcesoFechaHoy("OPENCLINICA");
				if (ejecucionProcesoHoy!=null){
					logger.debug("Se duerme main una hora ya se ejecutó el dia de hoy");
					System.out.println("Se duerme main una hora ya se ejecutó el dia de hoy " + ejecucionProcesoHoy.getSecEjecucion());
					this.sleep(3600000);//3600000 si ya se ejecuto se duerme una hora, por si se cambia el parámetro de la hora ejecución se vuelva a ejecutar
					logger.debug("despierta main");
					System.out.println("despierta main");				
				}else{
					String valor = parametroService.getParametroByName("HORA_EJECUCION_CAOC");
					if (valor!=null){
						System.out.println("HORA_EJECUCION_CAOC = "+valor);
						Date dFechaHoy = new Date();
						String sFechaHoy = UtilDate.DateToString(dFechaHoy, "dd/MM/yyyy");
						Date dFechaEjecucion = UtilDate.StringToDate(sFechaHoy+" "+valor, "dd/MM/yyyy HH:mm");
						System.out.println(dFechaEjecucion.compareTo(dFechaHoy));
						if (dFechaEjecucion.compareTo(dFechaHoy) < 0){
							List<HojaConsulta> hojasPendientesCarga = hojaConsultaService.getHojasConsultaPendientesCarga();
							logger.debug("hojasPendientesCarga.size() :: "+hojasPendientesCarga.size());
							System.out.println("hojasPendientesCarga.size() :: "+hojasPendientesCarga.size());
							if (hojasPendientesCarga.size()>0){
								EventScheduleParams eventParams;
								InfoResultado registroProceso = histEjecucionProcesoService.registrarEjecucionProceso("OPENCLINICA");
								registroProceso.setOk(true);
								if (registroProceso.isOk()){
									int sec=1;
									ServiciosOpenClinica cliente = new ServiciosOpenClinica();
									for(HojaConsulta hoja:hojasPendientesCarga){
										logger.debug("sec_hoja_consulta :: " +hoja.getSecHojaConsulta());
										System.out.println("sec_hoja_consulta :: " +hoja.getSecHojaConsulta());
										//se consumen webservices
										InfoResultado resultado = new InfoResultado();
										Paciente paciente = pacienteService.getPacienteById(hoja.getCodExpediente());
										eventParams = new EventScheduleParams();
										eventParams.setLabel(String.valueOf(paciente.getCodExpediente())); //<label>9803</label>
										eventParams.setEventDefinitionOID(config.getString("event.schedule.eventDefinitionOID"));//<eventDefinitionOID>SE_CONSULTACS</eventDefinitionOID>
										eventParams.setLocation(config.getString("event.schedule.location")); //<location>CS</location>
										eventParams.setIdentifier(config.getString("event.schedule.identifier")); //<identifier>S_1</identifier>
										eventParams.setSiteidentifier(config.getString("event.schedule.site.identifier")); //<identifier></identifier>
										eventParams.setStartDate(hoja.getFechaConsulta()); //<startDate>2008-12-12</startDate> //<startTime>12:00</startTime>
										if (hoja.getFechaCierre()!=null){
											eventParams.setEndDate(hoja.getFechaCierre()); //<endDate>2008-12-12</endDate> //<endTime>15:00</endTime>
										}else {
											eventParams.setEndDate(null);
										}
										resultado = cliente.consumirEventCliente(eventParams);
										if (resultado.isOk()){
											resultado = cliente.consumirDataClienteV2(hoja, sec, resultado.getMensaje());
											if (resultado.isOk()){
												//se registra estado carga cerrado
												hoja.setEstadoCarga('1');
												hojaConsultaService.updateHojaConsulta(hoja);
												logger.debug("Hoja de consulta procesada: "+hoja.getSecHojaConsulta());												
											}else{
												logger.error(resultado.getMensaje());
											}
										}else{
											logger.error(resultado.getMensaje());
										}
										sec ++; //secuencia para id hoja en data import
									}
								}else{
									logger.error(registroProceso.getMensaje());
								}
							}else{
								logger.debug("Se duerme main 5 min. No se encontraron hojas de consulta a cargar");
								System.out.println("Se duerme main 5 min. No se encontraron hojas de consulta a cargar");
								this.sleep(300000);//300000 si aún no es hora de ejecutar proceso se duerme 5 minutos
								logger.debug("despierta main");
								System.out.println("despierta main");
							}					
							
						 }else{
							logger.debug("Se duerme main 5 min");
							System.out.println("Se duerme main 5 min");
							this.sleep(300000);//300000 si aún no es hora de ejecutar proceso se duerme 5 minutos
							logger.debug("despierta main");
							System.out.println("despierta main");
						} 
					}else{
						logger.debug("Se duerme main 5 min. No se encontró valor de parámetro HORA_EJECUCION_CAOC");
						System.out.println("Se duerme main 5 min. No se encontró valor de parámetro HORA_EJECUCION_CAOC");
						this.sleep(300000);//300000 si aún no es hora de ejecutar proceso se duerme 5 minutos
						logger.debug("despierta main");
						System.out.println("despierta main");
					}
				}	
			}catch(Exception ex){
				logger.error("Ha ocurrido un error en la ejecución del hilo "+this.getName());
				logger.error(ex);
			}finally{
				logger.info(this.getName()+" - finalizado");
			}
		}
	}	
}
