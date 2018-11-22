import ni.com.sts.estudioCohorteCSSFV.thread.CargaAutomaticaOpenClinicaThread;

public class CargaAutomaticaOpenClinica {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ejecutarProceso();
	}
	
	public static void ejecutarProceso(){
		CargaAutomaticaOpenClinicaThread procesoCarga = new CargaAutomaticaOpenClinicaThread();
		procesoCarga.setName("ProcesoCargaAutomaticaOpenClinica");
		procesoCarga.start();
	}

}
