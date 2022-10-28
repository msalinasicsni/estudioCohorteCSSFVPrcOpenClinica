package ni.com.sts.estudioCohorteCSSFV.thread;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import ni.com.sts.estudioCohorteCSSFV.modelo.HojaInfluenza;
import ni.com.sts.estudioCohorteCSSFV.modelo.HojaZika;
import ni.com.sts.estudioCohorteCSSFV.modelo.Paciente;
import ni.com.sts.estudioCohorteCSSFV.modelo.SeguimientoInfluenza;
import ni.com.sts.estudioCohorteCSSFV.modelo.SeguimientoZika;
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
	public void run() {
		while(true){
			try{
				config = UtilProperty.getConfigurationfromExternalFile("estudioCohorteCSSFVOPC.properties");
				UtilLog.setLog(config.getString("estudioCohorteCSSFVOPC.log"));
				logger.info(this.getName()+" - inicia");
				System.out.println(getFechaHoraActual()+" "+"INICIO DEL PROCESO");
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
					System.out.println(getFechaHoraActual()+" "+"Se duerme main una hora ya se ejecutó el dia de hoy " + ejecucionProcesoHoy.getSecEjecucion());
					this.sleep(3600000);//3600000 si ya se ejecuto se duerme una hora, por si se cambia el parámetro de la hora ejecución se vuelva a ejecutar
					logger.debug("despierta main");
					System.out.println(getFechaHoraActual()+" "+"despierta main");	
					
				}else{
					String valor = parametroService.getParametroByName("HORA_EJECUCION_CAOC");
					if (valor!=null){
						System.out.println(getFechaHoraActual()+" "+"HORA_EJECUCION_CAOC = "+valor);
						Date dFechaHoy = new Date();
						String sFechaHoy = UtilDate.DateToString(dFechaHoy, "dd/MM/yyyy");
						Date dFechaEjecucion = UtilDate.StringToDate2(sFechaHoy+" "+valor, "dd/MM/yyyy HH:mm");
						System.out.println(getFechaHoraActual()+" "+dFechaEjecucion.compareTo(dFechaHoy));
						if (dFechaEjecucion.compareTo(dFechaHoy) < 0){
							
							List<HojaConsulta> hojasPendientesCarga = hojaConsultaService.getHojasConsultaPendientesCarga();
							List<HojaInfluenza> hojasInfluenzaPendientesCarga = hojaConsultaService.getHojasInfluenzaPendientesCarga();
							List<HojaZika> hojasZikaPendientesCarga = hojaConsultaService.getHojasZikaPendientesCarga();
							
							logger.debug("hojasPendientesCarga.size() :: "+hojasPendientesCarga.size());
							System.out.println(getFechaHoraActual()+" "+"hojasPendientesCarga.size() :: "+hojasPendientesCarga.size());
							
							InfoResultado registroProceso = new InfoResultado();
														
							if (hojasPendientesCarga.size() > 0 || hojasInfluenzaPendientesCarga.size() > 0 
									|| hojasZikaPendientesCarga.size() > 0) {
								registroProceso = histEjecucionProcesoService.registrarEjecucionProceso("OPENCLINICA");
								registroProceso.setOk(true);
							}
							
							if (hojasPendientesCarga.size()>0){
								EventScheduleParams eventParams;
								//InfoResultado registroProceso = histEjecucionProcesoService.registrarEjecucionProceso("OPENCLINICA");
								//registroProceso.setOk(true);
								if (registroProceso.isOk()){
									int sec=1;
									ServiciosOpenClinica cliente = new ServiciosOpenClinica();
									for(HojaConsulta hoja:hojasPendientesCarga){
										logger.debug("sec_hoja_consulta :: " +hoja.getSecHojaConsulta());
										System.out.println(getFechaHoraActual()+" "+"sec_hoja_consulta :: " +hoja.getSecHojaConsulta());
										//se consumen webservices
										InfoResultado resultado = new InfoResultado();
										
										if (hoja.getRepeatKey() == null) { // 07/02/2020
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
												hoja.setRepeatKey(resultado.getMensaje()); //07/02/2020
												hojaConsultaService.updateHojaConsultaRepeatKey(hoja); //07/02/2020
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
											sec ++;
										} else {
											resultado = cliente.consumirDataClienteV2(hoja, sec, resultado.getMensaje());
											if (resultado.isOk()){
												//se registra estado carga cerrado
												hoja.setEstadoCarga('1');
												hojaConsultaService.updateHojaConsulta(hoja);
												logger.debug("Hoja de consulta procesada: "+hoja.getSecHojaConsulta());												
											}else{
												logger.error(resultado.getMensaje());
											}
											sec ++;
										}
/*										Paciente paciente = pacienteService.getPacienteById(hoja.getCodExpediente());
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
										sec ++; //secuencia para id hoja en data import */
									}
								} else {
									logger.error(registroProceso.getMensaje());
								}
							}/*else{
								logger.debug("Se duerme main 5 min. No se encontraron hojas de consulta a cargar");
								System.out.println("Se duerme main 5 min. No se encontraron hojas de consulta a cargar");
								this.sleep(300000);//300000 si aún no es hora de ejecutar proceso se duerme 5 minutos
								logger.debug("despierta main");
								System.out.println("despierta main");
							}*/
							
							//-----------------------------------Hoja Influenza------------------------------------------------
							
							logger.debug("hojasInfluenzaPendientesCarga.size() :: "+hojasInfluenzaPendientesCarga.size());
							System.out.println(getFechaHoraActual()+" "+"hojasInfluenzaPendientesCarga.size() :: "+hojasInfluenzaPendientesCarga.size());
							
							// verificamos si hay hojas de influneza pendientes para subir a openClinica
							if (hojasInfluenzaPendientesCarga.size()>0) {
								EventScheduleParams eventParams;
								int sec=1;
								ServiciosOpenClinica cliente = new ServiciosOpenClinica();
								InfoResultado resultado = new InfoResultado();
								for(HojaInfluenza hojaInfluenza:hojasInfluenzaPendientesCarga) {
									
									logger.debug("sec_hoja_influenza :: " +hojaInfluenza.getSecHojaInfluenza());
									System.out.println(getFechaHoraActual()+" "+"sec_hoja_influenza :: " +hojaInfluenza.getSecHojaInfluenza());
									
									List<SeguimientoInfluenza> seguimientoInfluenzas = hojaConsultaService.getSeguimientoInfluenzaBySec(hojaInfluenza.getSecHojaInfluenza());
									
									/* Verificamos si el repeat key de la hoja influenza sea igual a null
									 * si se cumple la condicion se crea el crf completo*/
									if (hojaInfluenza.getRepeatKey() == null) {
										
										//se consumen webservices
										Paciente paciente = pacienteService.getPacienteById(hojaInfluenza.getCodExpediente());
										eventParams = new EventScheduleParams();
										eventParams.setLabel(String.valueOf(paciente.getCodExpediente())); //<label>9803</label>
										eventParams.setEventDefinitionOID(config.getString("event.schedule.eventDefinitionOID.HI"));//<eventDefinitionOID>SE_CONSULTACS</eventDefinitionOID>
										eventParams.setLocation(config.getString("event.schedule.location")); //<location>CS</location>
										eventParams.setIdentifier(config.getString("event.schedule.identifier")); //<identifier>S_1</identifier>
										eventParams.setSiteidentifier(config.getString("event.schedule.site.identifier")); //<identifier></identifier>
										eventParams.setStartDate(hojaInfluenza.getFechaInicio()); //<startDate>2008-12-12</startDate> //<startTime>12:00</startTime>
										if (hojaInfluenza.getFechaCierre()!=null) {
											eventParams.setEndDate(hojaInfluenza.getFechaCierre()); //<endDate>2008-12-12</endDate> //<endTime>15:00</endTime>
										} else {
											eventParams.setEndDate(null);
										}
										
										resultado = cliente.consumirEventCliente(eventParams);
										if (resultado.isOk()) {
											hojaInfluenza.setRepeatKey(resultado.getMensaje());
											hojaConsultaService.updateHojaInfluenzaRepeatKey(hojaInfluenza);
											resultado = cliente.consumirDataHojaInfluenza(hojaInfluenza, seguimientoInfluenzas, sec, resultado.getMensaje());
											if (resultado.isOk()) {
												hojaInfluenza.setEstadoCarga('1');
												hojaConsultaService.updateHojaInfluenza(hojaInfluenza);
												logger.debug("Hoja de influenza procesada: "+hojaInfluenza.getSecHojaInfluenza());	
											} else {
												logger.error(resultado.getMensaje());
											}
										} else{
											logger.error(resultado.getMensaje());
										}
										sec ++;
									} 
									/* Si ya existe el repeat key en la hoja de influenza, nos indica que ya se a creado el crf entonces
									 * solo procedemos a hacer un update para ese repeat key*/
									else { 
										//List<SeguimientoInfluenza> seguimientoInfluenzas = hojaConsultaService.getSeguimientoInfluenzaBySec(hojaInfluenza.getSecHojaInfluenza());
										resultado = cliente.consumirDataHojaInfluenza(hojaInfluenza, seguimientoInfluenzas, sec, resultado.getMensaje());
										if (resultado.isOk()) {
											hojaInfluenza.setEstadoCarga('1');
											hojaConsultaService.updateHojaInfluenza(hojaInfluenza);
											logger.debug("Hoja de influenza procesada: "+hojaInfluenza.getSecHojaInfluenza());	
										} else {
											logger.error(resultado.getMensaje());
										}
										sec ++;
									}
								}
							}
							
							//-----------------------------------Hoja Zika----------------------------------------------------
							
							logger.debug("hojasZikaPendientesCarga.size() :: "+hojasZikaPendientesCarga.size());
							System.out.println(getFechaHoraActual()+" "+"hojasZikaPendientesCarga.size() :: "+hojasZikaPendientesCarga.size());
							
							// verificamos si hay hojas de zika pendientes para subir a openClinica
							if (hojasZikaPendientesCarga.size()>0) {
								EventScheduleParams eventParams;
								int sec=1;
								ServiciosOpenClinica cliente = new ServiciosOpenClinica();
								InfoResultado resultado = new InfoResultado();
								for(HojaZika hojaZika:hojasZikaPendientesCarga) {
									
									logger.debug("sec_hoja_zika :: " +hojaZika.getSecHojaZika());
									System.out.println(getFechaHoraActual()+" "+"sec_hoja_zika :: " +hojaZika.getSecHojaZika());
									
									List<SeguimientoZika> seguimientoZika = hojaConsultaService.getSeguimientoZikaBySec(hojaZika.getSecHojaZika());
									
									/* Verificamos si el repeat key de la hoja zika sea igual a null
									 * si se cumple la condicion se crea el crf completo*/
									if ( hojaZika.getRepeatKey() == null) {
										
										//se consumen webservices
										Paciente paciente = pacienteService.getPacienteById(hojaZika.getCodExpediente());
										eventParams = new EventScheduleParams();
										eventParams.setLabel(String.valueOf(paciente.getCodExpediente())); //<label>9803</label>
										eventParams.setEventDefinitionOID(config.getString("event.schedule.eventDefinitionOID.ZK"));//<eventDefinitionOID>SE_CONSULTACS</eventDefinitionOID>
										eventParams.setLocation(config.getString("event.schedule.location")); //<location>CS</location>
										eventParams.setIdentifier(config.getString("event.schedule.identifier")); //<identifier>S_1</identifier>
										eventParams.setSiteidentifier(config.getString("event.schedule.site.identifier")); //<identifier></identifier>
										eventParams.setStartDate(hojaZika.getFechaInicio()); //<startDate>2008-12-12</startDate> //<startTime>12:00</startTime>
										if (hojaZika.getFechaCierre()!=null) {
											eventParams.setEndDate(hojaZika.getFechaCierre()); //<endDate>2008-12-12</endDate> //<endTime>15:00</endTime>
										} else {
											eventParams.setEndDate(null);
										}
										
										resultado = cliente.consumirEventCliente(eventParams);
										
										if (resultado.isOk()) {
											hojaZika.setRepeatKey(resultado.getMensaje());
											hojaConsultaService.updateHojaZikaRepeatKey(hojaZika);
											resultado = cliente.consumirDataHojaZika(hojaZika, seguimientoZika, sec, resultado.getMensaje());
											if (resultado.isOk()) {
												hojaZika.setEstadoCarga('1');
												hojaConsultaService.updateHojaZika(hojaZika);
												logger.debug("Hoja de zika procesada: "+hojaZika.getSecHojaZika());	
											} else {
												logger.error(resultado.getMensaje());
											}
										} else {
											logger.error(resultado.getMensaje());
										}
										sec ++;
									}
									/* Si ya existe el repeat key en la hoja de influenza, nos indica que ya se a creado el crf entonces
									 * solo procedemos a hacer un update para ese repeat key*/
									else {
										//List<SeguimientoZika> seguimientoZika = hojaConsultaService.getSeguimientoZikaBySec(hojaZika.getSecHojaZika());
										resultado = cliente.consumirDataHojaZika(hojaZika, seguimientoZika, sec, resultado.getMensaje());
										if (resultado.isOk()) {
											hojaZika.setEstadoCarga('1');
											hojaConsultaService.updateHojaZika(hojaZika);
											logger.debug("Hoja de zika procesada: "+hojaZika.getSecHojaZika());	
										} else {
											logger.error(resultado.getMensaje());
										}
										sec ++;
									}
								}
							}
							//------------------------------------------------------------------------------------------------
							if (hojasPendientesCarga.size() <= 0 && hojasInfluenzaPendientesCarga.size() <= 0 
									&& hojasZikaPendientesCarga.size() <= 0) {
								logger.debug("Se duerme main 5 min. No se encontraron hojas de consulta a cargar");
								System.out.println(getFechaHoraActual()+" "+"Se duerme main 5 min. No se encontraron hojas de consulta a cargar");
								this.sleep(300000);//300000 si aún no es hora de ejecutar proceso se duerme 5 minutos
								logger.debug("despierta main");
								System.out.println(getFechaHoraActual()+" "+"despierta main");
							}
							/*-------------------------------------------------------------------------------------------------*/
							
						 } else {
							logger.debug("Se duerme main 5 min");
							System.out.println(getFechaHoraActual()+" "+"Se duerme main 5 min");
							this.sleep(300000);//300000 si aún no es hora de ejecutar proceso se duerme 5 minutos
							logger.debug("despierta main");
							System.out.println(getFechaHoraActual()+" "+"despierta main");
						} 
					} else {
						logger.debug("Se duerme main 5 min. No se encontró valor de parámetro HORA_EJECUCION_CAOC");
						System.out.println(getFechaHoraActual()+" "+"Se duerme main 5 min. No se encontró valor de parámetro HORA_EJECUCION_CAOC");
						this.sleep(300000);//300000 si aún no es hora de ejecutar proceso se duerme 5 minutos
						logger.debug("despierta main");
						System.out.println(getFechaHoraActual()+" "+"despierta main");
					}
				}	
			}catch(Exception ex){
				logger.error("Ha ocurrido un error en la ejecución del hilo "+this.getName());
				logger.error(ex);
			}finally{
				logger.info(this.getName()+" - finalizado");
				System.out.println(getFechaHoraActual()+" "+"FIN DEL PROCESO");
			}
		}
	}
	
	public String getFechaHoraActual() {
		String fecha = null;
		try {
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			Date date = new Date();
			
			fecha = dateFormat.format(date);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return fecha;
	}
}
